package com.texasthree.zone.net;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.texasthree.zone.net.Message;
import com.texasthree.zone.net.MessageDispatcher;
import io.vertx.core.Vertx;
import io.vertx.core.http.ServerWebSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * @author: neo
 * @create: 2021-07-09 15:13
 */
public class Server {

    private static Logger log = LoggerFactory.getLogger(Server.class);

    private Vertx vertx;

    private int port = 8090;

    private MessageDispatcher dispatcher;

    private Map<String, ServerWebSocket> wsCache = new HashMap<>();

    public void start(String path, Function<String, Object> find) {
        log.info("开始启动服务器");
        this.vertx = Vertx.vertx();

        this.vertx.createHttpServer()
                .webSocketHandler(ws -> this.webSocketHandler(ws))
                .listen(port);

        this.dispatcher = new MessageDispatcher();
        this.dispatcher.register(path, find);

        log.info("服务器启动成功");
    }

    private void webSocketHandler(ServerWebSocket ws) {
        final var chatUrlPattern = Pattern.compile("/zone/(\\w+)");
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

        this.wsCache.put(uid, ws);
    }

    public void send(String uid, Object obj) {
        var ws = this.wsCache.get(uid);
        if (ws == null) {
            throw new IllegalArgumentException();
        }
        try {
            var str = new ObjectMapper().writeValueAsString(obj);
            var message = new Message(null, obj.getClass().getSimpleName(), str);
            ws.writeTextMessage(new ObjectMapper().writeValueAsString(message));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void send(Set<String> uids, Object obj) {
        try {
            var str = new ObjectMapper().writeValueAsString(obj);
            var message = new Message(null, obj.getClass().getSimpleName(), str);
            var data = new ObjectMapper().writeValueAsString(message);
            for (var uid : uids) {
                var ws = this.wsCache.get(uid);
                if (ws != null) {
                    ws.writeTextMessage(data);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
