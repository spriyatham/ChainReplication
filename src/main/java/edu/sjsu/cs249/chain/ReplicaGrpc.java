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
public final class ReplicaGrpc {

  private ReplicaGrpc() {}

  public static final String SERVICE_NAME = "chain.Replica";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<edu.sjsu.cs249.chain.UpdateRequest,
      edu.sjsu.cs249.chain.UpdateResponse> getUpdateMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "update",
      requestType = edu.sjsu.cs249.chain.UpdateRequest.class,
      responseType = edu.sjsu.cs249.chain.UpdateResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<edu.sjsu.cs249.chain.UpdateRequest,
      edu.sjsu.cs249.chain.UpdateResponse> getUpdateMethod() {
    io.grpc.MethodDescriptor<edu.sjsu.cs249.chain.UpdateRequest, edu.sjsu.cs249.chain.UpdateResponse> getUpdateMethod;
    if ((getUpdateMethod = ReplicaGrpc.getUpdateMethod) == null) {
      synchronized (ReplicaGrpc.class) {
        if ((getUpdateMethod = ReplicaGrpc.getUpdateMethod) == null) {
          ReplicaGrpc.getUpdateMethod = getUpdateMethod =
              io.grpc.MethodDescriptor.<edu.sjsu.cs249.chain.UpdateRequest, edu.sjsu.cs249.chain.UpdateResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "update"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  edu.sjsu.cs249.chain.UpdateRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  edu.sjsu.cs249.chain.UpdateResponse.getDefaultInstance()))
              .setSchemaDescriptor(new ReplicaMethodDescriptorSupplier("update"))
              .build();
        }
      }
    }
    return getUpdateMethod;
  }

  private static volatile io.grpc.MethodDescriptor<edu.sjsu.cs249.chain.StateTransferRequest,
      edu.sjsu.cs249.chain.StateTransferResponse> getStateTransferMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "stateTransfer",
      requestType = edu.sjsu.cs249.chain.StateTransferRequest.class,
      responseType = edu.sjsu.cs249.chain.StateTransferResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<edu.sjsu.cs249.chain.StateTransferRequest,
      edu.sjsu.cs249.chain.StateTransferResponse> getStateTransferMethod() {
    io.grpc.MethodDescriptor<edu.sjsu.cs249.chain.StateTransferRequest, edu.sjsu.cs249.chain.StateTransferResponse> getStateTransferMethod;
    if ((getStateTransferMethod = ReplicaGrpc.getStateTransferMethod) == null) {
      synchronized (ReplicaGrpc.class) {
        if ((getStateTransferMethod = ReplicaGrpc.getStateTransferMethod) == null) {
          ReplicaGrpc.getStateTransferMethod = getStateTransferMethod =
              io.grpc.MethodDescriptor.<edu.sjsu.cs249.chain.StateTransferRequest, edu.sjsu.cs249.chain.StateTransferResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "stateTransfer"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  edu.sjsu.cs249.chain.StateTransferRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  edu.sjsu.cs249.chain.StateTransferResponse.getDefaultInstance()))
              .setSchemaDescriptor(new ReplicaMethodDescriptorSupplier("stateTransfer"))
              .build();
        }
      }
    }
    return getStateTransferMethod;
  }

  private static volatile io.grpc.MethodDescriptor<edu.sjsu.cs249.chain.AckRequest,
      edu.sjsu.cs249.chain.AckResponse> getAckMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "ack",
      requestType = edu.sjsu.cs249.chain.AckRequest.class,
      responseType = edu.sjsu.cs249.chain.AckResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<edu.sjsu.cs249.chain.AckRequest,
      edu.sjsu.cs249.chain.AckResponse> getAckMethod() {
    io.grpc.MethodDescriptor<edu.sjsu.cs249.chain.AckRequest, edu.sjsu.cs249.chain.AckResponse> getAckMethod;
    if ((getAckMethod = ReplicaGrpc.getAckMethod) == null) {
      synchronized (ReplicaGrpc.class) {
        if ((getAckMethod = ReplicaGrpc.getAckMethod) == null) {
          ReplicaGrpc.getAckMethod = getAckMethod =
              io.grpc.MethodDescriptor.<edu.sjsu.cs249.chain.AckRequest, edu.sjsu.cs249.chain.AckResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "ack"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  edu.sjsu.cs249.chain.AckRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  edu.sjsu.cs249.chain.AckResponse.getDefaultInstance()))
              .setSchemaDescriptor(new ReplicaMethodDescriptorSupplier("ack"))
              .build();
        }
      }
    }
    return getAckMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static ReplicaStub newStub(io.grpc.Channel channel) {
    return new ReplicaStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static ReplicaBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new ReplicaBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static ReplicaFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new ReplicaFutureStub(channel);
  }

  /**
   */
  public static abstract class ReplicaImplBase implements io.grpc.BindableService {

    /**
     */
    public void update(edu.sjsu.cs249.chain.UpdateRequest request,
        io.grpc.stub.StreamObserver<edu.sjsu.cs249.chain.UpdateResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getUpdateMethod(), responseObserver);
    }

    /**
     */
    public void stateTransfer(edu.sjsu.cs249.chain.StateTransferRequest request,
        io.grpc.stub.StreamObserver<edu.sjsu.cs249.chain.StateTransferResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getStateTransferMethod(), responseObserver);
    }

    /**
     */
    public void ack(edu.sjsu.cs249.chain.AckRequest request,
        io.grpc.stub.StreamObserver<edu.sjsu.cs249.chain.AckResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getAckMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getUpdateMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                edu.sjsu.cs249.chain.UpdateRequest,
                edu.sjsu.cs249.chain.UpdateResponse>(
                  this, METHODID_UPDATE)))
          .addMethod(
            getStateTransferMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                edu.sjsu.cs249.chain.StateTransferRequest,
                edu.sjsu.cs249.chain.StateTransferResponse>(
                  this, METHODID_STATE_TRANSFER)))
          .addMethod(
            getAckMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                edu.sjsu.cs249.chain.AckRequest,
                edu.sjsu.cs249.chain.AckResponse>(
                  this, METHODID_ACK)))
          .build();
    }
  }

  /**
   */
  public static final class ReplicaStub extends io.grpc.stub.AbstractStub<ReplicaStub> {
    private ReplicaStub(io.grpc.Channel channel) {
      super(channel);
    }

    private ReplicaStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected ReplicaStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new ReplicaStub(channel, callOptions);
    }

    /**
     */
    public void update(edu.sjsu.cs249.chain.UpdateRequest request,
        io.grpc.stub.StreamObserver<edu.sjsu.cs249.chain.UpdateResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getUpdateMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void stateTransfer(edu.sjsu.cs249.chain.StateTransferRequest request,
        io.grpc.stub.StreamObserver<edu.sjsu.cs249.chain.StateTransferResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getStateTransferMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void ack(edu.sjsu.cs249.chain.AckRequest request,
        io.grpc.stub.StreamObserver<edu.sjsu.cs249.chain.AckResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getAckMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class ReplicaBlockingStub extends io.grpc.stub.AbstractStub<ReplicaBlockingStub> {
    private ReplicaBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private ReplicaBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected ReplicaBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new ReplicaBlockingStub(channel, callOptions);
    }

    /**
     */
    public edu.sjsu.cs249.chain.UpdateResponse update(edu.sjsu.cs249.chain.UpdateRequest request) {
      return blockingUnaryCall(
          getChannel(), getUpdateMethod(), getCallOptions(), request);
    }

    /**
     */
    public edu.sjsu.cs249.chain.StateTransferResponse stateTransfer(edu.sjsu.cs249.chain.StateTransferRequest request) {
      return blockingUnaryCall(
          getChannel(), getStateTransferMethod(), getCallOptions(), request);
    }

    /**
     */
    public edu.sjsu.cs249.chain.AckResponse ack(edu.sjsu.cs249.chain.AckRequest request) {
      return blockingUnaryCall(
          getChannel(), getAckMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class ReplicaFutureStub extends io.grpc.stub.AbstractStub<ReplicaFutureStub> {
    private ReplicaFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private ReplicaFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected ReplicaFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new ReplicaFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<edu.sjsu.cs249.chain.UpdateResponse> update(
        edu.sjsu.cs249.chain.UpdateRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getUpdateMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<edu.sjsu.cs249.chain.StateTransferResponse> stateTransfer(
        edu.sjsu.cs249.chain.StateTransferRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getStateTransferMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<edu.sjsu.cs249.chain.AckResponse> ack(
        edu.sjsu.cs249.chain.AckRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getAckMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_UPDATE = 0;
  private static final int METHODID_STATE_TRANSFER = 1;
  private static final int METHODID_ACK = 2;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final ReplicaImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(ReplicaImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_UPDATE:
          serviceImpl.update((edu.sjsu.cs249.chain.UpdateRequest) request,
              (io.grpc.stub.StreamObserver<edu.sjsu.cs249.chain.UpdateResponse>) responseObserver);
          break;
        case METHODID_STATE_TRANSFER:
          serviceImpl.stateTransfer((edu.sjsu.cs249.chain.StateTransferRequest) request,
              (io.grpc.stub.StreamObserver<edu.sjsu.cs249.chain.StateTransferResponse>) responseObserver);
          break;
        case METHODID_ACK:
          serviceImpl.ack((edu.sjsu.cs249.chain.AckRequest) request,
              (io.grpc.stub.StreamObserver<edu.sjsu.cs249.chain.AckResponse>) responseObserver);
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

  private static abstract class ReplicaBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    ReplicaBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return edu.sjsu.cs249.chain.Chain.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("Replica");
    }
  }

  private static final class ReplicaFileDescriptorSupplier
      extends ReplicaBaseDescriptorSupplier {
    ReplicaFileDescriptorSupplier() {}
  }

  private static final class ReplicaMethodDescriptorSupplier
      extends ReplicaBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    ReplicaMethodDescriptorSupplier(String methodName) {
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
      synchronized (ReplicaGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new ReplicaFileDescriptorSupplier())
              .addMethod(getUpdateMethod())
              .addMethod(getStateTransferMethod())
              .addMethod(getAckMethod())
              .build();
        }
      }
    }
    return result;
  }
}
