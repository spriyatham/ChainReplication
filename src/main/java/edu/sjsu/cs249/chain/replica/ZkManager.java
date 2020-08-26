package edu.sjsu.cs249.chain.replica;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

/**
 * Stores information and manages connection to zookeeper.
 * 
 *TODO: ZKM is also responsibe for monitoring the connection zookeeper..
 * When ever, a connection is lost, and reconnected messgae comes, the manager should check, if the replica znode is present 
 * in the control_path or not..if its not there..then set shutdown to true..
 * */
public class ZkManager {
	private static final Logger logger = Logger.getLogger(ZkManager.class);
	ReentrantLock zkLock = new ReentrantLock();
	ZooKeeper zk;
	//Path to znode, that contains the znodes of all the replicas in the chain.
	String chainZnodesPath;
	String zkEnsembleConnectionString; 
	ReplicaState state;
	String userName;
	
	
	public ZkManager(String connString, String controlPath,ReplicaState state, String userName) {
		this.state = state;
		this.zkEnsembleConnectionString = connString;
		this.chainZnodesPath = controlPath;
		this.userName = userName;
	}

	public boolean connect(String zkEnsembleConnectionString) throws IOException,InterruptedException {
		
		CountDownLatch connectedSignal = new CountDownLatch(1);
		logger.info("Establishing connection to ZooKeeper");
		zk = new ZooKeeper(zkEnsembleConnectionString,5000,(Watcher) new Watcher() {
			
	        public void process(WatchedEvent we) {
	            if (we.getState() == KeeperState.SyncConnected) {
               connectedSignal.countDown();
            }
         }
      });
			
      connectedSignal.await();
      logger.info("Connection to Zookeeper established.");
      return true;
	 }

	/**
	 * Create znode for the current replica..
	 * This function tries its best to create the znode and return its name
	 * If it fails (after a sensible number of retries and recovery ), it returns null.
	 * If null, is returned the caller should set shutdown to true;
	 * */
	public String createZNode()
	{
		/*
		 * the znode will contain two lines: 
		1.	the first line is host:port of the grpc for the replica. 
		2.	the next line will be your name.
		 * */
		String controlPath = getChainZnodesPath();
		String content = state.getReplicaIp() + ":" + state.getGrpcServerPort() + "\n" + userName;
		
		String replicaZnode = null;
		logger.info("Creating replcia znode...");
		try {
			replicaZnode = zk.create(controlPath+"/replica-" , content.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
			logger.info("Replica znode " + replicaZnode + " created..");
		}
		catch(KeeperException ke)
		{
			logger.error("Keeper Exception occurred",ke);
			//TODO: Handle Connection loss. session expired exception....and try to restablish the connectiom..others you cant do much..
			//throw ke;
		}
		catch(InterruptedException ie)
		{
			logger.error("InterruptedException occurred...",ie);
			//TODO retry for a particular number of times.
		}
		
		return replicaZnode;
	}
	/**
	 * Handle Synchronization..effectively..
	 * */
	public List<String> getChildren() throws InterruptedException
	{
		//TODO: might have to pass a watcher object..
		List<String> children = null;
		try {
			zkLock.lock();
			children =  zk.getChildren(chainZnodesPath, false);
			zkLock.unlock();
			if(children.size() == 0 || !children.contains(state.getMyName()))
			{
				logger.info("The replica is not present..in the" + getChainZnodesPath() + " so shutting down..."); 
				state.getShutdown().set(true);
				return null;
			}
		} catch (KeeperException e) {
			// TODO handle connection lost exception and other related stuff..
			logger.info("Keeper Exception occurred", e);
		} 
		finally{
			if(zkLock.isHeldByCurrentThread())
				zkLock.unlock();
		}
		return children;
	}
	
	public boolean setWatchOnControlPath(Watcher watcher)
	{
		try {
			zkLock.lock();
			zk.getChildren(chainZnodesPath, watcher);
		} catch (KeeperException e) {
			// TODO handle connection lost exception and other related stuff..
			//TODO: Retry for a preconfigured number of times.
			logger.info("Keeper Exception occurred...", e);
			return false;
		} catch (InterruptedException e) {
			//TODO: Retry for a preconfigured number of times.
			logger.info(" Interrupted Exception occurred...",e);
			e.printStackTrace();
			return false;
		} 
		finally{
			if(zkLock.isHeldByCurrentThread())
			zkLock.unlock();
		}
		return true;
	}
	//getData and set watch..
	public String[] getZnodeContents(String zNodeName, Watcher watcher)
	{
		logger.info("Get Data : "+ zNodeName);
		String[] contents = new String[2];
		try {
			zkLock.lock();
			byte[] data = zk.getData(chainZnodesPath + "/" +zNodeName, watcher, new Stat());
			zkLock.unlock();
			String sData = new String(data);
			logger.info("returned data " + sData);
			if(sData.length() == 0)
			{
				//The predecessor or successor has an empty zNode..since I cant function properly without that info..
				//I am gracefully shutting down
				logger.warn("The predecessor or successor has an empty zNode..since I cant function properly without that info..\n"+ "I am gracefully shutting down" );
				state.getShutdown().set(true);
				return null;
			}
			contents = sData.split("\\n");
			contents[0] = contents[0].trim();
			contents[1] = contents[1].trim();
		} catch (KeeperException e) {
			//TODO: If I get an node not present exception,then return null beca
			logger.error("keeper exception ", e);
			return null;
		} catch (InterruptedException e) {
			//TODO: Retry ...incase of interruption..
			logger.error("Interrupted Exception : " , e);
			//e.printStackTrace();
		} finally {
			if(zkLock.isHeldByCurrentThread())
			{
				zkLock.unlock();
			}
		}
		
		return contents;
	}
	
	
	public void close() throws InterruptedException
	{
		zk.close();
	}

	public ZooKeeper getZk() {
		return zk;
	}

	public void setZk(ZooKeeper zk) {
		this.zk = zk;
	}

	public String getChainZnodesPath() {
		return chainZnodesPath;
	}

	public void setChainZnodesPath(String chainZnodesPath) {
		this.chainZnodesPath = chainZnodesPath;
	}

	public String getZkEnsembleConnectionString() {
		return zkEnsembleConnectionString;
	}

	public void setZkEnsembleConnectionString(String zkEnsembleConnectionString) {
		this.zkEnsembleConnectionString = zkEnsembleConnectionString;
	}
	
	//TODO: Add restart() / re-establisConnection() methods if required.
	
	
}
