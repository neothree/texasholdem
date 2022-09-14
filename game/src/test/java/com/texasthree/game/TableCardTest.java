package com.texasthree.game;

import com.texasthree.game.texas.Card;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;


/**
 * Deck Tester.
 *
 * @author <Authors name>
 * @version 1.0
 * @since <pre>Jun 16, 2020</pre>
 */
public class TableCardTest extends AllCard {

    /**
     * Method: getInstance()
     */
    @Test
    public void testGetInstance() throws Exception {
        List<Card> pair = Deck.getInstance().shuffle();
        Set<Integer> set = new HashSet<>();
        pair.forEach(v -> set.add(v.getId()));
        assertEquals(52, set.size());
    }

    @Test
    public void testCard() throws Exception {
        assertNotEquals(spades5, clubA);
        assertEquals(spades5, spades5);

        var set = new HashSet<Card>();
        set.add(spades5);
        set.add(spades5);
        assertEquals(1, set.size());
        assertTrue(set.contains(spades5));
        assertFalse(set.contains(club4));
    }
}
