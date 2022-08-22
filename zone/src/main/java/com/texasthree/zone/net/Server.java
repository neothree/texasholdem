package com.texasthree.zone.net;

import com.texasthree.security.shiro.LoginerRealm;
import com.texasthree.utility.packet.Packet;
import com.texasthree.zone.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Set;

/**
 * @author: neo
 * @create: 2021-07-09 15:13
 */
@EnableAsync
@Service
@RestController
@RequestMapping
public class Server {

    private static Logger log = LoggerFactory.getLogger(Server.class);

    private static final String PING_DESTINATION = "/ping";

    private static final String USER_DESTINATION = "/private";

    private final SimpMessagingTemplate messagingTemplate;

    private final PacketDispatcher dispatcher;

    @Autowired
    public Server(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
        this.dispatcher = new PacketDispatcher(User::getUserById);
    }

    public void start() {
        dispatcher.register("com.texasthree.zone.controller");
    }

    @EventListener
    public void onWebSocketDisconnect(SessionDisconnectEvent event) {
//        var headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
//        String username = (String) headerAccessor.getSessionAttributes().get("username");
        log.info("Webscoket seesion断开连接 {}", event.getSessionId());
    }

    /**
     * 接收到玩家的 websocket packet 消息
     */
    @MessageMapping("/user")
    public void recv(SimpMessageHeaderAccessor headerAccessor,
                     @Payload Packet packet) throws Exception {
        var user = (User) headerAccessor.getSessionAttributes().get(LoginerRealm.ME_KEY);
        this.dispatcher.dispatch(packet, user);
    }

    public void send(Set<String> uids, Object obj) {
        var msg = Packet.convertAsString(obj);
        log.info(msg);
        for (var uid : uids) {
            this.send(uid, msg);
        }
    }

    public void send(String uid, Object obj) {
        this.send(uid, Packet.convertAsString(obj));
    }

    /**
     * 发送消息给玩家
     */
    public void send(String uid, String msg) {
        log.info("{} {}", uid, msg);
        var destination = USER_DESTINATION + "/" + uid;
        this.messagingTemplate.convertAndSend(destination, msg);
    }

    /**
     * 给所有的ws链接定时发送心跳消息
     */
    @Async
    @Scheduled(fixedRate = 1000)
    public void ping() {
        this.messagingTemplate.convertAndSend(PING_DESTINATION, "PING");
    }
}
