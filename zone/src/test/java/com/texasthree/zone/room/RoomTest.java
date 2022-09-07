package com.texasthree.zone.room;

import com.texasthree.utility.utlis.StringUtils;
import com.texasthree.zone.Tester;
import org.junit.jupiter.api.Test;

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
        var u1 = Tester.createUser();
        var u2 = Tester.createUser();
        var room = AssertRoom.build();

        room.assertOccupiedNum(0)
                .toAddUser(u1).assertOccupiedNum(0)
                .toAddUser(u2).assertOccupiedNum(0)
                .toSitDown(u1, 4).assertOccupiedNum(1)
                .toSitUp(u1).assertOccupiedNum(0)
                .toSitUp(u2).assertOccupiedNum(0)
                .toSitDown(u2, 3).assertOccupiedNum(1)
                .toSitDown(u1, 4).assertOccupiedNum(2)
                .assertException(() -> room.toSitDown(u1, 5), IllegalArgumentException.class).assertOccupiedNum(2)
        ;
    }

    @Test
    void testSitUp() throws Exception {
        var u1 = Tester.createUser();
        var u2 = Tester.createUser();
        AssertRoom.build().assertOccupiedNum(0)
                .toAddUser(u1).assertOccupiedNum(0)
                .toAddUser(u2).assertOccupiedNum(0)
                .toSitDown(u1, 4).assertOccupiedNum(1)
                .toSitUp(u1).assertOccupiedNum(0)
                .toSitUp(u2).assertOccupiedNum(0);
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
                // 新一局开始
                .toForce().assertRunning(true)
                .assertRoundNum(2);

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

        // 不知道输赢，所以没办法判断现在玩家的筹码是多少
        assertNotEquals(Room.initChips, room.getUserChips(u1.getId()));
        assertNotEquals(Room.initChips, room.getUserChips(u2.getId()));
    }

    @Test
    void testSeatExecute() throws Exception {
        var u1 = Tester.createRobot();
        var u2 = Tester.createUser();
        var u3 = Tester.createUser();
        AssertRoom.build()
                .toAddUser(u1).toSitDown(u1, 0)
                .toAddUser(u2).toSitDown(u2, 1)
                .toAddUser(u3).toSitDown(u3, 2)
                .assertNoExecute(0, 0)
                .assertNoExecute(1, 0)
                .assertNoExecute(2, 0)
                .toForce().assertRunning(true)
                .toForce().assertRunning(true)
                .toRoundForce(5).assertRunning(true)
                .toOnShowdown()
                // 第一局未操作
                .assertNoExecute(0, 1)
                .assertNoExecute(1, 1)
                .assertNoExecute(2, 0)
                .toForce().assertRunning(true)
                .toForce().assertRunning(true)
                .toRoundForce(5).assertRunning(true)
                .toOnShowdown()
                // 第二局未操作
                .assertNoExecute(0, 2)
                .assertNoExecute(1, 2)
                .assertNoExecute(2, 0)
                .toForce().assertRunning(true)
                .toForce().assertRunning(true)
                .toRoundForce(5).assertRunning(true)
                .toOnShowdown()
                .assertOccupiedNum(3)
                .assertNoExecute(0, 3)
                .assertNoExecute(1, 3)
                .assertNoExecute(2, 0)
                .toForce().assertRunning(true)
                // 第三局未操作，下次检测会 seatId=1 强制站起, seatId=0 是机器人可以不用站起
                .assertOccupiedNum(2)
                .assertPlayerSeat(0, 2)
        ;
    }

    @Test
    void testPending() throws Exception {
        var u0 = Tester.createUser();
        var u1 = Tester.createUser();
        var capacity = 2;
        AssertRoom.build(capacity).assertCapacity(capacity)
                .toAddUser(u0)
                // 初始化状态
                .assertPending(0, false).assertPending(1, false)

                // u0在座位上
                .toSitDown(u0, 0)
                .assertPending(0, false).assertPending(1, false)
                .toPending(u0)
                .assertPending(0, true).assertPending(1, false)
                .toPendingCancel(u0)
                .assertPending(0, false).assertPending(1, false)

                // u1 没有在座位上，操作无效
                .toPending(u1)
                .assertPending(0, false).assertPending(1, false)
                .toPendingCancel(u1)
                .assertPending(0, false).assertPending(1, false)
        ;
    }

    @Test
    void testDispose() throws Exception {
        var room = new Room(StringUtils.get10UUID(), 8);
        assertNotNull(Room.getRoom(room.getId()));

        room.dispose();
        assertNull(Room.getRoom(room.getId()));
    }
}