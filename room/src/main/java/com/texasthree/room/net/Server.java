package com.texasthree.room.net;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * @author: neo
 * @create: 2021-07-09 15:13
 */
@Slf4j
public class Server {

    private Vertx vertx;

    private EventBus eventBus;

    private int port = 8090;

    private MessageDispatcher dispatcher;

    public void start() {
        log.info("开始启动服务器");
        this.vertx = Vertx.vertx();
        this.eventBus = vertx.eventBus();

        this.startWebsocket();

        this.dispatcher = new MessageDispatcher();
        this.dispatcher.init();

        log.info("服务器启动成功");
    }


    private void startWebsocket() {

        final var chatUrlPattern = Pattern.compile("/chat/(\\w+)");

        vertx.createHttpServer().webSocketHandler(ws -> {
            final var m = chatUrlPattern.matcher(ws.path());
            if (!m.matches()) {
                ws.reject();
                return;
            }

            final var chatRoom = m.group(1);
            final var uid = ws.textHandlerID();
            log.info("registering new connection with id: " + uid + " for chat-room: " + chatRoom);
            vertx.sharedData().getLocalMap("chat.room." + chatRoom).put(uid, uid);

            ws.closeHandler(event -> {
                log.info("un-registering connection with id: " + uid + " from chat-room: " + chatRoom);
                vertx.sharedData().getLocalMap("chat.room." + chatRoom).remove(uid);

            });

            ws.handler(buffer -> {
                try {
                    var receive = buffer.toString();
                    log.info("收到消息 {}", receive);
                    var rootNode = new ObjectMapper().readTree(receive);
                    var message = new Message(uid, rootNode.get("name").asText(), rootNode.get("data").asText());
                    dispatcher.dispatch(message);
                } catch (IOException e) {
                    ws.reject();
                }
            });
        }).listen(port);
        log.info("websocket server 启动");
    }

    public void send(Object obj, String uid) {
        try {
            var str = new ObjectMapper().writeValueAsString(obj);
            var message = new Message(null, obj.getClass().getSimpleName(), str);
            eventBus.send(uid, message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void send(Object obj, Set<String> uids) {
        try {
            var str = new ObjectMapper().writeValueAsString(obj);
            var message = new Message(null, obj.getClass().getSimpleName(), str);
            for (var uid : uids) {
                eventBus.send(uid, message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
