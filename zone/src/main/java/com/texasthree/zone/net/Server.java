package com.texasthree.zone.net;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.texasthree.zone.net.Message;
import com.texasthree.zone.net.MessageDispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.function.Function;

/**
 * @author: neo
 * @create: 2021-07-09 15:13
 */
@EnableAsync
@Service
public class Server {

    private static Logger log = LoggerFactory.getLogger(Server.class);

    private static final String PING_DESTINATION = "/ping";

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    private MessageDispatcher dispatcher;


    public void start() {
        log.info("开始启动服务器");

//        this.dispatcher = new MessageDispatcher();
//        this.dispatcher.register(path, find);

        log.info("服务器启动成功");
    }

    public void send(String uid, Object obj) {

    }

    public void send(Set<String> uids, Object obj) {

    }

    @Async
    @Scheduled(fixedRate = 1000)
    public void ping() {
        this.messagingTemplate.convertAndSend(PING_DESTINATION, "PING");
    }
}
