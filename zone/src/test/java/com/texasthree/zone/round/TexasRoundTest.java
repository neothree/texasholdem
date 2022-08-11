package com.texasthree.zone.round;

import com.texasthree.zone.controller.Cmd;
import com.texasthree.zone.entity.User;
import org.junit.jupiter.api.Test;

class TexasRoundTest {
    
    @Test
    void start() throws Exception {
        User[] users = new User[3];
        for (int i = 0; i < 3; i++) {
            Cmd.UserData data = new Cmd.UserData();
            data.id = i + "";
            data.name = data.id;
            data.chips = 1000;
            User u = new User();
            users[i] = u;
        }
        TexasRound game = new TexasRound(users, null);
        game.start();
    }

}