package com.texasthree.zone.round;

import com.texasthree.game.texas.Action;
import com.texasthree.game.texas.Circle;
import com.texasthree.game.texas.Optype;
import com.texasthree.zone.room.Desk;
import com.texasthree.zone.user.User;
import com.texasthree.zone.user.UserData;
import com.texasthree.zone.utility.StringUtils;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class TexasRoundTest {

    @Test
    void testRound() throws Exception {
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
        assertEquals(3, round.getPlayers().size());
        assertEquals(dealer, round.dealer());
        assertNull(round.getOperator());
        assertEquals(Circle.PREFLOP, round.circle());

        round.force();
        assertEquals(0, round.getOperator().seatId);
        round.action(Action.of(Optype.Call));

        round.force();
        assertEquals(1, round.getOperator().seatId);
        round.action(Action.of(Optype.Call));

        round.force();
        assertEquals(2, round.getOperator().seatId);
        round.action(Action.of(Optype.Check));

        assertEquals(Circle.FLOP, round.circle());
    }
}