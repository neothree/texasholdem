package com.texasthree.zone.room;

import com.texasthree.utility.utlis.StringUtils;
import com.texasthree.zone.Tester;
import org.junit.jupiter.api.Test;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

class ProtocalTest {

    @Test
    void testRoomData() throws Exception {
        var id = StringUtils.get10UUID();
        var capacity = 8;
        var user = Tester.createUser();
        var seatId = 2;

        var room = new Room(id, capacity, null);
        room.addUser(user);
        room.sitDown(user, seatId);

        var data = new Protocal.RoomData(room, user.getId());
        assertEquals(id, data.id);
        assertEquals(capacity, data.capacity.intValue());
        assertEquals(1, data.seats.size());
        assertEquals(seatId, data.seats.get(0).seatId.intValue());
    }

    @Test
    void testRank() throws Exception {
        var room = new Room("11", 9, null);
        var rank = new Protocal.Rank(room);
        assertEquals(0, rank.insurance);
        assertTrue(rank.buyins.isEmpty());

        var u = Tester.createUser();
        room.buyin(u, 100);
        rank = new Protocal.Rank(room);
        assertEquals(0, rank.insurance);
        assertEquals(1, rank.buyins.size());
        assertBuyin(rank.buyins.get(0), 100, 0, u.getName());

        var u1 = Tester.createUser();
        room.buyin(u1, 500);
        rank = new Protocal.Rank(room);
        assertEquals(0, rank.insurance);
        assertEquals(2, rank.buyins.size());
        assertBuyin(rank.buyins.get(0), 500, 0, u1.getName());
        assertBuyin(rank.buyins.get(1), 100, 0, u.getName());

        var u2 = Tester.createUser();
        room.buyin(u2, 200);
        room.changeProfit(u2.getId(), 10);
        rank = new Protocal.Rank(room);
        assertEquals(3, rank.buyins.size());
        assertBuyin(rank.buyins.get(0), 200, 10, u2.getName());
        assertBuyin(rank.buyins.get(1), 500, 0, u1.getName());
        assertBuyin(rank.buyins.get(2), 100, 0, u.getName());

        var u3 = Tester.createUser();
        room.buyin(u3, 300);
        room.changeProfit(u3.getId(), 10);
        rank = new Protocal.Rank(room);
        assertEquals(4, rank.buyins.size());
        assertBuyin(rank.buyins.get(0), 300, 10, u3.getName());
        assertBuyin(rank.buyins.get(1), 200, 10, u2.getName());
        assertBuyin(rank.buyins.get(2), 500, 0, u1.getName());
        assertBuyin(rank.buyins.get(3), 100, 0, u.getName());
    }

    void assertBuyin(Protocal.Buyin buyin, int sum, int profit, String name) {
        assertEquals(name, buyin.name);
        assertEquals(sum, buyin.buyin);
        assertEquals(profit, buyin.profit);
    }
}