package com.texasthree.core;

import com.texasthree.core.texas.Card;
import com.texasthree.core.texas.TableCard;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


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
        Set<Integer> set = new HashSet<>();
        pair.forEach(v -> set.add(v.getId()));
        assertEquals(54, set.size());
    }
}
