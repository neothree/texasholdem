package com.texasthree.zone.room;

import com.texasthree.utility.utlis.StringUtils;
import com.texasthree.zone.Tester;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RoomTest {

    @Test
    void testData() throws Exception {
        var id = StringUtils.get10UUID();
        var capacity = 8;
        var user = Tester.createUser();
        var seatId = 2;
        var room = new Room(id, capacity);
        room.sitDown(user, seatId);

        var data = room.data();
        assertEquals(data.id, room.getId());
        assertEquals(data.capacity, room.getCapacity());
        assertEquals(1, data.seats.size());
        assertEquals(seatId, data.seats.get(0).seatId);
    }
}