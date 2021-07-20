package com.texasthree.room;

import com.texasthree.core.Server;
import org.apache.log4j.BasicConfigurator;


public class Application {

    public static void main(String[] args) throws Exception {
        BasicConfigurator.configure();

        var server = new Server();
        server.start("com.texasthree.room.controller", uid -> User.getUser(uid));

        // TODO 移除
        User.server = server;
    }
}
