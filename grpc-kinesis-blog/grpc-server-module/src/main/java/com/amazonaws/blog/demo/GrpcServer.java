package com.amazonaws.blog.demo;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.apache.log4j.Logger;

import java.io.IOException;

/**
 * Grpc Server
 *
 */
public class GrpcServer {
    private final int serverPort;
    private final Server server;
    private final ServiceRegisterer sr;
    private final  ServerBuilder sb;
    private final static Logger logger = Logger.getLogger(GrpcServer.class);
    private static final SubscribeAndObserve subscribeAndObserve = new SubscribeAndObserve();;


    private GrpcServer(int port) {
        this.serverPort = port;
        this.sb= ServerBuilder
                .forPort(port);
        this.sr =  new ServiceRegisterer(sb);
        // register services...
        sr.registerService(new GrpcService());

        sb.intercept(new GrpcServiceInterceptor(sr.getOptionsRegistry()));

        this.server = sb.build();

    }

    /** Start serving requests. */
    void start() throws IOException {
        server.start();
        subscribeAndObserve.start();
        logger.info("Server started, listening on port:" + serverPort);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("shutting down gRPC server since JVM is shutting down");
            GrpcServer.this.stop();
            logger.info("server is shutdown");
        }));
    }

    /** Stop serving requests and shutdown resources. */
    void stop() {
        if (server != null) {
            server.shutdown();
            subscribeAndObserve.stop();
        }
    }

    /**
     * Await termination on the main thread since the grpc library uses daemon threads.
     */
    private void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        GrpcServer server = null;
        server = new GrpcServer(9090);
        server.start();
        server.blockUntilShutdown();
    }
}