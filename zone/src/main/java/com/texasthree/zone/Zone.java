package com.texasthree.zone;

import com.texasthree.zone.entity.Room;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * @author: neo
 * @create: 2022-08-11 23:28
 */
@EnableAsync
@Service
public class Zone {
    private static Logger log = LoggerFactory.getLogger(Zone.class);

    public void start() {
        log.info("zone 开启启动");
    }

    @Async
    @Scheduled(fixedRate = 100)
    public void loop() {
        for (var v : Room.all()) {
            v.loop();
        }
    }
}
