package com.texasthree.zone.room;

import com.texasthree.game.texas.Circle;
import com.texasthree.game.texas.Optype;
import com.texasthree.utility.utlis.StringUtils;
import com.texasthree.zone.Tester;
import com.texasthree.zone.user.UserData;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class TexasRoundTest {

    @Test
    void testRound() throws Exception {
        var users = new ArrayList<UserPlayer>();
        for (int i = 0; i < 3; i++) {
            var user = Tester.createUser();
            users.add(new UserPlayer(i, user, 100));
        }

        var id = 12;
        var round = new TexasRound(id, "12311", users, new RoundEventHandler(() -> {
        }, (v) -> {
        }, (v, s) -> {
        }));
        assertEquals(id, round.getId());

        // PREFLOP
        var dealer = users.get(0).seatId;
        round.start(dealer);
        assertEquals(3, round.getPlayers().size());
        assertEquals(dealer, round.dealer());
        assertNull(round.getOperator());
        assertEquals(Circle.PREFLOP, round.circle());
        assertTrue(round.leftSec() > 0);

        round.force();
        assertEquals(0, round.getOperator().seatId);
        round.action(Optype.Call, 0);

        round.force();
        assertEquals(1, round.getOperator().seatId);
        round.action(Optype.Call, 0);

        round.force();
        assertEquals(2, round.getOperator().seatId);
        round.action(Optype.Check, 0);

        // FLOP
        assertEquals(Circle.FLOP, round.circle());
        assertNull(round.getOperator());

        round.force();
        assertEquals(1, round.getOperator().seatId);
        round.action(Optype.Check, 0);

        assertEquals(2, round.getOperator().seatId);
        round.action(Optype.Check, 0);

        assertEquals(0, round.getOperator().seatId);
        round.action(Optype.Check, 0);

        // TURN
        assertEquals(Circle.TURN, round.circle());
        assertNull(round.getOperator());

        round.force();
        assertEquals(1, round.getOperator().seatId);
        round.action(Optype.Check, 0);

        assertEquals(2, round.getOperator().seatId);
        round.action(Optype.Check, 0);

        assertEquals(0, round.getOperator().seatId);
        round.action(Optype.Check, 0);

        // RIVER
        assertEquals(Circle.RIVER, round.circle());
        assertNull(round.getOperator());

        round.force();
        assertEquals(1, round.getOperator().seatId);
        round.action(Optype.Check, 0);

        assertEquals(2, round.getOperator().seatId);
        round.action(Optype.Check, 0);

        assertEquals(0, round.getOperator().seatId);
        round.action(Optype.Check, 0);

        // SHOWDOWN
        assertTrue(round.finished());
    }
}