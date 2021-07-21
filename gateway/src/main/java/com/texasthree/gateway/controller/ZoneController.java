package com.texasthree.gateway.controller;

import com.texasthree.core.net.Command;
import com.texasthree.core.net.Controller;
import com.texasthree.core.proto.Proto;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: neo
 * @create: 2021-07-20 17:02
 */
@Controller
@Slf4j
public class ZoneController {

    @Command
    public void connect(Proto.Connect data, Object no) {

        log.info("收到消息 connect");
    }

    @Command
    public void heartbeat(Proto.Heartbeat data, Object no) {
        log.info("收到消息 hearbeat");
    }

}
