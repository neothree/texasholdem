package com.texasthree.room.net;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.ServerWebSocket;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.regex.Pattern;

/**
 * @author: neo
 * @create: 2021-07-09 15:13
 */
@Slf4j
public class Server {

    private int port = 8090;

    private MessageDispatcher dispatcher;

    public void start() {
        log.info("开始启动服务器");
        this.startWebsocket();

        this.dispatcher = new MessageDispatcher();
        this.dispatcher.init();

        log.info("服务器启动成功");
    }


    private void startWebsocket() {
        var vertx = Vertx.vertx();

        final var chatUrlPattern = Pattern.compile("/chat/(\\w+)");
        final var eventBus = vertx.eventBus();

        vertx.createHttpServer().webSocketHandler(new Handler<ServerWebSocket>() {
            @Override
            public void handle(final ServerWebSocket ws) {
                final var m = chatUrlPattern.matcher(ws.path());
                if (!m.matches()) {
                    ws.reject();
                    return;
                }

                final var chatRoom = m.group(1);
                final var uid = ws.textHandlerID();
                log.info("registering new connection with id: " + uid + " for chat-room: " + chatRoom);
                vertx.sharedData().getLocalMap("chat.room." + chatRoom).put(uid, uid);

                ws.closeHandler(new Handler<Void>() {
                    @Override
                    public void handle(final Void event) {
                        log.info("un-registering connection with id: " + uid + " from chat-room: " + chatRoom);
                        vertx.sharedData().getLocalMap("chat.room." + chatRoom).remove(uid);
                    }
                });

                ws.handler(new Handler<Buffer>() {
                    @Override
                    public void handle(final Buffer buffer) {
                        var m = new ObjectMapper();
                        try {
                            var receive = buffer.toString();
                            log.info("收到消息 {}", receive);
                            var rootNode = m.readTree(receive);
                            var message = new Message(uid, rootNode.get("name").asText(), rootNode.get("data").asText());
                            dispatcher.dispatch(message);
                        } catch (IOException e) {
                            ws.reject();
                        }
                    }
                });
            }
        }).listen(port);
        log.info("websocket server 启动");
    }
}
