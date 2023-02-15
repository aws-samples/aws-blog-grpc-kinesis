package com.amazonaws.blog.demo;

import io.grpc.ConnectivityState;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

/**
 * Hello world!
 *
 */
public class GrpcClient
{
    public static void main(String[] args){
        final String serverURL = args[0];
        final int port = Integer.parseInt(args[1]);
        final String apiAction = args[2];
        final String clientId = args[3];
        final String clientSecret = args[4];
        final String tokenUrl = args[5];
        ConfigLoader.setConfigData(clientId,clientSecret,tokenUrl);
        new GrpcClient().nonStaticMain(serverURL, port, apiAction );
    }

    public void nonStaticMain(String serverURL, int port, String apiAction) {
        System.out.println(String.format("Establishing connection", serverURL,port, apiAction));
        ManagedChannelBuilder<?> channelBuilder = ManagedChannelBuilder
                .forAddress(serverURL, port)
                .keepAliveTime(23, TimeUnit.HOURS)
                .enableRetry()
                .usePlaintext()
                .keepAliveWithoutCalls(true);
        ManagedChannel channel = channelBuilder.build();
        ConnectivityState connectivityState = channel.getState(false);
        System.out.println("Channel Connection Status: " + connectivityState);

        DemoStreamingServiceGrpc.DemoStreamingServiceBlockingStub streamingStub = DemoStreamingServiceGrpc.newBlockingStub(channel);
        try{
            if (apiAction.equalsIgnoreCase("stop-stream")) {
                System.out.println("stopping stream..");
                final DemoStreamingServiceOuterClass.StopStreamRequest stopRequest = DemoStreamingServiceOuterClass.StopStreamRequest.newBuilder().build();
                DemoStreamingServiceOuterClass.DemoStreamingServiceResponse stopResponse = DemoStreamingServiceGrpc
                        .newBlockingStub(channel)
                        .stopStream(stopRequest);
                System.out.println(stopResponse.getStatus() + "\n" + stopResponse.getData());

            } else if (apiAction.equalsIgnoreCase("start-stream")) {
                System.out.println("trying to establish connection...");
                final DemoStreamingServiceOuterClass.StartStreamRequest startRequest = DemoStreamingServiceOuterClass.StartStreamRequest.newBuilder().build();
                Iterator<DemoStreamingServiceOuterClass.DemoStreamingServiceResponse> dataRecords = streamingStub
                        .withWaitForReady()
                        .withDeadlineAfter(2, TimeUnit.HOURS)
                        .withCallCredentials(getCallCredential())
                        .startStream(startRequest);
                dataRecords.forEachRemaining(this::forEachRecord);}
            else {
                System.out.println("Please provide correct API operation to begin");
                System.out.println("shutting down channel");
                channel.shutdown();
                System.exit(1);
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("ArrayIndexOutOfBoundsException");
        } catch (StatusRuntimeException e) {
            System.out.println("Wrong Auth Key");
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void forEachRecord(DemoStreamingServiceOuterClass.DemoStreamingServiceResponse dataRecord) {

        System.out.println(String.format("status=%s data=%s", dataRecord.getStatus(), dataRecord.getData().trim()));
    }


    private JwtCallCredential getCallCredential() throws IOException {
        String token = CognitoToken.getNewCognitoToken();
        System.out.println(token);
        return new JwtCallCredential(token);
    }
}
