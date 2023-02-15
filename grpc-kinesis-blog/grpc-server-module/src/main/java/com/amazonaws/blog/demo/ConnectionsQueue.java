package com.amazonaws.blog.demo;

import com.amazonaws.blog.demo.grpc.DemoStreamingServiceOuterClass;
import io.grpc.stub.StreamObserver;
import lombok.Getter;

import java.util.ArrayList;

@Getter

/**
 * This Queue will keep track of all the active connections
 */
public enum ConnectionsQueue {
    INSTANCE;

    private volatile ArrayList<StreamObserver<DemoStreamingServiceOuterClass.DemoStreamingServiceResponse>> activeConnections = new ArrayList<>();

    public static void addConnection(StreamObserver<DemoStreamingServiceOuterClass.DemoStreamingServiceResponse> newConnection){
        System.out.println("Adding new connection to ConnectionsQueue" + newConnection.toString() );
        INSTANCE.activeConnections.add(newConnection);
    }
    public static void removeConnection(StreamObserver<DemoStreamingServiceOuterClass.DemoStreamingServiceResponse> oldConnection){
        System.out.println("Removing new connection to ConnectionsQueue" + oldConnection.toString());
        INSTANCE.activeConnections.remove(oldConnection);
    }

}

