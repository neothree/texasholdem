package com.texasthree.room.round;

import com.texasthree.room.Cmd;
import com.texasthree.room.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TexasRoundTest {
    @Test
    void start() throws Exception {
        User[] users = new User[3];
        for (int i = 0; i < 3; i++) {
            Cmd.UserData data = new Cmd.UserData();
            data.id = i + "";
            data.name = data.id;
            data.chips = 1000;
            User u = new User(data);
            users[i] = u;
        }
        TexasRound game = new TexasRound(users, null);
        game.start();
    }
}