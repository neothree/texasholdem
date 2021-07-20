package com.texasthree.room.net;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ServerTest {

    @Test
    void start() {
        var server = new Server();
        server.start();
    }
}