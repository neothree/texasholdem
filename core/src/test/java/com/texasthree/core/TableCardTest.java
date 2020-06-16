package com.texasthree.core;

import org.junit.jupiter.api.Test;

import java.util.List;


import static org.junit.Assert.assertEquals;


/**
 * TableCard Tester.
 *
 * @author <Authors name>
 * @version 1.0
 * @since <pre>Jun 16, 2020</pre>
 */
public class TableCardTest {

    /**
     * Method: getInstance()
     */
    @Test
    public void testGetInstance() throws Exception {
        List<Card> pair = TableCard.getInstance().shuffle();
        assertEquals(54, pair.size());
    }
}
