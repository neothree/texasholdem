package com.texasthree.zone.room;

import com.texasthree.utility.utlis.StringUtils;
import com.texasthree.zone.Tester;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SeatTest {

    @Test
    void testSeat() throws Exception {
        var seatId = 1;
        var roomId = StringUtils.get10UUID();
        var seat = new Seat(roomId, seatId);
        assertEquals(seatId, seat.id);
        assertEquals(roomId, seat.roomId);
        assertFalse(seat.occupied());
        assertNull(seat.getUser());
        assertNull(seat.getUid());

        var user = Tester.createUser();
        seat.sitDown(user);
        assertTrue(seat.occupied());
        assertEquals(user.getId(), seat.getUid());
    }
}