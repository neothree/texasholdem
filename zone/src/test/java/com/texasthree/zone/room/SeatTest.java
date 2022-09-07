package com.texasthree.zone.room;

import com.texasthree.utility.utlis.StringUtils;
import com.texasthree.zone.Tester;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SeatTest {

    @Test
    void testOccupy() throws Exception {
        var seatId = 1;
        var roomId = StringUtils.get10UUID();
        var seat = new Seat(roomId, seatId);
        assertEquals(seatId, seat.id);
        assertEquals(roomId, seat.roomId);
        assertFalse(seat.occupied());
        assertNull(seat.getUser());
        assertNull(seat.getUid());

        var user = Tester.createUser();
        seat.occupy(user);
        assertTrue(seat.occupied());
        assertEquals(user.getId(), seat.getUid());
    }

    @Test
    void testExecute() throws Exception {
        var seat = new Seat(StringUtils.get10UUID(), 2);
        assertEquals(0, seat.getNoExecute());
        seat.execute(false);
        assertEquals(0, seat.getNoExecute());

        seat.occupy(Tester.createUser());
        assertEquals(0, seat.getNoExecute());
        seat.execute(false);
        assertEquals(1, seat.getNoExecute());
        seat.execute(false);
        assertEquals(2, seat.getNoExecute());
        seat.execute(false);
        assertEquals(3, seat.getNoExecute());
        seat.execute(true);
        assertEquals(0, seat.getNoExecute());

        seat.execute(false);
        assertEquals(1, seat.getNoExecute());
        seat.occupyEnd();
        assertEquals(0, seat.getNoExecute());
    }

    @Test
    void testPending() throws Exception {
        var seat = new Seat(StringUtils.get10UUID(), 2);
        assertFalse(seat.isPending());
        seat.pending();
        assertFalse(seat.isPending());

        var user = Tester.createUser();
        seat.occupy(user);
        assertFalse(seat.isPending());
        seat.pending();
        assertTrue(seat.isPending());
        seat.pendingCancel();
        assertFalse(seat.isPending());
    }
}