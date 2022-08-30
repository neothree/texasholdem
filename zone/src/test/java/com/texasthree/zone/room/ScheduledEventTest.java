package com.texasthree.zone.room;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ScheduledEventTest {

    @Test
    void testCheck() throws Exception {
        var num = new Num();
        assertEquals(0, num.N);
        var event = new ScheduledEvent(() -> num.N++, 500);

        event.check();
        assertEquals(0, num.N);

        Thread.sleep(1000);

        event.check();
        assertEquals(1, num.N);
        event.check();
        assertEquals(1, num.N);
    }

    @Test
    void testForce() throws Exception {
        var num = new Num();
        assertEquals(0, num.N);
        var event = new ScheduledEvent(() -> num.N++, 1000);
        assertEquals(0, num.N);

        event.force();
        assertEquals(1, num.N);
        event.force();
        assertEquals(1, num.N);

    }

    private static class Num {
        public int N;
    }
}