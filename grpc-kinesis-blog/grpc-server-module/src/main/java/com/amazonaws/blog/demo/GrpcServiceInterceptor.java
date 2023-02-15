package com.amazonaws.blog.demo;

import com.google.protobuf.DescriptorProtos;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import io.grpc.Status;
import lombok.SneakyThrows;

public class GrpcServiceInterceptor implements ServerInterceptor {

    private final MethodOptionsRegistry reg;

    public GrpcServiceInterceptor(MethodOptionsRegistry reg) {
        this.reg = reg;
    }

    @SneakyThrows
    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
            ServerCall<ReqT, RespT> serverCall,
            Metadata metadata,
            ServerCallHandler<ReqT, RespT> serverCallHandler) {
        String name = serverCall.getMethodDescriptor().getFullMethodName();
        DescriptorProtos.MethodOptions opts = reg.get(name);

        String key = metadata.get(Metadata.Key.of("authorization",Metadata.ASCII_STRING_MARSHALLER));

        Boolean isAuthenticated = AuthService.authorize(key);
        if(isAuthenticated){
            return serverCallHandler.startCall(serverCall, metadata);
        }
        serverCall.close(Status.UNAUTHENTICATED,new Metadata());
        return serverCallHandler.startCall(serverCall, metadata);
    }
}
