package edu.sjsu.cs249.chain.replica;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import edu.sjsu.cs249.chain.service.ChainReplicaService;
import edu.sjsu.cs249.chain.service.HeadService;
import edu.sjsu.cs249.chain.service.TailService;
import edu.sjsu.cs249.chain.service.mode.MasterLiason;
import io.grpc.Server;
import io.grpc.ServerBuilder;

public class Main {
	private static final Logger log = org.apache.log4j.Logger.getLogger(Main.class);
	
	public static void main(String[] args) throws IOException, InterruptedException {
		//<inputzookeeper connection string> <control_path> <grpc port>
		
		String zkConnStr = args[0];
		String controlPath = args[1];
		String grpcPort = args[2];
		System.setProperty("file.name", args[3]);
		PropertyConfigurator.configure(args[5]);
		Main.log.info(zkConnStr + "," + controlPath + "," + grpcPort);
		
		Main.log.info("Replica State initalization: Begin");
		ReplicaState state = new ReplicaState(grpcPort);
		Main.log.info("Replica State initalization: Complete");
		
		ZkManager zkm = new ZkManager(zkConnStr, controlPath, state, args[4]);
		Main.log.info("Establishing connection to Master");
		zkm.connect(zkConnStr);
		Main.log.info("Connected to Master");
		ChainConnectionManager ccMan = new ChainConnectionManager();
		state.setChainConnMan(ccMan);
		state.setZkManager(zkm);
		MasterLiason ml = new MasterLiason(state);
		Thread mlt = new Thread(ml);
		mlt.start();
		
		ChainReplicaService replicaService = new ChainReplicaService(state);
		HeadService headService = new HeadService(state);
		TailService tailService = new TailService(state);
		
		Server server = ServerBuilder.forPort(Integer.parseInt(grpcPort)).
									addService(replicaService).
									addService(tailService).
									addService(headService).build();
		server.start();
		Main.log.info("Grpc Server started listeninging on " + grpcPort);
	}

}
