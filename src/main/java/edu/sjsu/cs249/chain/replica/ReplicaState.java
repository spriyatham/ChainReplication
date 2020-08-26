package edu.sjsu.cs249.chain.replica;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;

import edu.sjsu.cs249.chain.UpdateRequest;
import edu.sjsu.cs249.chain.UpdateResponse;
import edu.sjsu.cs249.chain.ReplicaGrpc.ReplicaFutureStub;
import edu.sjsu.cs249.chain.service.UpdateResponseCallBack;
import io.grpc.Context;

public class ReplicaState {
	
	private static final Logger logger = Logger.getLogger(ReplicaState.class);
	//Replica has to maintain this incremental hashtable and execute operations on it.
	public ConcurrentHashMap<String, Integer> hashTable;
	
	//When the replica is initializied its xid should be null...the value will be determined once the position is determined....
	//Ideally, the xid will be initialized in two ways..
	// 1. when you are head, it will get initialized, through the first inc request..
	// 2. When you are anywhere else in the chain, xid should be initialized through a stateTransferRequest
	// alaways read(release immediately after read) or write(release immediately after write) the value to after acquiring xid lock..
	public Integer xid = null; 
	public TreeMap<Integer,UpdateRequest> sentMap;
	public HashMap<Integer,Boolean> ackWaitMap;
	public ReentrantLock xidLock = new ReentrantLock();
	
	//out Of order update requests will be buffered here, until a correct request comes..
	//Empty this, when then node becomes head and when an inward state transfer request is completed..
	public PriorityQueue<UpdateRequest> replicaBuffer;
	
	
	// Does this replica have a successor?
	AtomicBoolean hasSuccessor;
	
	// Does this replica have a predecessor?
	AtomicBoolean hasPredecessor;
	
	// Replica figured out that it is at the end of the chain, however, it still cannot service queries..
	// it has to wait for StateTransferReqeusts to arrive from the other side.
	AtomicBoolean potentialTail;
	
	AtomicBoolean tail;
	
	AtomicBoolean head;
	
	AtomicBoolean shutdown;
	
	public AtomicBoolean recievingState;
	
	public Object recievingStateObj;
	
	public AtomicBoolean sendingState;
	
	public Object sendingStateObj;
	//TODO: Two objects to wait on..when the state transfer (recieving or sending ) is in progress..
	
	ReentrantLock predMonInProLock;
	
	String sucessorName;
	String predecessorName;
	//TODO: Set myName.
	String myName;
	
	/**TODO: At this point, I feel that list is enough,
	 * the xid of the last UpdateRequest gives us the last xid applied..
	 * Which can be compared during the last xid that it recieved in a state Transfer request..
	 * and forward the pending requests.
	 */
	ArrayList<UpdateRequest> sent;
	
	
	//manages connection to the manager.
	ZkManager zkManager;
	ChainConnectionManager chainConnMan;
	
	
	//TODO: This should be resolved during Replica
	String replicaIp;
	String grpcServerPort;
	
	public ReplicaState(String grpcServerPort) throws UnknownHostException {
		this.grpcServerPort = grpcServerPort;
		init();
	}

	void init() throws UnknownHostException
	{
		hasSuccessor = new AtomicBoolean(false);
		hasPredecessor = new AtomicBoolean(false);
		potentialTail = new AtomicBoolean(false);
		tail = new AtomicBoolean(false);
		head = new AtomicBoolean(false);
		shutdown = new AtomicBoolean(false);
		replicaIp = getLocaIpAddress();
		predMonInProLock = new ReentrantLock();
		sendingState = new AtomicBoolean(false);
		recievingState = new AtomicBoolean(false);
		sendingStateObj = new Object();
		recievingStateObj = new Object();
		
		
		//init protocol related data structures.
		hashTable = new ConcurrentHashMap<String, Integer>();
		sentMap = new TreeMap<Integer, UpdateRequest>();
		//will be cleared every time, you recieve a state transfer requests- because u are definitely not the head..at that time.
		ackWaitMap = new HashMap<Integer, Boolean>();
	}
	
	//shoulbe emptied, whenver, a server becomes head..
	public void initReplicaUpdateBuffer()
	{
		replicaBuffer = new PriorityQueue<UpdateRequest>(new Comparator<UpdateRequest>() {
			@Override
			public int compare(UpdateRequest o1, UpdateRequest o2) {
				return o1.getXid() - o2.getXid();
			}
		});
	}
	
	public void emptySentMap()
	{
		synchronized (sentMap) {
			sentMap.clear();
		}
	}
	
	public void emptyAckWaiMap()
	{
		synchronized (ackWaitMap) {
			ackWaitMap.clear();
		}
	}
	
	public PriorityQueue<UpdateRequest> getReplicaBuffer() {
		return replicaBuffer;
	}

	public void setReplicaBuffer(PriorityQueue<UpdateRequest> replicaBuffer) {
		this.replicaBuffer = replicaBuffer;
	}

