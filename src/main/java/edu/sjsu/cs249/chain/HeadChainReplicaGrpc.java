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
public final class HeadChainReplicaGrpc {

  private HeadChainReplicaGrpc() {}

  public static final String SERVICE_NAME = "chain.HeadChainReplica";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<edu.sjsu.cs249.chain.IncRequest,
      edu.sjsu.cs249.chain.HeadResponse> getIncrementMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "increment",
      requestType = edu.sjsu.cs249.chain.IncRequest.class,
      responseType = edu.sjsu.cs249.chain.HeadResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<edu.sjsu.cs249.chain.IncRequest,
      edu.sjsu.cs249.chain.HeadResponse> getIncrementMethod() {
    io.grpc.MethodDescriptor<edu.sjsu.cs249.chain.IncRequest, edu.sjsu.cs249.chain.HeadResponse> getIncrementMethod;
    if ((getIncrementMethod = HeadChainReplicaGrpc.getIncrementMethod) == null) {
      synchronized (HeadChainReplicaGrpc.class) {
        if ((getIncrementMethod = HeadChainReplicaGrpc.getIncrementMethod) == null) {
          HeadChainReplicaGrpc.getIncrementMethod = getIncrementMethod =
              io.grpc.MethodDescriptor.<edu.sjsu.cs249.chain.IncRequest, edu.sjsu.cs249.chain.HeadResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "increment"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  edu.sjsu.cs249.chain.IncRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  edu.sjsu.cs249.chain.HeadResponse.getDefaultInstance()))
              .setSchemaDescriptor(new HeadChainReplicaMethodDescriptorSupplier("increment"))
              .build();
        }
      }
    }
    return getIncrementMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static HeadChainReplicaStub newStub(io.grpc.Channel channel) {
    return new HeadChainReplicaStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static HeadChainReplicaBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new HeadChainReplicaBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static HeadChainReplicaFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new HeadChainReplicaFutureStub(channel);
  }

  /**
   */
  public static abstract class HeadChainReplicaImplBase implements io.grpc.BindableService {

    /**
     */
    public void increment(edu.sjsu.cs249.chain.IncRequest request,
        io.grpc.stub.StreamObserver<edu.sjsu.cs249.chain.HeadResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getIncrementMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getIncrementMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                edu.sjsu.cs249.chain.IncRequest,
                edu.sjsu.cs249.chain.HeadResponse>(
                  this, METHODID_INCREMENT)))
          .build();
    }
  }

  /**
   */
  public static final class HeadChainReplicaStub extends io.grpc.stub.AbstractStub<HeadChainReplicaStub> {
    private HeadChainReplicaStub(io.grpc.Channel channel) {
      super(channel);
    }

    private HeadChainReplicaStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected HeadChainReplicaStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new HeadChainReplicaStub(channel, callOptions);
    }

    /**
     */
    public void increment(edu.sjsu.cs249.chain.IncRequest request,
        io.grpc.stub.StreamObserver<edu.sjsu.cs249.chain.HeadResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getIncrementMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class HeadChainReplicaBlockingStub extends io.grpc.stub.AbstractStub<HeadChainReplicaBlockingStub> {
    private HeadChainReplicaBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private HeadChainReplicaBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected HeadChainReplicaBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new HeadChainReplicaBlockingStub(channel, callOptions);
    }

    /**
     */
    public edu.sjsu.cs249.chain.HeadResponse increment(edu.sjsu.cs249.chain.IncRequest request) {
      return blockingUnaryCall(
          getChannel(), getIncrementMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class HeadChainReplicaFutureStub extends io.grpc.stub.AbstractStub<HeadChainReplicaFutureStub> {
    private HeadChainReplicaFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private HeadChainReplicaFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected HeadChainReplicaFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new HeadChainReplicaFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<edu.sjsu.cs249.chain.HeadResponse> increment(
        edu.sjsu.cs249.chain.IncRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getIncrementMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_INCREMENT = 0;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final HeadChainReplicaImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(HeadChainReplicaImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_INCREMENT:
          serviceImpl.increment((edu.sjsu.cs249.chain.IncRequest) request,
              (io.grpc.stub.StreamObserver<edu.sjsu.cs249.chain.HeadResponse>) responseObserver);
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

  private static abstract class HeadChainReplicaBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    HeadChainReplicaBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return edu.sjsu.cs249.chain.Chain.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("HeadChainReplica");
    }
  }

  private static final class HeadChainReplicaFileDescriptorSupplier
      extends HeadChainReplicaBaseDescriptorSupplier {
    HeadChainReplicaFileDescriptorSupplier() {}
  }

  private static final class HeadChainReplicaMethodDescriptorSupplier
      extends HeadChainReplicaBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    HeadChainReplicaMethodDescriptorSupplier(String methodName) {
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
      synchronized (HeadChainReplicaGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new HeadChainReplicaFileDescriptorSupplier())
              .addMethod(getIncrementMethod())
              .build();
        }
      }
    }
    return result;
  }
}
