package com.texasthree.zone.round;

import com.texasthree.zone.entity.User;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

class TexasRoundTest {
    
    @Test
    void start() throws Exception {
        var users = new ArrayList<UserPayer>();
        for (int i = 0; i < 3; i++) {
            var up = new UserPayer();
            up.seatId = i;
            up.user = new User();
            up.user.setChips(100);
            users.add(up);
        }
        var game = new TexasRound(users, v -> {});
        game.start();
    }

}