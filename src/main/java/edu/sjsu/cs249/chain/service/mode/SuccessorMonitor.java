package edu.sjsu.cs249.chain.service.mode;

import java.util.Comparator;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

import edu.sjsu.cs249.chain.ReplicaGrpc.ReplicaBlockingStub;
import edu.sjsu.cs249.chain.StateTransferRequest;
import edu.sjsu.cs249.chain.StateTransferRequest.Builder;
import edu.sjsu.cs249.chain.UpdateRequest;
import edu.sjsu.cs249.chain.replica.ChainConnectionManager;
import edu.sjsu.cs249.chain.replica.ReplicaState;
import edu.sjsu.cs249.chain.replica.ZkManager;

public class SuccessorMonitor implements Runnable, Watcher {
	
	/**
	 * Monitor any changes to the client's successor, and new successor might be added or deleted
	 * 
	 * */
	private static final Logger logger = Logger.getLogger(SuccessorMonitor.class);
	
	//boolean shutdown;
	Object eventMonitor = new Object();
	//boolean shutdown;
	ReplicaState state;
	ZkManager zkm;
	ChainConnectionManager ccMan;
	boolean successorDeleted;
	ReentrantLock predMonInProgLock;
	
	public SuccessorMonitor(ReplicaState state) {
		super();
		this.state = state;
		this.zkm = state.getZkManager();
		this.ccMan = state.getChainConnMan();
		this.predMonInProgLock = state.getPredMonInProLock();
	}
	
