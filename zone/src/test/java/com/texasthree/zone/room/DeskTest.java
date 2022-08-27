package com.texasthree.zone.room;

import com.texasthree.zone.Tester;
import org.junit.jupiter.api.Test;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

class DeskTest {


    @Test
    void testSitDown() throws Exception {
        var desk = new Desk(9);
        assertEquals(0, desk.playerNum());
        var u1 = Tester.createUser();
        desk.sitDown(u1, 0);
        assertEquals(1, desk.playerNum());
        var u2 = Tester.createUser();
        desk.sitDown(u2, 1);
        assertEquals(2, desk.playerNum());

        // 两个人自动开局
        assertTrue(desk.running());

        var u3 = Tester.createUser();
        desk.sitDown(u3, 2);
        assertEquals(3, desk.playerNum());
    }
}