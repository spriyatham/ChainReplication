package edu.sjsu.cs249.chain.service;

import org.apache.log4j.Logger;

import edu.sjsu.cs249.chain.GetRequest;
import edu.sjsu.cs249.chain.GetResponse;
import edu.sjsu.cs249.chain.GetResponse.Builder;
import edu.sjsu.cs249.chain.TailChainReplicaGrpc.TailChainReplicaImplBase;
import edu.sjsu.cs249.chain.replica.ReplicaState;
import io.grpc.stub.StreamObserver;

/**
 * 
service TailChainReplica {
    rpc get(GetRequest) returns (GetResponse);
}

message GetRequest {
    string key = 1;
}

message GetResponse {
   // rc = 0 means success, rc = 1 i'm not the tail
    uint32 rc = 1;
    int32 value = 2;
}
 * 
 * */

public class TailService extends TailChainReplicaImplBase {
	ReplicaState state;
	
	public TailService(ReplicaState state) {
		this.state = state;
	}
	private static final Logger logger = Logger.getLogger("TailService");
	@Override
	public void get(GetRequest request, StreamObserver<GetResponse> responseObserver) {
		
		Builder getRespBuilder =  GetResponse.newBuilder();
		
		String key = request.getKey();
		logger.info("Recieved a get request for the key : " + key);
		
		//TODO: computation should be don and the response should be wrtitten to the responseObserver object
		//TODO: Check if you are the tail...
		if(!state.getTail().get())
		{
			logger.info("I  am not the tail..returning 1");
			GetResponse resp = getRespBuilder.setRc(1).setValue(-1).build();
			responseObserver.onNext(resp);
			responseObserver.onCompleted();
			return;
		}
		
		if(state.recievingState.get())
		{
			logger.info("Inward state transfer in progress, waiting for it to get completed, before I can service the query.");
			try {
				synchronized (state.recievingStateObj) {
					state.recievingStateObj.wait();
				}
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();	
			}
			logger.info("Inward state transfer completed, proceeding...");
		}
		
		Integer value = state.getHashTable().get(key);
		if(value == null) value = 0;
		logger.info("Returning the value : " + value);
		responseObserver.onNext(getRespBuilder.setRc(0).setValue(value).build());
		responseObserver.onCompleted();
	}
	
}
