package edu.sjsu.cs249.chain.service.mode;


import org.apache.log4j.Logger;

import edu.sjsu.cs249.chain.replica.ReplicaState;
import edu.sjsu.cs249.chain.replica.ZkManager;

public class MasterLiason implements Runnable {
	private static final Logger logger = Logger.getLogger(MasterLiason.class);
	ZkManager zkm;
	ReplicaState state;
	//ZooKeeper zk;
	
	public MasterLiason(ReplicaState state) {
		this.state = state;
		this.zkm = state.getZkManager();
	}

	@Override
	public void run() {
		// 1. create znode 
		logger.info("Creating replica Znode");
		String myName = zkm.createZNode(); 
		if(myName == null)
		{
			//Creating the node failed, I cant work without being part of the chain.
			//so shutting myself down.
			logger.fatal("Creating the node failed, I cant work without being part of the chain, so shutting myself down."+
			"\n shutdown flag set to true.");
			state.getShutdown().set(true);
			return;
		}
		logger.info(myName + " zNode created");
		state.setMyName(myName);
		PredecessorMontior predecessorMontior = new PredecessorMontior(state);
		SuccessorMonitor successorMonitor = new SuccessorMonitor(state);
		Thread pdt = new Thread(predecessorMontior);
		Thread smt = new Thread(successorMonitor);
		pdt.start();
		smt.start();
		logger.info("Started Prdecessor and Successor Monitor threads");
		try {
			pdt.join();
			smt.join();
			logger.info("Prdecessor and Successor Monitor threads completed their execution..");
		} catch (InterruptedException e) {
			//TODO: Can I do anything here to recover???
			//just set  shutdown.
			logger.info("Interruppted exception occurred while waiting for predecessor and successor threads to complete their exection,"
					+ " shutting down the replica." + e.getMessage());
			state.getShutdown().compareAndSet(false, true);
			e.printStackTrace();
		}
		
	}
	
	
	
}
