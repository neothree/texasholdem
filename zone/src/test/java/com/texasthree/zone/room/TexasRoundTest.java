package com.texasthree.zone.room;

import com.texasthree.game.texas.Action;
import com.texasthree.game.texas.Circle;
import com.texasthree.game.texas.Optype;
import com.texasthree.utility.utlis.StringUtils;
import com.texasthree.zone.user.User;
import com.texasthree.zone.user.UserData;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

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
        var desk = new Desk(9);
        var round = new TexasRound("12311", users, new TexasEventHandler(desk));


        // PREFLOP
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

        // FLOP
        assertEquals(Circle.FLOP, round.circle());
        assertNull(round.getOperator());

        round.force();
        assertEquals(1, round.getOperator().seatId);
        round.action(Action.of(Optype.Check));

        assertEquals(2, round.getOperator().seatId);
        round.action(Action.of(Optype.Check));

        assertEquals(0, round.getOperator().seatId);
        round.action(Action.of(Optype.Check));

        // TURN
        assertEquals(Circle.TURN, round.circle());
        assertNull(round.getOperator());

        round.force();
        assertEquals(1, round.getOperator().seatId);
        round.action(Action.of(Optype.Check));

        assertEquals(2, round.getOperator().seatId);
        round.action(Action.of(Optype.Check));

        assertEquals(0, round.getOperator().seatId);
        round.action(Action.of(Optype.Check));

        // RIVER
        assertEquals(Circle.RIVER, round.circle());
        assertNull(round.getOperator());

        round.force();
        assertEquals(1, round.getOperator().seatId);
        round.action(Action.of(Optype.Check));

        assertEquals(2, round.getOperator().seatId);
        round.action(Action.of(Optype.Check));

        assertEquals(0, round.getOperator().seatId);
        round.action(Action.of(Optype.Check));

        // SHOWDOWN
        assertTrue(round.finished());
    }
}