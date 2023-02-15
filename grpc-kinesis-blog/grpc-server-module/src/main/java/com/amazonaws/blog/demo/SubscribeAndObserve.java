package com.amazonaws.blog.demo;

import com.amazonaws.blog.demo.grpc.DemoStreamingServiceOuterClass;
import com.google.rpc.Status;
import io.lettuce.core.RedisClient;
import io.lettuce.core.pubsub.RedisPubSubAdapter;
import io.lettuce.core.pubsub.RedisPubSubListener;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import io.lettuce.core.pubsub.api.sync.RedisPubSubCommands;
import org.apache.log4j.Logger;

public class SubscribeAndObserve {
    private Thread demon = null;
    private boolean running = true;
    private boolean isConnectionTimedOut = false;
    private final static Logger logger = Logger.getLogger(SubscribeAndObserve.class);

    public SubscribeAndObserve() {
    }
    public void start() {
        try {
            RedisClient redisClient = RedisClient.create(System.getenv("REDIS_ENDPOINT"));
            StatefulRedisPubSubConnection<String, String> connection = redisClient.connectPubSub();
            if (demon != null) return;
            running = true;

            demon = new Thread(() -> {
                try {
                    RedisPubSubListener<String, String> listener = new RedisPubSubAdapter<String, String>() {

                        @Override
                        public void message(String channel, String message) {
                            System.out.println(String.format("Channel: %s, Message: %s", channel, message));

                            ConnectionsQueue
                                    .INSTANCE
                                    .getActiveConnections()
                                    .listIterator()
                                    .forEachRemaining(x -> x.onNext(DemoStreamingServiceOuterClass
                                            .DemoStreamingServiceResponse
                                            .newBuilder()
                                            .setStatus(Status.newBuilder().setMessage("Streaming").build())
                                            .setData(String.valueOf(message))
                                            .build()));
                        }
                    };
                    connection.addListener(listener);
                    RedisPubSubCommands<String, String> sync = connection.sync();
                    sync.subscribe("channel");

                } catch (Exception e) {
                    running = false;
                    connection.close();
                    redisClient.shutdown();
                }
            });
            demon.setDaemon(true);
            demon.start();
        }catch( SecurityException | NullPointerException e ){
            e.printStackTrace();
        }
    }

    public void stop() {
        if (demon == null || !running) return;
        running = false;
        demon.interrupt();
        demon = null;
    }
}

