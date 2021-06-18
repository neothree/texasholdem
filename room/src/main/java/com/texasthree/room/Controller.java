package com.texasthree.room;

import io.vertx.core.Vertx;

/**
 * @author: neo
 * @create: 2021-06-18 10:27
 */
public class Controller {
    private void start() {
        var vertx = Vertx.vertx();
        var server = vertx.createHttpServer();
    }
}
