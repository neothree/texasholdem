package com.texasthree;

import com.texasthree.room.User;
import com.texasthree.room.net.Server;
import org.apache.log4j.BasicConfigurator;


public class Application {

    public static void main(String[] args) throws Exception {
        BasicConfigurator.configure();

        var server = new Server();
        server.start();

        // TODO 移除
        User.server = server;
    }
}
