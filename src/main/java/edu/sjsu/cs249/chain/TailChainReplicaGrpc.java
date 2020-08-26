package edu.sjsu.cs249.chain;

import static io.grpc.MethodDescriptor.generateFullMethodName;
import static io.grpc.stub.ClientCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ClientCalls.asyncClientStreamingCall;
import static io.grpc.stub.ClientCalls.asyncServerStreamingCall;
import static io.grpc.stub.ClientCalls.asyncUnaryCall;
import static io.grpc.stub.ClientCalls.blockingServerStreamingCall;
import static io.grpc.stub.ClientCalls.blockingUnaryCall;
import static io.grpc.stub.ClientCalls.futureUnaryCall;
import static io.grpc.stub.ServerCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ServerCalls.asyncClientStreamingCall;
import static io.grpc.stub.ServerCalls.asyncServerStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.23.0)",
    comments = "Source: chain.proto")
public final class TailChainReplicaGrpc {

  private TailChainReplicaGrpc() {}

  public static final String SERVICE_NAME = "chain.TailChainReplica";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<edu.sjsu.cs249.chain.GetRequest,
      edu.sjsu.cs249.chain.GetResponse> getGetMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "get",
      requestType = edu.sjsu.cs249.chain.GetRequest.class,
      responseType = edu.sjsu.cs249.chain.GetResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<edu.sjsu.cs249.chain.GetRequest,
      edu.sjsu.cs249.chain.GetResponse> getGetMethod() {
    io.grpc.MethodDescriptor<edu.sjsu.cs249.chain.GetRequest, edu.sjsu.cs249.chain.GetResponse> getGetMethod;
    if ((getGetMethod = TailChainReplicaGrpc.getGetMethod) == null) {
      synchronized (TailChainReplicaGrpc.class) {
        if ((getGetMethod = TailChainReplicaGrpc.getGetMethod) == null) {
          TailChainReplicaGrpc.getGetMethod = getGetMethod =
              io.grpc.MethodDescriptor.<edu.sjsu.cs249.chain.GetRequest, edu.sjsu.cs249.chain.GetResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "get"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  edu.sjsu.cs249.chain.GetRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  edu.sjsu.cs249.chain.GetResponse.getDefaultInstance()))
              .setSchemaDescriptor(new TailChainReplicaMethodDescriptorSupplier("get"))
              .build();
        }
      }
    }
    return getGetMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static TailChainReplicaStub newStub(io.grpc.Channel channel) {
    return new TailChainReplicaStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static TailChainReplicaBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new TailChainReplicaBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static TailChainReplicaFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new TailChainReplicaFutureStub(channel);
  }

  /**
   */
  public static abstract class TailChainReplicaImplBase implements io.grpc.BindableService {

    /**
     */
    public void get(edu.sjsu.cs249.chain.GetRequest request,
        io.grpc.stub.StreamObserver<edu.sjsu.cs249.chain.GetResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getGetMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getGetMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                edu.sjsu.cs249.chain.GetRequest,
                edu.sjsu.cs249.chain.GetResponse>(
                  this, METHODID_GET)))
          .build();
    }
  }

  /**
   */
  public static final class TailChainReplicaStub extends io.grpc.stub.AbstractStub<TailChainReplicaStub> {
    private TailChainReplicaStub(io.grpc.Channel channel) {
      super(channel);
    }

    private TailChainReplicaStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected TailChainReplicaStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new TailChainReplicaStub(channel, callOptions);
    }

    /**
     */
    public void get(edu.sjsu.cs249.chain.GetRequest request,
        io.grpc.stub.StreamObserver<edu.sjsu.cs249.chain.GetResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getGetMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class TailChainReplicaBlockingStub extends io.grpc.stub.AbstractStub<TailChainReplicaBlockingStub> {
    private TailChainReplicaBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private TailChainReplicaBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected TailChainReplicaBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new TailChainReplicaBlockingStub(channel, callOptions);
    }

    /**
     */
    public edu.sjsu.cs249.chain.GetResponse get(edu.sjsu.cs249.chain.GetRequest request) {
      return blockingUnaryCall(
          getChannel(), getGetMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class TailChainReplicaFutureStub extends io.grpc.stub.AbstractStub<TailChainReplicaFutureStub> {
    private TailChainReplicaFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private TailChainReplicaFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected TailChainReplicaFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new TailChainReplicaFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<edu.sjsu.cs249.chain.GetResponse> get(
        edu.sjsu.cs249.chain.GetRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getGetMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_GET = 0;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final TailChainReplicaImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(TailChainReplicaImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_GET:
          serviceImpl.get((edu.sjsu.cs249.chain.GetRequest) request,
              (io.grpc.stub.StreamObserver<edu.sjsu.cs249.chain.GetResponse>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  private static abstract class TailChainReplicaBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    TailChainReplicaBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return edu.sjsu.cs249.chain.Chain.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("TailChainReplica");
    }
  }

  private static final class TailChainReplicaFileDescriptorSupplier
      extends TailChainReplicaBaseDescriptorSupplier {
    TailChainReplicaFileDescriptorSupplier() {}
  }

  private static final class TailChainReplicaMethodDescriptorSupplier
      extends TailChainReplicaBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    TailChainReplicaMethodDescriptorSupplier(String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (TailChainReplicaGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new TailChainReplicaFileDescriptorSupplier())
              .addMethod(getGetMethod())
              .build();
        }
      }
    }
    return result;
  }
}
