package com.amazonaws.blog.demo;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.KinesisEvent;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisException;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import io.lettuce.core.pubsub.api.sync.RedisPubSubCommands;

import java.nio.charset.StandardCharsets;

/**
 * Lambda Function for consuming Kinesis Data and Publishing to the Redis Server
 *
 */
public class KinesisConsumerLambda implements RequestHandler<KinesisEvent, String> {
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
// com.amazonaws.blog.demo.KinesisConsumerLambda::handleRequest
    @Override
    public String handleRequest(KinesisEvent kinesisEvent, Context context) {
        LambdaLogger logger = context.getLogger();
        // process event
       // logger.log("EVENT: " + gson.toJson(kinesisEvent));

        try{
            System.out.println(kinesisEvent.toString());

            RedisClient redisClient = RedisClient.create(System.getenv("REDIS_ENDPOINT"));
            StatefulRedisPubSubConnection<String, String> connection = redisClient.connectPubSub();

            RedisPubSubCommands<String, String> sync = connection.sync();
            //sync.subscribe("channel");

            //todo replace the name channel and add something meaningful
            kinesisEvent
                    .getRecords()
                    .listIterator()
                    .forEachRemaining(
                            x -> sync.publish("channel", StandardCharsets.UTF_8.decode(x.getKinesis().getData()).toString()));

            connection.close();
            redisClient.shutdown();

        }catch ( RedisException e){
            e.printStackTrace();
        }
        return null;
    }
}
