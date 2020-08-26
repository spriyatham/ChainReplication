package edu.sjsu.cs249.chain.service.mode;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import edu.sjsu.cs249.chain.replica.ChainConnectionManager;
import edu.sjsu.cs249.chain.replica.ReplicaState;
import edu.sjsu.cs249.chain.replica.ZkManager;

public class PredecessorMontior implements Runnable, Watcher {

	/**
	 * The duty of this thread is monitor any changes to the current replica's predecessor..
	 * 
	 * Should utilize a common config object..
	 * **/
	private static final Logger logger = Logger.getLogger(PredecessorMontior.class);
	Object eventMonitor = new Object();
	//boolean shutdown;
	ReplicaState state;
	ZkManager zkm;
	ChainConnectionManager ccMan;
	ReentrantLock inProgressLock;
	
	public PredecessorMontior(ReplicaState state) {
		super();
		this.state = state;
		this.zkm = state.getZkManager();
		this.ccMan = state.getChainConnMan();
		this.inProgressLock = state.getPredMonInProLock();
	}
	
	@Override
	public void run() {
		logger.info("Predecessor thread started..");
		do 
		{
			try
			{
				//1. get children
				//2. sort children based on replica numbers..
				//3. See..if it has a predecessor
				//4. If it has predeccessor, then get the predecessorZnode contents..
				//5. Then set has predecessor .
				//6. if it does not, then set. havePredecessor = Flase and set head = true;
				//
				boolean posDetermined = false;
				inProgressLock.lock();
				while(!posDetermined && !state.getShutdown().get())
				{
					
					logger.info("Determining position..");
					List<String> chainNodes = zkm.getChildren();
					//will this cause an inifinite loop?? //TODO: maintain a Retry Count..
					//may be put this retry logic in the getChildren() itself..
					// if retry limit crossed..then..te process should gracefully shutdown.
					
					if(chainNodes == null || chainNodes.size() == 0) 
					{
						logger.info(zkm.getChainZnodesPath() + " has no nodes..shutting down...");
						continue;
					}
					
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
					
					//size should not be zero..and if it is one it should be the current Replica's znode
					//Exceptional Case: Duee to some connection..problem, if we loose connection to the master,
					//Replica will still be running, however, its znode might get deleted, because of the timeout on zk side.
					//This might lead..to erroneous situation...at this point replica, should gracefully shutdown
					//DONE: Its Handled in getChildren()
					if(chainNodes.size() == 1 || chainNodes.get(0).equals(state.getMyName()))
					{
						state.getHasPredecessor().set(false);
						state.getHead().set(true);
						state.initReplicaUpdateBuffer();
						state.getChainConnMan().destoryPredecessorStubs();
						state.setPredecessorName("");
						logger.info(state.getMyName() + " is te head of the chain..");
						logger.debug("hasPredcessor : false");
						posDetermined = true;
						continue;
					}
					
					
					int index = chainNodes.indexOf(state.getMyName());
					
					if(index > 0)
					{
						//have predecessor
						//get index-1 znode
						String predecessor = chainNodes.get(index -1);
						logger.info(predecessor + "is the predecessor of " + state.getMyName());
						logger.info("Getting " +predecessor + "'s conents..");
						String[] predConnInfo = zkm.getZnodeContents(predecessor, this);
						
						if(predConnInfo==null) // getZnodeContents will return null when the node is not present.
						{
							logger.info(predecessor  + "might have failed between the time, we quried for getChildren and the getData\n" + 
							"So we need to determine the position again");
							continue;
							//TODO: May be here, try getting the --index chain Node..until we run out of chainNodes..before we determine
							//the position...
						}
						//and store that information.
						state.getHead().compareAndSet(true, false);
						state.getHasPredecessor().set(true);
						logger.debug("Head flag = false");
						logger.debug("has predecessor = true");
						posDetermined = true;
						ccMan.setPredecessorConnectionInfo(predConnInfo[0]);
						state.setPredecessorName(predConnInfo[1]);
						logger.info(predecessor + " stube creation : BEGIN");
						ccMan.createPredecssorStubs();
						logger.info(predecessor + " stube creation : END");
						//TDOD: Once I know that I ave a predecessor I, sould just, wait for a state Transfer request, or 
						//update request before I process anything..right???
					}
				}
				inProgressLock.unlock();
				posDetermined = false;
				state.logState();
				logger.info(state.getMyName() + "'s positon determined, waiting for next change..");
				synchronized (eventMonitor) {
					eventMonitor.wait();
				}
				logger.info("Predecessor related change identfied for " + state.getMyName() +
						" determining postion again.");
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
		// TODO add appropriateStuffHere..
		if(arg0.getType() == Watcher.Event.EventType.NodeDeleted)
		{
			//Predecessor was deleted..
			//Destroy the stubs.
			ccMan.destoryPredecessorStubs();
			state.getHasPredecessor().set(false);
			synchronized(eventMonitor) {
				eventMonitor.notify();
			}
		}
		
	}
	
	public void sutdown()
	{
		//The common shutdown flage must have already been set to true
		synchronized(eventMonitor) {
			eventMonitor.notify();
		}
	}
	

}
