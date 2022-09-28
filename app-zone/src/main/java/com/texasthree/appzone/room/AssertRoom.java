package com.texasthree.appzone.room;

import com.texasthree.game.texas.Optype;
import com.texasthree.utility.utlis.StringUtils;
import com.texasthree.appzone.User;
import org.junit.jupiter.api.Assertions;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author: neo
 * @create: 2022-09-05 11:21
 */
public class AssertRoom extends Room {

    public static AssertRoom build() {
        return new AssertRoom(StringUtils.get10UUID(), 9);
    }

    public static AssertRoom build(int capacity) {
        return new AssertRoom(StringUtils.get10UUID(), capacity);
    }

    AssertRoom(String id, int capacity) {
        super(id, capacity, null);
    }

    AssertRoom toAddUser(User user) {
        super.addUser(user);
        return this;
    }

    AssertRoom toBuyin(User user) {
        super.buyin(user, Room.initChips);
        return this;
    }

    AssertRoom toSettle(String uid) {
        super.settle(uid);
        return this;
    }

    AssertRoom toSitDown(User user, int seatId) {
        super.sitDown(user, seatId);
        return this;
    }

    AssertRoom toSitUp(User user) {
        super.sitUp(user);
        return this;
    }

    AssertRoom toPending(User user) {
        super.pending(user);
        return this;
    }

    AssertRoom toPendingCancel(User user) {
        super.pendingCancel(user);
        return this;
    }


    AssertRoom toForce() {
        super.force();
        return this;
    }

    AssertRoom toRoundForce() {
        super.getRound().force();
        return this;
    }

    AssertRoom toRoundForce(int num) {
        for (var i = 0; i < num; i++) {
            super.getRound().force();
        }

        return this;
    }

    AssertRoom toAction(Optype op, int chipsBet) {
        super.getRound().action(op, chipsBet);
        return this;
    }

    AssertRoom toAction(Optype op) {
        return toAction(op, 0);
    }

    AssertRoom toOnShowdown() {
        super.onShowdown();
        return this;
    }

    AssertRoom assertBalance(String id, int chips) {
        assertEquals(chips, this.getUserBalance(id));
        return this;
    }

    AssertRoom assertRoundNum(int num) {
        assertEquals(num, this.getRoundNum());
        return this;
    }

    AssertRoom assertPlayerChips(String id, int chips) {
        assertEquals(chips, this.getPlayerChips(id));
        return this;
    }

    AssertRoom assertRunning(boolean running) {
        assertEquals(running, super.running());
        return this;
    }

    AssertRoom assertNoExecute(int seatId, int noExecute) {
        var seat = this.getSeat(seatId);
        assertEquals(noExecute, seat.getNoExecute());
        return this;
    }

    AssertRoom assertPending(int seatId, boolean value) {
        var seat = this.getSeat(seatId);
        assertEquals(value, seat.isPending());
        return this;
    }

    AssertRoom assertOccupiedNum(int num) {
        assertEquals(num, this.occupiedNum());
        return this;
    }

    AssertRoom assertCapacity(int num) {
        assertEquals(num, this.getCapacity());
        return this;
    }

    AssertRoom assertException(Runnable func, Class biz) {
        try {
            func.run();
            assertTrue(false);
        } catch (Exception e) {
            Assertions.assertEquals(biz, e.getClass());
        }
        return this;
    }

    AssertRoom assertPlayerSeat(Integer... seatIds) {
        assertOccupiedNum(seatIds.length);
        var seats = this.getSeats();
        for (var v : seatIds) {
            assertTrue(seats.get(v).occupied());
        }
        return this;
    }
}