	public void logState()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("******REPLICA STATE*********\n");
		sb.append("is Tail : " + tail.get()+"\n");
		sb.append("is head : " + head.get()+"\n");
		sb.append("is Potential Tail : " + potentialTail.get()+"\n");
		sb.append("is shutdown " + shutdown.get()+"\n");
		sb.append("has Successor :" + hasSuccessor.get()+"\n");
		sb.append("has Predecessor : " + hasPredecessor.get()+"\n");
		sb.append("******REPLICA STATE*********"+"\n");
		logger.info(sb.toString());
	}
	
	String getLocaIpAddress() throws UnknownHostException
	{
		InetAddress inetAddress = InetAddress.getLocalHost();
		//TODO: Add some validation stuff here if required..and may be handle unknown host exception..
		return inetAddress.getHostAddress();
	}
	
	/**
	 * TODO: Remove duolicate code..
	 * To be called only when you are not the tail..
	 * */
	public boolean  sendUpdateToSuccessor(int xid, String key, int value, boolean addToSentMap)
	{
		
		UpdateRequest ur = UpdateRequest.newBuilder().setKey(key).setXid(xid).setNewValue(value).build();
		//The updateRequest handler should just put the new value..
		ReplicaFutureStub rfs = getChainConnMan().getSuccessorFutureStub();
		
		logger.info("Sending an update request to the successor : " + getSucessorName() + " contnets[" + 
		xid +"," + key +","+ value+ "]");
		
		Context context = Context.current().fork();
		context.run(() -> {
			ListenableFuture<UpdateResponse> upRes = rfs.update(ur);
			Futures.addCallback(upRes, new UpdateResponseCallBack(xid, this), MoreExecutors.directExecutor());}
		);
		
		logger.info("Update request future request scheduled.");
		if(addToSentMap)
		{
			synchronized (sentMap) {
				sentMap.put(xid, ur);
			}
		}
		if(getHead().get())
		{
			synchronized (ackWaitMap)
			{
				//we set true, assuming that the operation would be successful, if it fails the call back would set False.
				ackWaitMap.put(xid, Boolean.TRUE);
			}
		}
		return true;
	}
	
	public boolean sendUpdateToSuccessor(int xid, String key, int value)
	{
		return sendUpdateToSuccessor(xid, key, value, true);
	}
	
	
	public ConcurrentHashMap<String, Integer> getHashTable() {
		return hashTable;
	}
	public void setHashTable(ConcurrentHashMap<String, Integer> hashTable) {
		this.hashTable = hashTable;
	}
	public AtomicBoolean getHasSuccessor() {
		return hasSuccessor;
	}
	public void setHasSuccessor(AtomicBoolean hasSuccessor) {
		this.hasSuccessor = hasSuccessor;
	}
	public AtomicBoolean getHasPredecessor() {
		return hasPredecessor;
	}
	public void setHasPredecessor(AtomicBoolean hasPredecessor) {
		this.hasPredecessor = hasPredecessor;
	}
	public AtomicBoolean getPotentialTail() {
		return potentialTail;
	}
	public void setPotentialTail(AtomicBoolean potentialTail) {
		this.potentialTail = potentialTail;
	}
	public AtomicBoolean getTail() {
		return tail;
	}
	public void setTail(AtomicBoolean tail) {
		this.tail = tail;
	}
	public AtomicBoolean getHead() {
		return head;
	}
	public void setHead(AtomicBoolean head) {
		this.head = head;
	}
	public ArrayList<UpdateRequest> getSent() {
		return sent;
	}
	public void setSent(ArrayList<UpdateRequest> sent) {
		this.sent = sent;
	}
	public ZkManager getZkManager() {
		return zkManager;
	}
	public void setZkManager(ZkManager zkManager) {
		this.zkManager = zkManager;
	}
	public ChainConnectionManager getChainConnMan() {
		return chainConnMan;
	}
	public void setChainConnMan(ChainConnectionManager chainConnMan) {
		this.chainConnMan = chainConnMan;
	}
	public String getReplicaIp() {
		return replicaIp;
	}
	public void setReplicaIp(String replicaIp) {
		this.replicaIp = replicaIp;
	}
	public String getGrpcServerPort() {
		return grpcServerPort;
	}
	public void setGrpcServerPort(String grpcServerPort) {
		this.grpcServerPort = grpcServerPort;
	}
	public AtomicBoolean getShutdown() {
		return shutdown;
	}
	public void setShutdown(AtomicBoolean shutdown) {
		this.shutdown = shutdown;
	}
	public String getSucessorName() {
		return sucessorName;
	}
	public void setSucessorName(String sucessorName) {
		this.sucessorName = sucessorName;
	}
	public String getPredecessorName() {
		return predecessorName;
	}
	public void setPredecessorName(String predecessorName) {
		this.predecessorName = predecessorName;
	}
	public String getMyName() {
		return myName;
	}
	public void setMyName(String myName) {
		int start = zkManager.getChainZnodesPath().length()+1;
		this.myName = myName.substring(start);
	}

	public ReentrantLock getPredMonInProLock() {
		return predMonInProLock;
	}

	public void setPredMonInProLock(ReentrantLock predMonInProLock) {
		this.predMonInProLock = predMonInProLock;
	}
	
	
	
	
}
