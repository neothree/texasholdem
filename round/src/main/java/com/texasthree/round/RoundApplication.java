package com.texasthree.round;

import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
public class RoundApplication {
    public static void main(String[] args) throws Exception {
        final GrpcServer server = new GrpcServer();
        server.start();
        server.blockUntilShutdown();
    }
}
