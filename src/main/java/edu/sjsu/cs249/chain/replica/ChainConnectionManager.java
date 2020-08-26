package edu.sjsu.cs249.chain.replica;

import edu.sjsu.cs249.chain.ReplicaGrpc;
import edu.sjsu.cs249.chain.ReplicaGrpc.ReplicaBlockingStub;
import edu.sjsu.cs249.chain.ReplicaGrpc.ReplicaFutureStub;
import edu.sjsu.cs249.chain.service.ChainReplicaService;
import io.grpc.Channel;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

/**
 * Here we maintain the connectivity information of the successor and predecessor if any.
 * It has methods which create and store stubs to preceeding and succeeding nodes.
 * */
public class ChainConnectionManager {
	
	
	public ChainConnectionManager() {
	}

	// arrays of length 2 : 0 - host , 1 - port
	String predecessorIp;
	int predecessorPort;
	String successorIp;
	int successorPort;
	
	
	String[] successorConnectionInfo;
	String[] predecessorConnectionInfo;
 	
	ManagedChannel predecssorChannel;
	ManagedChannel successorChannel;
	
	ReplicaFutureStub predecessorFutureStub;
	ReplicaBlockingStub predecessorBlockingStub;
	
	
	ReplicaFutureStub successorFutureStub;
	ReplicaBlockingStub successorBlockingStub;
	
	//TODO: Add appropriate constructor to parse the connection string..and set the required variables.
	
	public void createPredecssorStubs()
	{
		predecssorChannel = ManagedChannelBuilder.forAddress(predecessorIp, predecessorPort).usePlaintext().build();
		predecessorFutureStub = ReplicaGrpc.newFutureStub(predecssorChannel);
		predecessorBlockingStub = ReplicaGrpc.newBlockingStub(predecssorChannel);
	}
	
	public void destoryPredecessorStubs()
	{
		predecessorIp = null;
		predecessorPort = 0;
		predecessorFutureStub = null;
		predecessorBlockingStub = null;
		if(predecssorChannel != null)
		{
			predecssorChannel.shutdownNow();
			predecssorChannel = null;
		}
	}
	
	public void createSuccessorStubs()
	{
		successorChannel = ManagedChannelBuilder.forAddress(successorIp, successorPort).usePlaintext().build();
		successorFutureStub = ReplicaGrpc.newFutureStub(successorChannel);
		successorBlockingStub = ReplicaGrpc.newBlockingStub(successorChannel);
	}

	public void destroySuccessorStubs()
	{
		successorIp = null;
		successorPort = 0;
		successorFutureStub = null;
		successorBlockingStub = null;
		if(successorChannel != null)
		{
			successorChannel.shutdownNow();
			successorChannel = null;
		}
	}
	public String[] getSuccessorConnectionInfo() {
		return successorConnectionInfo;
	}

	public void setSuccessorConnectionInfo(String successorConnectionInfo) {
		String[] connInfo = successorConnectionInfo.split(":");
		this.successorConnectionInfo = connInfo;
		this.successorIp = connInfo[0];
		this.successorPort = Integer.parseInt(connInfo[1]);
	}
	
	
	public String[] getPredecessorConnectionInfo() {
		return predecessorConnectionInfo;
	}

	public void setPredecessorConnectionInfo(String predecessorConnectionInfo) {
		String[] connInfo = predecessorConnectionInfo.split(":");
		this.predecessorConnectionInfo = connInfo;
		this.predecessorIp = connInfo[0];
		this.predecessorPort = Integer.parseInt(connInfo[1]);
	}

	public ReplicaFutureStub getPredecessorFutureStub() {
		return predecessorFutureStub;
	}

	public void setPredecessorFutureStub(ReplicaFutureStub predecessorFutureStub) {
		this.predecessorFutureStub = predecessorFutureStub;
	}

	public ReplicaBlockingStub getPredecessorBlockingStub() {
		return predecessorBlockingStub;
	}

	public void setPredecessorBlockingStub(ReplicaBlockingStub predecessorBlockingStub) {
		this.predecessorBlockingStub = predecessorBlockingStub;
	}

	public ReplicaFutureStub getSuccessorFutureStub() {
		return successorFutureStub;
	}

	public void setSuccessorFutureStub(ReplicaFutureStub successorFutureStub) {
		this.successorFutureStub = successorFutureStub;
	}

	public ReplicaBlockingStub getSuccessorBlockingStub() {
		return successorBlockingStub;
	}

	public void setSuccessorBlockingStub(ReplicaBlockingStub successorBlockingStub) {
		this.successorBlockingStub = successorBlockingStub;
	}
	
	
	

}
