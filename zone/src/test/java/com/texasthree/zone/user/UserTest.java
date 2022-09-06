package com.texasthree.zone.user;

import com.texasthree.zone.Tester;
import com.texasthree.zone.room.AssertRoom;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void testUser() throws Exception {
        var user = Tester.createUser();
        assertNull(user.getRoom());

        var room = AssertRoom.build();
        assertFalse(room.contains(user.getId()));
        user.enter(room);
        assertTrue(room.contains(user.getId()));

        var userRoom = user.getRoom();
        assertNotNull(userRoom);
        assertEquals(userRoom.getId(), room.getId());
    }
}