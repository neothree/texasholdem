package com.texasthree.zone.room;

import com.texasthree.utility.utlis.StringUtils;
import com.texasthree.zone.Tester;
import org.junit.jupiter.api.Test;

import static junit.framework.TestCase.assertEquals;

class ProtocalTest {

    @Test
    void testRoomData() throws Exception {
        var id = StringUtils.get10UUID();
        var capacity = 8;
        var user = Tester.createUser();
        var seatId = 2;

        var room = new Room(id, capacity);
        room.addUser(user);
        room.sitDown(user, seatId);

        var data = new Protocal.RoomData(room, user.getId());
        assertEquals(id, data.id);
        assertEquals(capacity, data.capacity.intValue());
        assertEquals(1, data.seats.size());
        assertEquals(seatId, data.seats.get(0).seatId.intValue());
    }

}