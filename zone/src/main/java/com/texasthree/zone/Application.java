package com.texasthree.zone;

import com.texasthree.core.net.Message;
import org.apache.log4j.BasicConfigurator;


public class Application {

    public static void main(String[] args) throws Exception {
        BasicConfigurator.configure();

        var client = new GatewayClient();
        client.connect(8090, "localhost", "/zone/123");

        var message = new Message("hello", "Heartbeat", "");

        Thread.sleep(200);
        client.send(message);
    }
}
