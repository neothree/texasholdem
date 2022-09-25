package com.texasthree.zone.round;

import com.texasthree.zone.Tester;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserPlayerTest {

    @Test
    void testUserPlayer() throws Exception {
        var user = Tester.createUser();
        var chips = 20;
        var seatId = 2;
        var up = new UserPlayer(seatId, user, chips);
        assertEquals(seatId, up.seatId);
        assertEquals(chips, up.getChips());
        assertEquals(user.getId(), up.getId());
        assertFalse(up.isExecute());

        up.execute();
        assertTrue(up.isExecute());

        assertFalse(up.isGain());
        up.gain();
        assertTrue(up.isGain());
    }
}