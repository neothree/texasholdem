package com.texasthree;

import com.texasthree.core.server.ServerManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;

@SpringBootApplication
public class RoomApplication {
    private static final Logger LOG = LoggerFactory.getLogger(RoomApplication.class);

    @Autowired
    private ServerManager serverManager;

    public static void main(String[] args) throws Exception {
        SpringApplication.run(RoomApplication.class, args);
    }

    @PostConstruct
    private void start() {
        try {
            serverManager.startServers();
            LOG.info("Room 启动成功");
        } catch (Exception e) {
            e.printStackTrace();
            LOG.error("Unable to start servers cleanly: {}", e.getMessage());
            System.exit(1);
        }
    }
}
