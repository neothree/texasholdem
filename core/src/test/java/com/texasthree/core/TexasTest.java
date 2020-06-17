package com.texasthree.core;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

class TexasConfig {
    public int smallBind = 1;
    public int ante = 0;
    public int playerNum = 3;

    public TexasConfig() {
    }

    public TexasConfig(int playerNum) {
        List<Card> leftCard = TableCard.getInstance().shuffle();

        Ring<Player> ring = new Ring<>(new Player(0, 100, new Hand(leftCard.subList(0, 2))));
        for (int i = 1; i < 3; i++) {
            ring.link(new Player(i, 100, new Hand(leftCard.subList(i * 2, i * 2 + 2))));
        }
        assertNotNull(ring.value);

        leftCard = leftCard.subList(ring.size(), leftCard.size());
    }
}

/**
 * Texas Tester.
 *
 * @author <Authors name>
 * @version 1.0
 * @since <pre>Jun 17, 2020</pre>
 */
public class TexasTest {

    private static AllCard c;

    @BeforeAll
    public static void before() {
        c = AllCard.getInstance();
    }

    /**
     * Method: start()
     */
    @Test
    public void testStart() throws Exception {
        Map<Law, Integer> law = new HashMap<>();
        law.put(Law.SmallBlind, 50);
        law.put(Law.Dealer, 0);
        law.put(Law.SB, 0);
        law.put(Law.BB, 1);

        List<Card> leftCard = TableCard.getInstance().shuffle();

        Ring<Player> ring = new Ring<>(new Player(0, 500, new Hand(leftCard.subList(0, 2))));
        ring.link(new Player(1, 50, new Hand(leftCard.subList(2, 4))));

        leftCard = Arrays.asList(c.club9, c.club10, c.spadesA, c.heartK, c.heart4);
        Texas texas = new Texas(law, ring, leftCard);
        Move move = texas.start();
        assertEquals(0, texas.dealer().getId());
        assertEquals(0, texas.sbPlayer().getId());
        assertEquals(1, texas.bbPlayer().getId());
        assertEquals(50, texas.smallBlind());
        assertEquals(Move.Showdown, move);
    }

} 
