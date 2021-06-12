package com.texasthree.room.game;

import com.texasthree.room.Cmd;
import com.texasthree.room.User;
import org.junit.jupiter.api.Test;

class TexasGameTest {

    @Test
    void start() throws Exception {
        User[] users = new User[3];
        for (int i = 0; i < 3; i++) {
            Cmd.UserData data = new Cmd.UserData();
            data.id = i+"";
            data.name = data.id;
            data.chips = 1000;
            User u = new User(data, null);
            users[i] = u;
        }
        TexasGame game = new TexasGame(users, null);
        game.start();
    }
}