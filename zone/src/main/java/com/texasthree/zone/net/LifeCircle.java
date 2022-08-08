package com.texasthree.zone.net;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author: neo
 * @create: 2022-01-07 14:58
 */
@Service
public class LifeCircle {

    private Server server;

    @Autowired
    public LifeCircle(Server server) {
        this.server = server;
    }

    private static Logger log = LoggerFactory.getLogger(LifeCircle.class);

    public void start() {
        log.info("zone启动");
        server.start();
    }

    public void exit() {
        log.info("zone关闭");
    }
}
