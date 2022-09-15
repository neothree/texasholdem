package com.texasthree.zone.room;

import com.texasthree.game.texas.Circle;
import com.texasthree.game.texas.Optype;
import com.texasthree.zone.Tester;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TexasRoundTest {

    @Test
    void testRound() throws Exception {
        var users = createUsers(3);
        var id = 12;
        var round = new TexasRound(id, "12311", users, handler());
        assertEquals(id, round.getId());

        // PREFLOP
        var dealer = users.get(0).seatId;
        round.start(dealer);
        assertEquals(3, round.getPlayers().size());
        assertEquals(dealer, round.dealer());
        assertNull(round.getOperator());
        assertEquals(Circle.PREFLOP, round.circle());
        assertTrue(round.leftSec() > 0);

        // 获取过押注的权限
        assertFalse(round.getPlayerBySeatId(0).isGain());
        round.force();
        assertTrue(round.getPlayerBySeatId(0).isGain());

        var op = round.getOperator();
        assertEquals(0, op.seatId);
        // 只主动执行押注
        assertFalse(op.isExecute());
        round.action(Optype.Call, 0);
        assertTrue(op.isExecute());

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

    @Test
    void testInsurance() throws Exception {
        var users = createUsers(2);
        var id = 12;
        var round = new TexasRound(id, "12311", users, handler());

        var dealer = users.get(0).seatId;
        round.start(dealer);
        round.force();
        round.action(Optype.Allin, 0);
        round.force();
        round.action(Optype.Allin, 0);
        round.force();
        assertNotNull(round.getInsurance());
    }

    private List<UserPlayer> createUsers(int num) {
        var users = new ArrayList<UserPlayer>();
        for (int i = 0; i < num; i++) {
            var user = Tester.createUser();
            users.add(new UserPlayer(i, user, 100));
        }
        return users;
    }

    private RoundEventHandler handler() {
        return new RoundEventHandler(() -> {
        }, (v) -> {
        }, (v, s) -> {
        });
    }
}