	@Override
	public void run() {
		logger.info("Successor thread started..");
		do 
		{
			try
			{
				//1. get children
				//2. sort children based on replica numbers..
				//3. See..if it has a Successor
				//4. If it has Successor, then get the sucessorZnode contents..
				//5. Then set has Successor .
				//6. if it does not, then set. has Successor = Flase and set Tail/potential Tail = true;
				//
				boolean posDetermined = false;
				logger.info("determining the position of " + state.getMyName());
				while(!posDetermined && !state.getShutdown().get())
				{
					List<String> chainNodes = zkm.getChildren();
					
					if(chainNodes == null || chainNodes.size() == 0) 
					{
						logger.info("For some weird reason, " + zkm.getChainZnodesPath() + " is empty, shutting down..");
						continue;
					}
					
					int size = chainNodes.size();
					chainNodes.sort(new Comparator<String>() {
						@Override
						public int compare(String o1, String o2) {
							int start1 = o1.length() - 10;
							int start2 = o2.length() - 10;
							long replicaNum1 = Long.parseLong(o1.substring(start1));
							long replicaNum2 = Long.parseLong(o2.substring(start2));
							if(replicaNum2 < replicaNum1) return 1;
							if(replicaNum2 > replicaNum1) return -1;
							else return 0;
						}
					});
					
					//Am I the last node in the chain.
					if(chainNodes.get(size -1).equalsIgnoreCase(state.getMyName()) )
					{
						if(!state.getHasSuccessor().get() && (state.getTail().get() || state.getPotentialTail().get()))
						{
							//you are already the tail..and you still are...so not changing anything..
							zkm.setWatchOnControlPath(this);
							posDetermined = true;
							state.getHasSuccessor().set(false);
							logger.info("I am already the tail: This is not a successor related change : Watch has been reset.");
							continue;
						}
						logger.info(state.getMyName() + " is the last node in the chain");
						state.getHasSuccessor().set(false);
						state.setSucessorName("");
						//Am I the last node, because my successor was deleted.?
						if(successorDeleted)
						{
							//You are part of the chain, your successor got deleted, so..now your are the tail..
							logger.info("You ("+state.getMyName() + ") were already part of the chain, your successor got deleted, so..now your are the tail..");
							state.getTail().set(true);
							logger.info("Emptying the sent array, Now that, I am the tail");
							state.emptySentMap();
							successorDeleted = false;
							ccMan.destroySuccessorStubs();
						}
						else
						{
							//This should be called only when, a predecessor determination run is not in progress
							boolean hasPredecessor = false;
							predMonInProgLock.lock();
							hasPredecessor = state.getHasPredecessor().get();
							predMonInProgLock.unlock();
							
							if(hasPredecessor)
							{
								//I have a predecessor, so I will recieve a state transfer request, then I can become the tail.
								logger.info(state.getMyName() + " has a predecessor, so I will recieve a state transfer request, then I can become the tail.");
								state.getPotentialTail().set(true);
							}
							else
							{
								//Now you are the tail..
								logger.info(state.getMyName() + " is the tail now..");
								state.getTail().set(true);
								logger.info("Emptying the sent array, Now that, I am the tail");
								state.emptySentMap();
							}
						}
						//set a watch to get notified, when a successor is added.
						logger.info("set a watch on " +zkm.getChainZnodesPath()+ " to get notified, when a successor is added.");
						zkm.setWatchOnControlPath(this);
						//state.getHasSuccessor().set(false);
						posDetermined = true;
						continue;
					}
					
					
					
					//int size = chainNodes.size();
					int index = chainNodes.indexOf(state.getMyName());
					
					if(index < size - 1)
					{
						//check if your exisiting successor changed..
						if(state.getHasSuccessor().get() && state.getSucessorName().equals(chainNodes.get(index+1)))
						{
							//your successor is not effected..
							logger.info("My successor is not effected, this change is not relavent to us. ");
							//logger.info("Resetting the watch on control Path.");
							//zkm.setWatchOnControlPath(this);
							posDetermined = true;
							continue;
						}
						//you have successor
						String[] succConnInf = zkm.getZnodeContents(chainNodes.get(index+1), this);
						if(succConnInf==null) // getZnodeContents will return null when the node is not present.
						{
							//means the successor failed between the time, we quried for getChildren and the getData.
							//So I need to determine my position again
							logger.info("Successor ("+chainNodes.get(index+1) +") failed between the time, we quried for getChildren and the getData\n So I need to determine my position again");
							continue;
						}
						//and store that information.
						state.getHasSuccessor().set(true);
						posDetermined = true;
						ccMan.setSuccessorConnectionInfo(succConnInf[0]);
						state.setSucessorName(succConnInf[1]);
						logger.info("Creating successor stubs: BEGIN");
						ccMan.createSuccessorStubs();
						logger.info("Creating successor stubs: END");
						
						//Perform state transfer to the successor..
						performStateTransfer();
						//set your tail flag to flase;
						state.getTail().compareAndSet(true, false);
						state.getPotentialTail().compareAndSet(true, false);
					}
					
				}
				successorDeleted = false;
				state.logState();
				//waiting for the 
				logger.info("waiting for the next successor related change...");
				synchronized (eventMonitor) {
					eventMonitor.wait();
				}
				logger.info("wait ended...");
			}
			catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		while(!state.getShutdown().get());
	}
	@Override
	public void process(WatchedEvent arg0) {
		/**
		 * You will get events for
		 * 1. Node Added - ideally..you should just deal with the successor..but its fine..just call determine position..
		 * 2. Node Deleted - You will be waiting on a specific node...- just call determine position for node to be added..
		 * */
		if(arg0.getType() == Watcher.Event.EventType.NodeChildrenChanged)
		{
			//TODO: Check if you will get an event when a change happends somewhere down the lane..
			//A new node added to the queue, will create this trigger, i.e you have a new successor
			logger.info("Some change happened to " + zkm.getChainZnodesPath() + " checking if it is relavent to us..");
			synchronized (eventMonitor) {
				eventMonitor.notify();
			}
		}
		
		if(arg0.getType() == Watcher.Event.EventType.NodeDeleted)
		{
			//Node Delete will trigger this...
			logger.info("Successor (" + arg0.getPath() + ") deleted...notifying...");
			successorDeleted = true;
			synchronized (eventMonitor) {
				eventMonitor.notify();
			}
		}
	}
	
	void performStateTransfer()
	{
		//TODO: Ensure that you are not recieving a state transfer...
		if(state.recievingState.get())
		{
			logger.info("Currently recieving state transfer from predecessor, waiting for it to complete, before I begin the transfer to the successor");
			//wait on an object to get notofied..
		}
		logger.info("Initating Outward state Transfer process");
		state.sendingState.set(true);
		Builder strb = StateTransferRequest.newBuilder();
		state.xidLock.lock();
		synchronized(state.hashTable)
		{
			synchronized (state.sentMap) {
				if(state.xid == null)
				{
					//TODO: Again, Set 0 because, As per my def, i assign the first request and xid of 1
					strb.setXid(0);
				}
				else
				{
					strb.setXid(state.xid);
				}
				strb.putAllState(state.hashTable);
				strb.addAllSent(state.sentMap.values());
			}
		}
		state.xidLock.unlock();
		StateTransferRequest str = strb.build();
		ReplicaBlockingStub rbs = state.getChainConnMan().getSuccessorBlockingStub();
		rbs.stateTransfer(str);
		logger.info("Outward State Transfer process complete.");
		state.sendingState.set(false);
		synchronized (state.sendingStateObj) {
			//ToDO: Will an error be thrown...when a no thread is waiting for this??
			state.sendingStateObj.notifyAll();
		}
	}
	

}
