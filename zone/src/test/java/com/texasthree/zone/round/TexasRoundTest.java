package com.texasthree.zone.round;

import com.texasthree.zone.entity.User;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

class TexasRoundTest {
    
    @Test
    void start() throws Exception {
        var users = new ArrayList<UserPlayer>();
        for (int i = 0; i < 3; i++) {
            var user = new User();
            user.setChips(100);
            users.add(new UserPlayer(1, user));
        }
        var game = new TexasRound(users, v -> {});
        game.start();
    }

}