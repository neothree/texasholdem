package com.texasthree.core;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.Assert.*;

class TexasConfig {
    int playerNum = 2;
    int smallBlind = 1;
    int ante = 0;
    int initChips = 100;
    Ring<Player> ring;
    List<Card> leftCard = TableCard.getInstance().shuffle();

    Texas make() {
        if (ring == null) {
            ring = Ring.create(playerNum);
            for (int i = 0; i < playerNum; i++) {
                ring.setValue(new Player(i, initChips, new Hand(leftCard.subList(i * 2, i * 2 + 2))));
                ring = ring.getNext();
            }
        }
        Map<Law, Integer> laws = new HashMap<>();
        laws.put(Law.SmallBlind, smallBlind);
        laws.put(Law.Ante, ante);
        laws.put(Law.Dealer, 1);

        if (ring.size() == 2) {
            laws.put(Law.SB, 1);
            laws.put(Law.BB, 2);
        } else {
            laws.put(Law.SB, 2);
            laws.put(Law.BB, 3);
        }

        return new Texas(laws, ring, leftCard);
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

    private Texas create(int playerNum) {
        return create(playerNum, 100);
    }


    private Texas create(int playerNum, int initChips) {
        return create(playerNum, initChips, 1);
    }

    private Texas create(int playerNum, int initChips, int smallBlind) {
        Map<Law, Integer> law = new HashMap<>();
        law.put(Law.SmallBlind, smallBlind);

        List<Card> leftCard = TableCard.getInstance().shuffle();

        Ring<Player> ring = Ring.create(playerNum);
        for (int i = 0; i < playerNum; i++) {
            ring.setValue(new Player(i, initChips, new Hand(leftCard.subList(i * 2, i * 2 + 2))));
            ring = ring.getNext();
        }
        assertNotNull(ring.value);

        leftCard = leftCard.subList(ring.size(), leftCard.size());
        return create(ring, leftCard, law);
    }

    private Texas create(Ring ring, List<Card> leftCard, Map<Law, Integer> law) {
        law.put(Law.Dealer, 0);

        if (ring.size() == 2) {
            law.put(Law.SB, 0);
            law.put(Law.BB, 1);
        } else {
            law.put(Law.SB, 1);
            law.put(Law.BB, 2);
        }

        return new Texas(law, ring, leftCard);
    }

    /**
     * Method: start()
     */
    @Test
    public void testOther() throws Exception {
        Map<Law, Integer> law = new HashMap<>();
        law.put(Law.SmallBlind, 1);
        law.put(Law.Ante, 1);
        law.put(Law.Dealer, 0);
        law.put(Law.SB, 1);
        law.put(Law.BB, 2);

        Texas texas = create(3);
        assertEquals(0, texas.dealer().getId());
        assertEquals(1, texas.sbPlayer().getId());
        assertEquals(2, texas.bbPlayer().getId());
        assertEquals(1, texas.smallBlind());
        assertEquals(1, texas.ante());
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


        Texas texas = create(2, 100, 50);
        Move move = texas.start();
        assertEquals(0, texas.dealer().getId());
        assertEquals(0, texas.sbPlayer().getId());
        assertEquals(1, texas.bbPlayer().getId());
        assertEquals(50, texas.smallBlind());
        assertEquals(Move.Showdown, move);
    }

    @Test
    public void testAction() throws Exception {
        Texas texas = create(2, 200);
        texas.start();
        assertEquals(Move.NextOp, texas.action(createRaise(198)));

        /**
         * 第一圈都 call, 大盲多一次押注
         */
        texas = create(3);
        assertEquals(Move.NextOp, texas.start());
        assertEquals(Move.NextOp, texas.action(new Action(Optype.Call)));
        assertEquals(Move.NextOp, texas.action(new Action(Optype.Call)));
        assertEquals(Move.CircleEnd, texas.action(new Action(Optype.Check)));

        // 小盲为0
        texas = create(2, 100, 0);
        assertEquals(Move.NextOp, texas.start());
        assertEquals(Move.NextOp, texas.action(new Action(Optype.Check)));
        assertEquals(Move.CircleEnd, texas.action(new Action(Optype.Check)));


        // TODO 两倍前注
//        Map<Law, Integer> law = new HashMap<>();
//        law.put(Law.SmallBlind, 0);
//        law.put(Law.Ante, 1);
//        law.put(Law.DoubleAnte, 1);
//        texas = create(2, 100, law);
//        assertEquals(Move.NextOp, texas.start());
//        assertEquals(texas.opPlayer().getId(), 1);
//        equalsMoveAndOp(new Action(Optype.Call), Move.NextOp, 1, texas);
//        equalsMoveAndOp(new Action(Optype.Check), Move.CircleEnd, 0, texas);

        // 复现
        Ring<Player> ring = Ring.create(3);
        ring.setValue(new Player(1, 100, new Hand(new ArrayList<>())));
        ring.getNext().setValue(new Player(2, 200, new Hand(new ArrayList<>())));
        ring.getPrev().setValue(new Player(3, 200, new Hand(new ArrayList<>())));
        TexasConfig config = new TexasConfig();
        config.ring = ring;
        texas = config.make();
        assertEquals(Move.NextOp, texas.start());
        equalsMoveAndOp(1, new Action(Optype.Allin), Move.NextOp, texas);
        equalsMoveAndOp(2, new Action(Optype.Call), Move.NextOp, texas);
        equalsMoveAndOp(3, new Action(Optype.Call), Move.CircleEnd, texas);
        equalsMoveAndOp(2, new Action(Optype.Fold), Move.Showdown, texas);
    }

    private Action createRaise(int chipsAdd) {
        return new Action(-1, Optype.Raise, 0, chipsAdd, 0, 0);
    }

    private void equalsMoveAndOp(Integer opId, Action act, Move move, Texas texas) throws Exception {
        assertTrue(opId == texas.opPlayer().getId());
        assertEquals(move, texas.action(act));
    }
}
