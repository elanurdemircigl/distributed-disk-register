package com.hatokuse.grpc;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 * <pre>
 * Servis Tanımı: Üye (Member) sunucusu bu fonksiyonları dışarıya açar
 * </pre>
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.67.1)",
    comments = "Source: hatokuse.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class MemberServiceGrpc {

  private MemberServiceGrpc() {}

  public static final java.lang.String SERVICE_NAME = "MemberService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<com.hatokuse.grpc.MessageRequest,
      com.hatokuse.grpc.MessageResponse> getStoreMessageMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "StoreMessage",
      requestType = com.hatokuse.grpc.MessageRequest.class,
      responseType = com.hatokuse.grpc.MessageResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.hatokuse.grpc.MessageRequest,
      com.hatokuse.grpc.MessageResponse> getStoreMessageMethod() {
    io.grpc.MethodDescriptor<com.hatokuse.grpc.MessageRequest, com.hatokuse.grpc.MessageResponse> getStoreMessageMethod;
    if ((getStoreMessageMethod = MemberServiceGrpc.getStoreMessageMethod) == null) {
      synchronized (MemberServiceGrpc.class) {
        if ((getStoreMessageMethod = MemberServiceGrpc.getStoreMessageMethod) == null) {
          MemberServiceGrpc.getStoreMessageMethod = getStoreMessageMethod =
              io.grpc.MethodDescriptor.<com.hatokuse.grpc.MessageRequest, com.hatokuse.grpc.MessageResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "StoreMessage"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.hatokuse.grpc.MessageRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.hatokuse.grpc.MessageResponse.getDefaultInstance()))
              .setSchemaDescriptor(new MemberServiceMethodDescriptorSupplier("StoreMessage"))
              .build();
        }
      }
    }
    return getStoreMessageMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.hatokuse.grpc.RetrieveRequest,
      com.hatokuse.grpc.RetrieveResponse> getRetrieveMessageMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "RetrieveMessage",
      requestType = com.hatokuse.grpc.RetrieveRequest.class,
      responseType = com.hatokuse.grpc.RetrieveResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.hatokuse.grpc.RetrieveRequest,
      com.hatokuse.grpc.RetrieveResponse> getRetrieveMessageMethod() {
    io.grpc.MethodDescriptor<com.hatokuse.grpc.RetrieveRequest, com.hatokuse.grpc.RetrieveResponse> getRetrieveMessageMethod;
    if ((getRetrieveMessageMethod = MemberServiceGrpc.getRetrieveMessageMethod) == null) {
      synchronized (MemberServiceGrpc.class) {
        if ((getRetrieveMessageMethod = MemberServiceGrpc.getRetrieveMessageMethod) == null) {
          MemberServiceGrpc.getRetrieveMessageMethod = getRetrieveMessageMethod =
              io.grpc.MethodDescriptor.<com.hatokuse.grpc.RetrieveRequest, com.hatokuse.grpc.RetrieveResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "RetrieveMessage"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.hatokuse.grpc.RetrieveRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.hatokuse.grpc.RetrieveResponse.getDefaultInstance()))
              .setSchemaDescriptor(new MemberServiceMethodDescriptorSupplier("RetrieveMessage"))
              .build();
        }
      }
    }
    return getRetrieveMessageMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static MemberServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<MemberServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<MemberServiceStub>() {
        @java.lang.Override
        public MemberServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new MemberServiceStub(channel, callOptions);
        }
      };
    return MemberServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static MemberServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<MemberServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<MemberServiceBlockingStub>() {
        @java.lang.Override
        public MemberServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new MemberServiceBlockingStub(channel, callOptions);
        }
      };
    return MemberServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static MemberServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<MemberServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<MemberServiceFutureStub>() {
        @java.lang.Override
        public MemberServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new MemberServiceFutureStub(channel, callOptions);
        }
      };
    return MemberServiceFutureStub.newStub(factory, channel);
  }

  /**
   * <pre>
   * Servis Tanımı: Üye (Member) sunucusu bu fonksiyonları dışarıya açar
   * </pre>
   */
  public interface AsyncService {

    /**
     * <pre>
     * Lider, bir üyeye mesajı kaydetmesi için bu fonksiyonu çağırır
     * </pre>
     */
    default void storeMessage(com.hatokuse.grpc.MessageRequest request,
        io.grpc.stub.StreamObserver<com.hatokuse.grpc.MessageResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getStoreMessageMethod(), responseObserver);
    }

    /**
     */
    default void retrieveMessage(com.hatokuse.grpc.RetrieveRequest request,
        io.grpc.stub.StreamObserver<com.hatokuse.grpc.RetrieveResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getRetrieveMessageMethod(), responseObserver);
    }
  }

  /**
   * Base class for the server implementation of the service MemberService.
   * <pre>
   * Servis Tanımı: Üye (Member) sunucusu bu fonksiyonları dışarıya açar
   * </pre>
   */
  public static abstract class MemberServiceImplBase
      implements io.grpc.BindableService, AsyncService {

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return MemberServiceGrpc.bindService(this);
    }
  }

  /**
   * A stub to allow clients to do asynchronous rpc calls to service MemberService.
   * <pre>
   * Servis Tanımı: Üye (Member) sunucusu bu fonksiyonları dışarıya açar
   * </pre>
   */
  public static final class MemberServiceStub
      extends io.grpc.stub.AbstractAsyncStub<MemberServiceStub> {
    private MemberServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected MemberServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new MemberServiceStub(channel, callOptions);
    }

    /**
     * <pre>
     * Lider, bir üyeye mesajı kaydetmesi için bu fonksiyonu çağırır
     * </pre>
     */
    public void storeMessage(com.hatokuse.grpc.MessageRequest request,
        io.grpc.stub.StreamObserver<com.hatokuse.grpc.MessageResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getStoreMessageMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void retrieveMessage(com.hatokuse.grpc.RetrieveRequest request,
        io.grpc.stub.StreamObserver<com.hatokuse.grpc.RetrieveResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getRetrieveMessageMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * A stub to allow clients to do synchronous rpc calls to service MemberService.
   * <pre>
   * Servis Tanımı: Üye (Member) sunucusu bu fonksiyonları dışarıya açar
   * </pre>
   */
  public static final class MemberServiceBlockingStub
      extends io.grpc.stub.AbstractBlockingStub<MemberServiceBlockingStub> {
    private MemberServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected MemberServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new MemberServiceBlockingStub(channel, callOptions);
    }

    /**
     * <pre>
     * Lider, bir üyeye mesajı kaydetmesi için bu fonksiyonu çağırır
     * </pre>
     */
    public com.hatokuse.grpc.MessageResponse storeMessage(com.hatokuse.grpc.MessageRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getStoreMessageMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.hatokuse.grpc.RetrieveResponse retrieveMessage(com.hatokuse.grpc.RetrieveRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getRetrieveMessageMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do ListenableFuture-style rpc calls to service MemberService.
   * <pre>
   * Servis Tanımı: Üye (Member) sunucusu bu fonksiyonları dışarıya açar
   * </pre>
   */
  public static final class MemberServiceFutureStub
      extends io.grpc.stub.AbstractFutureStub<MemberServiceFutureStub> {
    private MemberServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected MemberServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new MemberServiceFutureStub(channel, callOptions);
    }

    /**
     * <pre>
     * Lider, bir üyeye mesajı kaydetmesi için bu fonksiyonu çağırır
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.hatokuse.grpc.MessageResponse> storeMessage(
        com.hatokuse.grpc.MessageRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getStoreMessageMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.hatokuse.grpc.RetrieveResponse> retrieveMessage(
        com.hatokuse.grpc.RetrieveRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getRetrieveMessageMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_STORE_MESSAGE = 0;
  private static final int METHODID_RETRIEVE_MESSAGE = 1;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final AsyncService serviceImpl;
    private final int methodId;

    MethodHandlers(AsyncService serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_STORE_MESSAGE:
          serviceImpl.storeMessage((com.hatokuse.grpc.MessageRequest) request,
              (io.grpc.stub.StreamObserver<com.hatokuse.grpc.MessageResponse>) responseObserver);
          break;
        case METHODID_RETRIEVE_MESSAGE:
          serviceImpl.retrieveMessage((com.hatokuse.grpc.RetrieveRequest) request,
              (io.grpc.stub.StreamObserver<com.hatokuse.grpc.RetrieveResponse>) responseObserver);
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

  public static final io.grpc.ServerServiceDefinition bindService(AsyncService service) {
    return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
        .addMethod(
          getStoreMessageMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.hatokuse.grpc.MessageRequest,
              com.hatokuse.grpc.MessageResponse>(
                service, METHODID_STORE_MESSAGE)))
        .addMethod(
          getRetrieveMessageMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.hatokuse.grpc.RetrieveRequest,
              com.hatokuse.grpc.RetrieveResponse>(
                service, METHODID_RETRIEVE_MESSAGE)))
        .build();
  }

  private static abstract class MemberServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    MemberServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return com.hatokuse.grpc.HatokuseProto.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("MemberService");
    }
  }

  private static final class MemberServiceFileDescriptorSupplier
      extends MemberServiceBaseDescriptorSupplier {
    MemberServiceFileDescriptorSupplier() {}
  }

  private static final class MemberServiceMethodDescriptorSupplier
      extends MemberServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final java.lang.String methodName;

    MemberServiceMethodDescriptorSupplier(java.lang.String methodName) {
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
      synchronized (MemberServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new MemberServiceFileDescriptorSupplier())
              .addMethod(getStoreMessageMethod())
              .addMethod(getRetrieveMessageMethod())
              .build();
        }
      }
    }
    return result;
  }
}
