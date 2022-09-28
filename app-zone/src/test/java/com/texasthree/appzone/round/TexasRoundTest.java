package com.texasthree.appzone.round;

import com.texasthree.appzone.Tester;
import com.texasthree.game.texas.Circle;
import com.texasthree.game.texas.Optype;
import com.texasthree.utility.utlis.StringUtils;
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
        round.action(Optype.Raise, 2);

        round.force();
        assertEquals(2, round.getOperator().seatId);
        round.action(Optype.Call, 0);

        round.force();
        assertEquals(0, round.getOperator().seatId);
        round.action(Optype.Call, 0);

        // TURN
        assertEquals(Circle.TURN, round.circle());
        assertNull(round.getOperator());

        round.force();
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

        var insurance = round.getInsurance();
        assertNotNull(insurance);
        assertFalse(insurance.finished());

        insurance.force();
        assertFalse(insurance.finished());
        insurance.force();
        assertFalse(insurance.finished());
        insurance.force();
        assertTrue(insurance.finished());


        // 结算
        round = new TexasRound(id, StringUtils.get10UUID(), users, handler());
        dealer = users.get(0).seatId;
        round.start(dealer);
        round.force();
        round.action(Optype.Allin, 0);
        round.force();
        round.action(Optype.Allin, 0);
        round.force();
        insurance = round.getInsurance();
        assertNotNull(insurance);

        var winner = insurance.getPot(Circle.TURN, 0);
        insurance.buy(winner.applicant, 0, 10, null);
        insurance.force();
        winner = insurance.getPot(Circle.RIVER, 0);
        insurance.buy(winner.applicant, 0, 10, null);
        insurance.force();
        assertTrue(insurance.finished());

        var settle = round.settle();
        for (var v : settle) {
            assertNotEquals(v.profit, 0);
            if (v.id == winner.applicant) {
                assertNotEquals(v.insurance, 0);
            }
        }
    }

    private List<UserPlayer> createUsers(int num) {
        var users = new ArrayList<UserPlayer>();
        for (int i = 0; i < num; i++) {
            var user = Tester.createUser();
            users.add(new UserPlayer(i, user, 100));
        }
        return users;
    }

    private TexasEventHandler handler() {
        return new TexasEventHandler(() -> {
        }, (v) -> {
        }, (v, s) -> {
        });
    }
}