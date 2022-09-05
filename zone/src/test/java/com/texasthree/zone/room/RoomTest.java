package com.texasthree.zone.room;

import com.texasthree.utility.utlis.StringUtils;
import com.texasthree.zone.Tester;
import org.junit.jupiter.api.Test;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;
import static org.junit.jupiter.api.Assertions.*;

class RoomTest {

    @Test
    void testBring() throws Exception {
        var user = Tester.createUser();
        AssertRoom.build()
                .toAddUser(user).assertUserChips(user.getId(), Room.initChips)
                .toBring(user.getId()).assertUserChips(user.getId(), Room.initChips * 2)
                .toTakeout(user.getId()).assertUserChips(user.getId(), 0);
    }

    @Test
    void testSitDown() throws Exception {
        var room = new Room("1", 9);
        assertEquals(0, room.playerNum());
        var u1 = Tester.createUser();
        room.sitDown(u1, 0);
        assertEquals(1, room.playerNum());
        var u2 = Tester.createUser();
        room.sitDown(u2, 1);
        assertEquals(2, room.playerNum());

        var u3 = Tester.createUser();
        room.sitDown(u3, 2);
        assertEquals(3, room.playerNum());
    }

    @Test
    void testStart() throws Exception {
        // 设定的smallBling=1
        var initChips = Room.initChips;
        var u1 = Tester.createUser();
        var u2 = Tester.createUser();
        AssertRoom.build()
                .toAddUser(u1).assertUserChips(u1.getId(), initChips)
                .toAddUser(u2).assertUserChips(u2.getId(), initChips)
                .toBring(u1.getId()).assertUserChips(u1.getId(), initChips * 2)
                .toSitDown(u1, 1)
                .toSitDown(u2, 2)
                .toForce().assertRunning(true)
                .assertPlayerChips(u1.getId(), initChips * 2 - 1)
                .assertPlayerChips(u2.getId(), initChips - 2)
        ;
    }

    @Test
    void testTryStart() throws Exception {
        var room = new Room("1", 9);
        var u1 = Tester.createUser();
        room.sitDown(u1, 0);

        assertFalse(room.running());
        room.force();
        assertFalse(room.running());

        var u2 = Tester.createUser();
        room.sitDown(u2, 1);

        // 两个人自动开局
        assertFalse(room.running());
        room.force();
        assertTrue(room.running());
        assertNotNull(room.getRound());
    }

    @Test
    void testShowdown() throws Exception {
        var room = new Room("1", 9);
        room.sitDown(Tester.createUser(), 0);
        room.sitDown(Tester.createUser(), 1);
        room.force();
        assertTrue(room.running());
        room.force();

        // 结算
        assertTrue(room.running());
        room.onShowdown();
        assertFalse(room.running());
    }

    @Test
    void testRestart() throws Exception {
        var room = new Room("1", 9);
        assertEquals(0, room.getRoundNum());
        room.sitDown(Tester.createUser(), 0);
        room.sitDown(Tester.createUser(), 1);
        room.force();
        assertTrue(room.running());
        assertEquals(1, room.getRoundNum());
        room.force();

        room.onShowdown();
        assertFalse(room.running());

        // 重新开局
        room.force();
        room.force();
        assertTrue(room.running());
        assertEquals(2, room.getRoundNum());
    }

    @Test
    void testData() throws Exception {
        var id = StringUtils.get10UUID();
        var capacity = 8;
        var user = Tester.createUser();
        var seatId = 2;
        var room = new Room(id, capacity);
        room.sitDown(user, seatId);

        var data = room.data();
        assertEquals(id, data.id);
        assertEquals(capacity, data.capacity.intValue());
        assertEquals(1, data.seats.size());
        assertEquals(seatId, data.seats.get(0).seatId.intValue());
    }

    @Test
    void testDispose() throws Exception {
        var room = new Room(StringUtils.get10UUID(), 8);
        assertNotNull(Room.getRoom(room.getId()));

        room.dispose();
        assertNull(Room.getRoom(room.getId()));
    }
}