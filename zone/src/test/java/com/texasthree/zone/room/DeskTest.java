package com.texasthree.zone.room;

import com.texasthree.zone.Tester;
import org.junit.jupiter.api.Test;

import static junit.framework.TestCase.*;

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

        var u3 = Tester.createUser();
        desk.sitDown(u3, 2);
        assertEquals(3, desk.playerNum());
    }

    @Test
    void testTryStart() throws Exception {
        var desk = new Desk(9);
        var u1 = Tester.createUser();
        desk.sitDown(u1, 0);

        assertFalse(desk.running());
        desk.force();
        assertFalse(desk.running());

        var u2 = Tester.createUser();
        desk.sitDown(u2, 1);

        // 两个人自动开局
        assertFalse(desk.running());
        desk.force();
        assertTrue(desk.running());
        assertNotNull(desk.getRound());
    }

    @Test
    void testShowdown() throws Exception {
        var desk = new Desk(9);
        desk.sitDown(Tester.createUser(), 0);
        desk.sitDown(Tester.createUser(), 1);
        desk.force();
        assertTrue(desk.running());
        desk.force();

        // 结算
        assertTrue(desk.running());
        desk.onShowdown();
        assertFalse(desk.running());
    }

    @Test
    void testRestart() throws Exception {
        var desk = new Desk(9);
        desk.sitDown(Tester.createUser(), 0);
        desk.sitDown(Tester.createUser(), 1);
        desk.force();
        assertTrue(desk.running());
        desk.force();

        desk.onShowdown();
        assertFalse(desk.running());

        // 重新开局
        desk.force();
        desk.force();
        assertTrue(desk.running());
    }
}