package edu.sjsu.cs249.chain.service;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;

import edu.sjsu.cs249.chain.AckRequest;
import edu.sjsu.cs249.chain.AckResponse;
import edu.sjsu.cs249.chain.ReplicaGrpc.ReplicaBlockingStub;
import edu.sjsu.cs249.chain.ReplicaGrpc.ReplicaFutureStub;
import edu.sjsu.cs249.chain.ReplicaGrpc.ReplicaImplBase;
import edu.sjsu.cs249.chain.StateTransferRequest;
import edu.sjsu.cs249.chain.StateTransferResponse;
import edu.sjsu.cs249.chain.UpdateRequest;
import edu.sjsu.cs249.chain.UpdateResponse;
import edu.sjsu.cs249.chain.replica.ReplicaState;
import io.grpc.stub.StreamObserver;

public class ChainReplicaService extends ReplicaImplBase {
	/**
	 * service Replica {
    rpc update(UpdateRequest) returns (UpdateResponse);
    rpc stateTransfer(StateTransferRequest) returns (StateTransferResponse);
    rpc ack(AckRequest) returns (AckResponse);
	}
	
	message UpdateRequest {
	    string key = 1;
	    int32 newValue = 2;
	    uint32 xid = 3;
	}
	message UpdateResponse {
	}
	message StateTransferRequest {
	    map<string, uint32> state = 1;
	    uint32 xid = 2;
	    repeated UpdateRequest sent = 3;
	}
	message StateTransferResponse {
	}
	message AckRequest {
	    uint32 xid = 1;
	}
	message AckResponse {
	}
	 **/
	private static final Logger logger = Logger.getLogger("ChainReplicaService");
	ReplicaState state;
	

	public ChainReplicaService(ReplicaState state) {
		this.state = state;
	}

