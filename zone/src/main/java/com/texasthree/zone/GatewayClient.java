package com.texasthree.zone;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.texasthree.core.net.Message;
import com.texasthree.core.net.MessageDispatcher;
import com.texasthree.zone.entity.User;
import io.vertx.core.Vertx;
import io.vertx.core.http.WebSocket;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: neo
 * @create: 2021-07-20 17:48
 */
@Slf4j
public class GatewayClient {

    private WebSocket ws;

    private MessageDispatcher dispatcher;

    private int port;

    private String host;

    private String uri;

    private Vertx vertx;

    public void connect(int port, String host, String uri) {
        this.port = port;
        this.host = host;
        this.uri = uri;

        this.dispatcher = new MessageDispatcher();
        this.dispatcher.register("com.texasthree.zone.controller", uid -> User.getUser(uid));

        this.vertx = Vertx.vertx();
        this.startClient();
    }

    private void startClient() {
        var client = vertx.createHttpClient();
        client.webSocket(port, host, uri, ar -> {
            if (ar.succeeded()) {
                log.info("ws 连接成功");
                this.ws = ar.result();
                ws.textMessageHandler((msg) -> {
                    log.info("收到消息 {}", msg);
                }).exceptionHandler((e) -> {
                    client.close();
                    vertx.setTimer(10 * 1000, i -> {
                        startClient();
                    });
                });
            } else {
                log.error(ar.cause().getMessage());
            }
        });
    }

    public void send(Message message) {
        if (this.ws == null) {
            log.error("未连接到 gateway");
            return;
        }

        try {
            this.ws.writeTextMessage(new ObjectMapper().writeValueAsString(message));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
