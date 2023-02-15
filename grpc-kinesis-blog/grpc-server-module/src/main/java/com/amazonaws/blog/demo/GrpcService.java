package com.amazonaws.blog.demo;

import com.amazonaws.blog.demo.grpc.DemoStreamingServiceGrpc;
import com.amazonaws.blog.demo.grpc.DemoStreamingServiceOuterClass;
import com.google.rpc.Status;
import io.grpc.Context;
import io.grpc.stub.StreamObserver;
import org.apache.log4j.Logger;

public class GrpcService extends DemoStreamingServiceGrpc.DemoStreamingServiceImplBase {
    private final static Logger logger = Logger.getLogger(GrpcServer.class);
    @Override
    public void startStream(DemoStreamingServiceOuterClass.StartStreamRequest request, StreamObserver<DemoStreamingServiceOuterClass.DemoStreamingServiceResponse> responseObserver) {
        try{
            ConnectionsQueue.addConnection(responseObserver);

            while (!Context.current().isCancelled()) {
                responseObserver.onNext(DemoStreamingServiceOuterClass
                        .DemoStreamingServiceResponse
                        .newBuilder()
                        .setStatus(Status.newBuilder().setMessage("connected").build())
                        .setData("TimeStamp: " + System.currentTimeMillis())
                        .build());
                Thread.sleep(30000);
            }
            if (Context.current().isCancelled()) {
                ConnectionsQueue.removeConnection(responseObserver);
            }

        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void stopStream(DemoStreamingServiceOuterClass.StopStreamRequest request, StreamObserver<DemoStreamingServiceOuterClass.DemoStreamingServiceResponse> responseObserver) {
        super.stopStream(request, responseObserver);
        //todo implement stop
        logger.info("server stopped");
    }
}