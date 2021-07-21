package com.texasthree.gateway;

import com.texasthree.core.Server;
import com.texasthree.core.net.Message;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: neo
 * @create: 2021-07-20 17:05
 */
@Slf4j
public class Gateway {

    public String id;

    public String name;

    private Map<String, Zone> zoneMap = new HashMap<>();

    private Server server;


    public void start(String path) {
        log.info("网关开始启动");

        this.server = new Server();
        server.start(path, uid -> null);

        log.info("启动完成");
    }

    /**
     * 区服连接
     */
    public void connect(String id, String name) {
        var zone = new Zone();
        zone.id = id;
        zone.name = name;
        log.info("区服连接 {}", zone);
        this.zoneMap.put(zone.getId(), zone);
    }

    public void forward(String zoneId, Message message) {
        this.server.send(zoneId, message);
    }

    @Override
    public String toString() {
        return "[" + id + ":" + name + "]";
    }
}
