package com.texasthree.game;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author: neo
 * @create: 2022-09-02 16:01
 */
public class Tester {
    public static void assertException(Runnable func, Class biz) {
        try {
            func.run();
            assertTrue(false);
        } catch (Exception e) {
            assertEquals(biz, e.getClass());
        }
    }
}
