package com.texasthree.round;

import com.texasthree.proto.CommandGrpc;
import com.texasthree.proto.CommandReply;
import com.texasthree.proto.CommandRequest;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GrpcClient {
    private static final Logger logger = Logger.getLogger(GrpcClient.class.getName());
    private ManagedChannel channel;
    private CommandGrpc.CommandBlockingStub blockingStub;

    public  GrpcClient(String host, int port) {
        channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build();

        blockingStub = CommandGrpc.newBlockingStub(channel);
    }


    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

    public void send(String name, String data) {
        CommandRequest request = CommandRequest.newBuilder().setName(name).setData(data).build();

        CommandReply response;
        try {
            response = blockingStub.sendCommand(request);
        } catch (StatusRuntimeException e) {
            logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
            return;
        }
        logger.info("Message from gRPC-Server: " + response.getData());
    }
}
