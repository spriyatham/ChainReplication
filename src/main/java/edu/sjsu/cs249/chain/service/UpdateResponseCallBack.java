package edu.sjsu.cs249.chain.service;

import org.apache.log4j.Logger;

import com.google.common.util.concurrent.FutureCallback;

import edu.sjsu.cs249.chain.UpdateResponse;
import edu.sjsu.cs249.chain.replica.ReplicaState;

public class UpdateResponseCallBack implements FutureCallback<UpdateResponse> {
	private static final Logger logger = Logger.getLogger("UpdateResponseCallBack");
	int xid;
	ReplicaState state;
	
	public UpdateResponseCallBack(int xid, ReplicaState State)
	{
		this.xid = xid;
		this.state = state;
	}
	
	@Override
	public void onSuccess(UpdateResponse result) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onFailure(Throwable t) {
		logger.error("The update failed, because of :", t);
		Boolean waitObj = null;
		if(state.ackWaitMap == null)
		{
			logger.info("THe ackWaitMap is null..so returning..");
			return;
		}
		logger.info("THe ackWaitMap is not..so returning..");
		synchronized (state.ackWaitMap) {
			waitObj = state.ackWaitMap.get(xid);
		}
		if(waitObj != null)
		{
			synchronized (waitObj) {
				waitObj = Boolean.FALSE;//because the operation failed.
				waitObj.notify();
			}
		}
		//TODO: I might also have to remove from the sent map..because it failed...
		//And retry it..,how many retries though?
		//if i dont retry.. i have to revert the state of the system..
		//even if I revert, I cannot transmit the failure back through the chain..since we only have ACK...
	}

}
