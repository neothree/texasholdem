package com.texasthree.zone.room;

import com.texasthree.game.texas.Optype;
import com.texasthree.utility.utlis.StringUtils;
import com.texasthree.zone.user.User;

import static org.junit.Assert.assertEquals;

/**
 * @author: neo
 * @create: 2022-09-05 11:21
 */
class AssertRoom extends Room {

    static AssertRoom build() {
        return new AssertRoom(StringUtils.get10UUID(), 9);
    }

    AssertRoom(String id, int capacity) {
        super(id, capacity);
    }

    AssertRoom toAddUser(User user) {
        super.addUser(user);
        return this;
    }

    AssertRoom toBring(String uid) {
        super.bring(uid);
        return this;
    }

    AssertRoom toSitDown(User user, int seatId) {
        super.sitDown(user, seatId);
        return this;
    }

    AssertRoom toTakeout(String uid) {
        super.takeout(uid);
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

    AssertRoom assertUserChips(String id, int chips) {
        assertEquals(chips, this.getUserChips(id));
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
        var seat = this.getSeats().get(seatId);
        assertEquals(noExecute, seat.getNoExecute());
        return this;
    }

    AssertRoom assertOccupiedNum(int num) {
        assertEquals(num, this.occupiedNum());
        return this;
    }
}
