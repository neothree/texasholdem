package com.texasthree.zone.round;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

class TexasRoundTest {
    
    @Test
    void start() throws Exception {
        var users = new ArrayList<Integer>();
        for (int i = 0; i < 3; i++) {
            users.add(100);
        }
        var game = new TexasRound(users, v -> {});
        game.start();
    }

}