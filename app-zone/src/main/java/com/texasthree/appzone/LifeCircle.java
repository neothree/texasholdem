package com.texasthree.appzone;

import com.texasthree.appzone.net.Server;
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

    private static Logger log = LoggerFactory.getLogger(LifeCircle.class);

    private Server server;

    private Zone zone;

    @Autowired
    public LifeCircle(Server server,
                      Zone zone) {
        this.server = server;
        this.zone = zone;
    }


    public void start() {
        server.start();
        zone.start();
    }

    public void exit() {
        log.info("zone关闭");
    }
}
