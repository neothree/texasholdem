package com.texasthree.game;

import com.texasthree.game.texas.Card;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;


/**
 * Deck Tester.
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
        List<Card> pair = Deck.getInstance().shuffle();
        Set<Integer> set = new HashSet<>();
        pair.forEach(v -> set.add(v.getId()));
        assertEquals(54, set.size());
    }
}
