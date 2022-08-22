package com.texasthree.zone.round;

import com.texasthree.zone.room.Desk;
import com.texasthree.zone.user.User;
import com.texasthree.zone.user.UserData;
import com.texasthree.zone.utility.StringUtils;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TexasRoundTest {

    @Test
    void testStart() throws Exception {
        var users = new ArrayList<UserPlayer>();
        for (int i = 0; i < 3; i++) {
            var data = new UserData(StringUtils.get10UUID(), StringUtils.get10UUID());
            data.setId(i + "");
            var user = new User(data);
            user.setChips(100);
            users.add(new UserPlayer(i, user));
        }
        var desk = new Desk();
        var round = new TexasRound(users, new TexasEventHandler(desk));

        var dealer = users.get(0).seatId;
        round.start(dealer);
        assertEquals(3, round.getPlayers().stream().count());
        assertEquals(dealer, round.dealer());
    }
}