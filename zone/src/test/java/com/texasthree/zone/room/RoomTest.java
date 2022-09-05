package com.texasthree.zone.room;

import com.texasthree.utility.utlis.StringUtils;
import com.texasthree.zone.Tester;
import org.junit.jupiter.api.Test;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

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
        assertEquals(0, room.occupiedNum());
        var u1 = Tester.createUser();
        room.addUser(u1);
        room.sitDown(u1, 0);
        assertEquals(1, room.occupiedNum());
        var u2 = Tester.createUser();
        room.addUser(u2);
        room.sitDown(u2, 1);
        assertEquals(2, room.occupiedNum());

        var u3 = Tester.createUser();
        room.addUser(u3);
        room.sitDown(u3, 2);
        assertEquals(3, room.occupiedNum());
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
                .assertPlayerChips(u2.getId(), initChips - 2);
    }

    @Test
    void testTryStart() throws Exception {
        var u1 = Tester.createUser();
        var u2 = Tester.createUser();
        var room = AssertRoom.build()
                .toAddUser(u1).toSitDown(u1, 0)
                .assertRunning(false).toForce().assertRunning(false)
                .toAddUser(u2).toSitDown(u2, 1)
                .assertRunning(false).toForce().assertRunning(true);
        assertNotNull(room.getRound());

        AssertRoom.build()
                .toAddUser(u1).assertUserChips(u1.getId(), Room.initChips)
                .toAddUser(u2).assertUserChips(u2.getId(), Room.initChips)
                .toSitDown(u1, 1)
                .toSitDown(u2, 2)
                // 玩家余额不足，不能开局
                .toTakeout(u2.getId()).assertUserChips(u2.getId(), 0)
                .toForce().assertRunning(false);
    }

    @Test
    void testShowdown() throws Exception {
        var u1 = Tester.createUser();
        var u2 = Tester.createUser();
        var room = AssertRoom.build()
                .toAddUser(u1).toSitDown(u1, 0)
                .assertRunning(false).toForce().assertRunning(false)
                .toAddUser(u2).toSitDown(u2, 1)
                .assertPlayerChips(u1.getId(), Room.initChips)
                .assertPlayerChips(u2.getId(), Room.initChips)
                .assertRunning(false).toForce().assertRunning(true)
                .toForce().assertRunning(true)
                .toRoundForce().toRoundForce().toRoundForce().assertRunning(true)
                .toOnShowdown().assertRunning(false);
        assertNotEquals(Room.initChips, room.getUserChips(u1.getId()));
        assertNotEquals(Room.initChips, room.getUserChips(u2.getId()));
    }

    @Test
    void testRestart() throws Exception {
        var u1 = Tester.createUser();
        var u2 = Tester.createUser();
        AssertRoom.build()
                .toAddUser(u1).toSitDown(u1, 0)
                .assertRunning(false).toForce().assertRunning(false)
                .toAddUser(u2).toSitDown(u2, 1)
                .assertRoundNum(0)
                .assertRunning(false).toForce().assertRunning(true)
                .assertRoundNum(1)
                .toForce().assertRunning(true)
                .toRoundForce().toRoundForce().toRoundForce().assertRunning(true)
                // 一局结束
                .toOnShowdown().assertRunning(false)
                .toForce().assertRunning(true)
                .assertRoundNum(2);
    }

    @Test
    void testSeatExecute() throws Exception {
        var u1 = Tester.createUser();
        var u2 = Tester.createUser();
        AssertRoom.build()
                .toAddUser(u1).toSitDown(u1, 0)
                .toAddUser(u2).toSitDown(u2, 1)
                .assertNoExecute(0, 0)
                .assertNoExecute(1, 0)
                .toForce().assertRunning(true)
                .toForce().assertRunning(true)
                .toRoundForce().toRoundForce().toRoundForce().assertRunning(true)
                .toOnShowdown()
                .assertNoExecute(0, 1)
                .assertNoExecute(1, 0);
    }

    @Test
    void testData() throws Exception {
        var id = StringUtils.get10UUID();
        var capacity = 8;
        var user = Tester.createUser();
        var seatId = 2;

        var room = new Room(id, capacity);
        room.addUser(user);
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