package edu.sjsu.cs249.chain.service;

import edu.sjsu.cs249.chain.HeadChainReplicaGrpc.HeadChainReplicaImplBase;

import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;

import edu.sjsu.cs249.chain.HeadResponse;
import edu.sjsu.cs249.chain.HeadResponse.Builder;
import edu.sjsu.cs249.chain.IncRequest;
import edu.sjsu.cs249.chain.ReplicaGrpc.ReplicaBlockingStub;
import edu.sjsu.cs249.chain.UpdateRequest;
import edu.sjsu.cs249.chain.UpdateRequestOrBuilder;
import edu.sjsu.cs249.chain.UpdateResponse;
import edu.sjsu.cs249.chain.replica.ReplicaState;
import io.grpc.stub.StreamObserver;

/**
 * 
service HeadChainReplica {
   rpc increment(IncRequest) returns (HeadResponse);
}


message IncRequest {
   string key = 1;
   // if the key does not exist, it will be created with this value, otherwise the value
   // if the existing key will be incremented by this value
   int32 incValue = 2;
}

message HeadResponse {
   // rc = 0 means success, rc = 1 i'm not the head
   uint32 rc = 1;
}
 * 
 * */

public class HeadService extends HeadChainReplicaImplBase {

	ReplicaState state;
	ReentrantLock xidLock;
	private static final Logger logger = Logger.getLogger("HeadService");
	
	
	public HeadService(ReplicaState state) {
		super();
		this.state = state;
		this.xidLock = state.xidLock;
	}

	@Override
	public void increment(IncRequest request, StreamObserver<HeadResponse> responseObserver) {
		String key = request.getKey();
		int value= request.getIncValue();
		logger.info("Head recieved increment request with key = " + key + ", value = " + value);
		
		Builder resBuilder = HeadResponse.newBuilder();
		//increment should only be processed when you are te head..
		if(!state.getHead().get())
		{
			logger.info(state.getMyName() + " not the head, returning status 1");
			HeadResponse hr = resBuilder.setRc(1).build();
			responseObserver.onNext(hr);
			responseObserver.onCompleted();
			return;
		}
		
		//Are you transferring your state..?? then wait..for it get completed..before you apply this change..
		if(state.sendingState.get())
		{
			logger.info("Waitng...for the state transfer to get completed, before I can process a new increment");
			synchronized (state.sendingStateObj) {
				try {
					state.sendingStateObj.wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			logger.info("Wait ended..processing the increment rquest");
		}
		
		
		int currXid = 0;
		int newVal = -1;
		xidLock.lock();
		logger.info("Acquired xid lock");
		state.xid = (state.xid == null)? 1 : state.xid +1; //TODO: For now, I am assuming that, the first xid is 1;
		currXid = state.xid;
		logger.info("New xid = " + currXid);
		if(state.hashTable.containsKey(key))
		{
			int currVal = state.hashTable.get(key);
			newVal = currVal + value;
			state.hashTable.put(key, newVal);
		}
		else
		{
			newVal = value;
			state.hashTable.put(key, value);
		}
		logger.info("After increment state : xid = " + currXid + ", key = " + key + " ,value = " + newVal);
		xidLock.unlock();
		if(!state.getTail().get() && state.getHasSuccessor().get())
		{
			state.sendUpdateToSuccessor(currXid, key, newVal);
			Object ackWaitObj = null;
			
			synchronized(state.ackWaitMap)
			{
				ackWaitObj = state.ackWaitMap.get(currXid);
			}
			
			synchronized(ackWaitObj)
			{
				try {
					ackWaitObj.wait();
					
					logger.info("Returning result to client...");
					if(ackWaitObj.equals(Boolean.FALSE))
					{
						responseObserver.onError(new Exception("Some error occurred in sending the update request."));
						return;
					}
				} catch (InterruptedException e) {
					responseObserver.onError(e);
					return;
				}
			}
		}
		else
		{
			logger.info("I am also the tail, so not sending an UpdateRequest to the successor.");
		}
		
		//Send a success HeadResponse through response observer...
		HeadResponse hr = resBuilder.setRc(0).build();
		responseObserver.onNext(hr);
		responseObserver.onCompleted();
	}

	/**
	 * To be called only when you are not the tail..
	 * */
	boolean  sendUpdateToSuccessor(int xid, String key, int value)
	{
		UpdateRequest ur = UpdateRequest.newBuilder().setKey(key).setXid(xid).setNewValue(value).build();
		//The updateRequest handler should just put the new value..
		ReplicaBlockingStub rbs = state.getChainConnMan().getSuccessorBlockingStub();
		
		logger.info("Sending an update request to the successor : " + state.getSucessorName() + " contnets[" + 
		xid +"," + key +","+ value+ "]");
		//TODO: Handle error;
		UpdateResponse upRes = rbs.update(ur);
		
		logger.info("Update request succesffully sent");
		synchronized (state.sentMap) {
			state.sentMap.put(xid, ur);
		}
		synchronized (state.ackWaitMap)
		{
			state.ackWaitMap.put(xid,Boolean.TRUE);
		}
		return true;
	}
	
	
}
