package com.texasthree.zone.room;

import com.texasthree.utility.utlis.StringUtils;
import com.texasthree.zone.user.User;

import static org.junit.Assert.assertEquals;

/**
 * @author: neo
 * @create: 2022-09-05 11:21
 */
public class AssertRoom extends Room {

    public static AssertRoom build() {
        return new AssertRoom(StringUtils.get10UUID(), 9);
    }

    public AssertRoom(String id, int capacity) {
        super(id, capacity);
    }

    public AssertRoom toAddUser(User user) {
        super.addUser(user);
        return this;
    }

    public AssertRoom toBring(String uid) {
        super.bring(uid);
        return this;
    }

    public AssertRoom toSitDown(User user, int seatId) {
        super.sitDown(user, seatId);
        return this;
    }

    public AssertRoom toTakeout(String uid) {
        super.takeout(uid);
        return this;
    }

    public AssertRoom toForce() {
        super.force();
        return this;
    }

    public AssertRoom assertUserChips(String id, int chips) {
        assertEquals(chips, this.getUserChips(id));
        return this;
    }

    public AssertRoom assertPlayerChips(String id, int chips) {
        assertEquals(chips, this.getPlayerChips(id));
        return this;
    }

    public AssertRoom assertRunning(boolean running) {
        assertEquals(running, super.running());
        return this;
    }
}