	@Override
	public void update(UpdateRequest request, StreamObserver<UpdateResponse> responseObserver) {
		String key = request.getKey();
		int newVal = request.getNewValue();
		int rXid = request.getXid();
		logger.info(String.format("Recieved UpdateRequest : [ xid : %d , key : %s , newValue: %d ]", rXid, key, newVal));
		
		try
		{
			//TODO Move this wait logic to a different function.
			if(state.sendingState.get())
			{
				logger.info("I am currently transfering my state, waiting for state transfer to complete.");
				//TODO: Wait to be notified..when state transfer completes.
				synchronized (state.sendingStateObj) {
					state.sendingStateObj.wait();
				}
				logger.info("Outward State Transfer completed, continuing with the update..");
			}
			
			if(state.recievingState.get())
			{
				logger.info("Inbound State Transfer in Progress, waiting for it  to complete.");
				//TODO: Wait to be notified..when state transfer completes.
				synchronized (state.recievingStateObj) {
					state.recievingStateObj.wait();
				}
				logger.info("Inward State Transfer completed, continuing with the update.");
			}
		}
		catch(InterruptedException ie)
		{
			ie.printStackTrace();
		}
		int currXid = 0;
		state.xidLock.lock();
		if(state.xid == null) state.xid = currXid;
		currXid = state.xid;
		//I assume that i recieve Xid's only in the increasing order,
		 
		if( currXid +1 == rXid) 
		{
			logger.info("Update with xid :" + rXid + " can be applied..on the hastable");
			currXid = rXid;
			state.xid = currXid;
			state.hashTable.put(key, newVal);
			logger.info("Update applied on hastable");
			//Updating the xid and updtaing the map should be atomic.
		}
		state.xidLock.unlock();
		
		UpdateResponse updateResponse = UpdateResponse.newBuilder().getDefaultInstanceForType();
		responseObserver.onNext(updateResponse);
		responseObserver.onCompleted();
		
		//decide if the update was succesffull or should i buffer it.
		if(currXid == rXid)
		{
		
			if(((state.getTail().get() && !state.getHasSuccessor().get())))
			{
				//if you are the tail and have predecessor, send the ack back.
				//btw..this ..condition will never be false, because, if you are the head too, u will not recieve an 
				//update request in the first place
				if(state.getHasPredecessor().get())
					sendAckToPredcessor(currXid);
			}
			else
			{
				//send the update to the sucessor.
				state.sendUpdateToSuccessor(currXid, key, newVal);
			}
		}
		else // buffer the update..
		{
			logger.info("Update with xid :" + rXid + "  could not be applied because currXid = " + currXid);
			//buffer the request for future processing..
			synchronized(state.replicaBuffer)
			{
				state.replicaBuffer.add(request);
			}
			logger.info("Update with xid :" + rXid + " has been buffered for future processing.");
		}
		
		logger.info("Attempting to apply any buffered requests, if the xid is appropriate..");
		synchronized(state.replicaBuffer)
		{
			state.xidLock.lock();
			while(!state.replicaBuffer.isEmpty() && (state.replicaBuffer.peek().getXid() == currXid +1))
			{
				logger.info(String.format(" buffere xid : %d = currXid : %d + 1, so applying buffered update ", currXid+1, currXid));
				UpdateRequest pendUpReq = state.replicaBuffer.poll();
				currXid+=1;
				state.xid = currXid;
				state.hashTable.put(pendUpReq.getKey(), pendUpReq.getNewValue());
				
				//TODO: Move this and the above if-else block to a common function..
				if(( !state.getTail().get() && state.getHasSuccessor().get()))
				{
					state.sendUpdateToSuccessor(currXid, pendUpReq.getKey(), pendUpReq.getNewValue());
				}
				else
				{
					if(state.getHasPredecessor().get())
						sendAckToPredcessor(currXid);
				}
				
			}
			state.xidLock.unlock();
			logger.info("buffered updates(if any) are applied");
		}
		
	}

	
	@Override
	public void stateTransfer(StateTransferRequest request, StreamObserver<StateTransferResponse> responseObserver) {
		
		List<UpdateRequest> sent = request.getSentList();
		int xid = request.getXid();
		Map<String,Integer> map = request.getStateMap();
		
		logger.info(String.format("Recieved State Transfer Request  : [ xid : %d , numKeys : %d , sentArrayLength : %d ", xid, map.size(), sent.size()));
		
		if(state.sendingState.get())
		{
			logger.info("Outward state transfer in progress, waiting for its complemtion.");
			//TODO: Wait for the completion.
			synchronized (state.sendingStateObj) {
				try {
					state.sendingStateObj.wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			logger.info("Outward state transfer completed, proceeding with inward state transfer..");
		}
		
		//set StateTransfer request is in progress == true;
		state.recievingState.set(true);
		int currXid = 0; //or -1 depends on wether xid start with 0 or 1. For now I put 0 becuase xid starts from 1.
		int currSentSize = -1;
		//lock on on xid.
		state.xidLock.lock();
		//lock on hastable
		synchronized (state.hashTable) {
			synchronized (state.sentMap) {
				currXid = state.xid == null ? currXid : state.xid;
				currSentSize = state.sentMap.size();
				state.xid = xid;
				state.hashTable = new ConcurrentHashMap<String, Integer>(map);
				state.sentMap.clear();
				for(UpdateRequest ur: sent)
				{
					state.sentMap.put(ur.getXid(), ur);
				}
				
				//Transferring the delta of update requests to the successor..
				int start = currXid+1; //TODO: Having + 1 or not depends on the starting number of xid.
				while(start<=state.xid && state.getHasSuccessor().get())
				{
					UpdateRequest ur = state.sentMap.get(start);
					state.sendUpdateToSuccessor(start++, ur.getKey(), ur.getNewValue(), false);
				}
				//reinitialize replica buffer..
				state.initReplicaUpdateBuffer();
			}
		}
		state.xidLock.unlock();
		
		state.recievingState.set(false);
		synchronized (state.recievingStateObj) {
			state.recievingStateObj.notifyAll();
		}
		
		responseObserver.onNext(StateTransferResponse.newBuilder().getDefaultInstanceForType());
		responseObserver.onCompleted();
		
		if(state.getPotentialTail().get())
		{
			state.emptySentMap();
			state.getTail().set(true);
			state.getPotentialTail().set(false);
			logger.info("State Transfer Complete, You are now the tail..");
		}
		state.emptyAckWaiMap();
	}

	@Override
	public void ack(AckRequest request, StreamObserver<AckResponse> responseObserver) {
		// TODO Auto-generated method stub
		int xid = request.getXid();
		logger.info("ACK recieved for xid :" + xid);
		List<Integer> removedXids = new ArrayList<>();
		synchronized(state.sentMap)
		{
			//TODO: Remove the XIDS, less than n..and send the acks too
			while(state.sentMap.containsKey(xid))
			{
				state.sentMap.remove(xid);
				removedXids.add(xid--);
			}
		}
		logger.info("Removed xid : "  +(xid+1) +" to " + request.getXid()+" from the sent array.");
		
		for(Integer rXid : removedXids)
		{
			Object waitObj = null;
			
			synchronized (state.ackWaitMap) {
				waitObj = state.ackWaitMap.get(rXid);
				state.ackWaitMap.remove(rXid);
			}
			if(waitObj != null)
			{
				synchronized (waitObj) {
					waitObj.notify();
				}
				logger.info("Notified the increment call : " + rXid +" ");
			}
		}
		responseObserver.onNext(AckResponse.newBuilder().getDefaultInstanceForType());
		responseObserver.onCompleted();
		//If doing residual after onCompleted is legal..then we are good..because...it will not effect any body..
		if(state.getHasPredecessor().get())
			sendAckToPredcessor(removedXids);	
	}
	
	
	
	/**
	 * Make it an async call ? or let a separate thread handle it? Not required..
	 * */
	boolean sendAckToPredcessor(List<Integer> xids)
	{
		ReplicaBlockingStub rbs = state.getChainConnMan().getPredecessorBlockingStub();
		for(Integer xid : xids)
		{
			AckRequest ackReq = AckRequest.newBuilder().setXid(xid).build();
		
			AckResponse ackResp = rbs.ack(ackReq);
			logger.info("ACK sent to predecessor :" + state.getPredecessorName()+ " for xid : " + xid);
		}
		//ToDO: check what sort of errors might occur ..here..
		return false;
	}

	boolean sendAckToPredcessor(int xid)
	{
		List<Integer> xids = new ArrayList<Integer>();
		xids.add(xid);
		return sendAckToPredcessor(xids);
	}
	/*
	boolean sendAsynchAckToPredcessor(int xid)
	{
		AckRequest ackReq = AckRequest.newBuilder().setXid(xid).build();
		ReplicaFutureStub = state.getChainConnMan().getPredecessorFutureStub();
		AckResponse ackResp = rbs.ack(ackReq);
		logger.info("ACK sent to predecessor :" + state.getPredecessorName()+ " for xid : " + xid);
		//Should I do anything with the resp?? Nope i dont think so.
		//ToDO: check what sort of errors might occur ..here..
		return false;
	}
	*/
}
