package com.texasthree.round;

import com.texasthree.proto.CommandGrpc;

import com.texasthree.proto.CommandReply;
import com.texasthree.proto.CommandRequest;
import io.grpc.stub.StreamObserver;
import org.lognet.springboot.grpc.GRpcService;

import java.util.logging.Logger;

public class GrpcServer {
    private static final Logger logger = Logger.getLogger(GrpcServer.class.getName());


    @GRpcService
    static class CommandImpl extends CommandGrpc.CommandImplBase {

        @Override
        public void sendCommand(CommandRequest req, StreamObserver<CommandReply> responseObserver) {
            switch (req.getName()) {
                case "create":
                    Protocol.create(req.getData());
                    break;
                case "destroy":
                    Protocol.destroy(req.getData());
                    break;
                case "getData":
                    Protocol.getData(req.getData());
                    break;
                case "action":
                    Protocol.action(req.getData());
                    break;
                case "settle":
                    Protocol.settle(req.getData());
                    break;
            }

            logger.info("rpc收到消息:" + req.getName());
            CommandReply reply = CommandReply.newBuilder().setRetcode(0).build();
            responseObserver.onNext(reply);
            responseObserver.onCompleted();
        }
    }
}
