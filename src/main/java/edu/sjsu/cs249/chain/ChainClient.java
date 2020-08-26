package edu.sjsu.cs249.chain;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class ChainClient implements Watcher {
    private ZooKeeper zk;
    private String currentChainTail="";
    private String currentChainHead="";
    private TailChainReplicaGrpc.TailChainReplicaFutureStub  chainTail;
    private HeadChainReplicaGrpc.HeadChainReplicaFutureStub  chainHead;
    private AtomicBoolean retryChainCheck = new AtomicBoolean();

    private void setChainHead(String session) throws KeeperException, InterruptedException {
        if (currentChainHead.equals(session)) {
            return;
        }
        chainHead = getHeadStub(session);
        currentChainHead = session;
    }

    private HeadChainReplicaGrpc.HeadChainReplicaFutureStub getHeadStub(String session) throws KeeperException, InterruptedException {
        byte data[] = zk.getData("/chain/" + session, false, null);
        InetSocketAddress addr = str2addr(new String(data).split("\n")[0]);
        ManagedChannel channel = ManagedChannelBuilder.forAddress(addr.getHostName(), addr.getPort()).usePlaintext().build();
        return HeadChainReplicaGrpc.newFutureStub(channel);
    }

    private TailChainReplicaGrpc.TailChainReplicaFutureStub getTailStub(String session) throws KeeperException, InterruptedException {
        byte data[] = zk.getData("/chain/" + session, false, null);
        InetSocketAddress addr = str2addr(new String(data).split("\n")[0]);
        ManagedChannel channel = ManagedChannelBuilder.forAddress(addr.getHostName(), addr.getPort()).usePlaintext().build();
        return TailChainReplicaGrpc.newFutureStub(channel);
    }
    
    private void setChainTail(String session) throws KeeperException, InterruptedException {
        if (currentChainTail.equals(session)) {
            return;
        }
        chainTail = null;
        chainTail = getTailStub(session);
        currentChainTail = session;
    }

    private static InetSocketAddress str2addr(String addr) {
        int colon = addr.lastIndexOf(':');
        return new InetSocketAddress(addr.substring(0, colon), Integer.parseInt(addr.substring(colon+1).trim()));
    }

    private ChainClient(String zkServer) throws KeeperException, InterruptedException, IOException {
        zk = new ZooKeeper(zkServer, 3000, this);
        checkHeadTail();
        new ScheduledThreadPoolExecutor(1).scheduleAtFixedRate(
                () -> { if (retryChainCheck.getAndSet(false)) checkHeadTail();},
                5,
                5,
                TimeUnit.SECONDS
        );
    }

    private void get(String key) {
        ListenableFuture<GetResponse> resFuture = chainTail.get(GetRequest.newBuilder().setKey(key).build());
        Futures.addCallback(resFuture, new FutureCallback<GetResponse>() {

			@Override
			public void onSuccess(GetResponse rsp) {
				if (rsp.getRc() != 0) {
		            System.out.printf("Error %d occurred\n", rsp.getRc());
		        } else {
		            System.out.printf("%d\n", rsp.getValue());
		        }
			}

			@Override
			public void onFailure(Throwable t) {
				
				
			}
		}, MoreExecutors.directExecutor());
        
    }

 /*
    private void del(String key) {
        HeadResponse rsp = chainHead.delete(DeleteRequest.newBuilder().setKey(key).build());
        if (rsp.getRc() != 0) {
            System.out.printf("Error %d occurred\n", rsp.getRc());
        } else {
            System.out.print("%s deleted\n");
        }
    }
 */
    private void inc(String key, int val) {
        ListenableFuture<HeadResponse> rspFuture = chainHead.increment(IncRequest.newBuilder().setKey(key).setIncValue(val).build());
        Futures.addCallback(rspFuture, new FutureCallback<HeadResponse>() {

			@Override
			public void onSuccess(HeadResponse rsp) {
				if (rsp.getRc() != 0) {
		            System.out.printf("Error %d occurred\n", rsp.getRc());
		        } else {
		            System.out.printf("%s incremented by %d\n", key, val);
		        }
				
			}

			@Override
			public void onFailure(Throwable t) {
				// TODO Auto-generated method stub
				
			}
		}, MoreExecutors.directExecutor());
    }

    public static void main(String args[]) throws Exception {
    	System.out.println("args[0]" + args[0]);
        ChainClient client = new ChainClient(args[0]);
        
        /*
        Scanner scanner = new Scanner(System.in);
        String line;
        while ((line = scanner.nextLine()) != null) {
            String parts[] = line.split(" ");
            if (parts[0].equals("get")) {
                client.get(parts[1]);
            } else if (parts[0].equals("inc")) {
                client.inc(parts[1], Integer.parseInt(parts[2]));
            } else {
                System.out.println("don't know " + parts[0]);
                System.out.println("i know:");
                System.out.println("get key");
                System.out.println("inc key value");
                System.out.println("del key");
            }
        }
        */
        Thread.sleep(50000);
        System.out.println("I am up and ready.!");
        //String[] commands = new String[]{"inc", "get"};
        
        Random rand = new Random();
        
        for(int i = 1 ; i < 1000; i++)
        {
        	String key =  (char)(rand.nextInt(26) + 97)+""; 
        	if(i % 17 == 0)
        	{
        		client.get(key);
        		continue;
        	}
        	System.out.println("Increment Key = " + key + " value = " + 1);
        	client.inc(key, 1);
        }
        
    }

    private void checkHeadTail() {
        try {
            List<String> children = zk.getChildren("/chain", true);
            children.sort(new Comparator<String>() {
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
            String head = children.get(0);
            String tail = children.get(children.size() - 1);
            setChainHead(head);
            setChainTail(tail);
        } catch (KeeperException | InterruptedException e) {
            retryChainCheck.set(true);
        }
    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        if (watchedEvent.getState().equals(Event.KeeperState.Expired) || watchedEvent.getState().equals(Event.KeeperState.Closed)) {
            System.err.println("disconnected from zookeeper");
            System.exit(2);
        }
        checkHeadTail();
    }
}
