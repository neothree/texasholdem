package com.texasthree.round;


import com.texasthree.round.texas.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Texas Tester.
 *
 * @author <Authors name>
 * @version 1.0
 * @since <pre>Jun 17, 2020</pre>
 */
public class TexasTest {

    static class TexasConfig {
        int playerNum = 2;
        int smallBlind = 1;
        int ante = 0;
        int initChips = 100;
        Ring<Player> ring;
        List<Card> leftCard = TableCard.getInstance().shuffle();
        Map<Regulation, Integer> laws;

        public TexasConfig() {

        }

        public TexasConfig(int playerNum) {
            this.playerNum = playerNum;
        }

        Texas make() {
            if (ring == null) {
                ring = Ring.create(playerNum);
                for (int i = 1; i <= playerNum; i++) {
                    ring.setValue(new Player(i, initChips, new Hand(leftCard.subList(i * 2, i * 2 + 2))));
                    ring = ring.getNext();
                }
            }
            if (laws == null) {
                laws = new HashMap<>();
            }

            laws.put(Regulation.SmallBlind, smallBlind);
            laws.put(Regulation.Ante, ante);
            laws.put(Regulation.Dealer, 1);

            if (ring.size() == 2) {
                laws.put(Regulation.SB, 1);
                laws.put(Regulation.BB, 2);
            } else {
                laws.put(Regulation.SB, 2);
                laws.put(Regulation.BB, 3);
            }

            return new Texas(laws, ring, leftCard);
        }
    }


    private Texas create(int playerNum) {
        return create(playerNum, 100);
    }


    private Texas create(int playerNum, int initChips) {
        return create(playerNum, initChips, 1);
    }

    private Texas create(int playerNum, int initChips, int smallBlind) {
        Map<Regulation, Integer> law = new HashMap<>();
        law.put(Regulation.SmallBlind, smallBlind);

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

    private Texas create(Ring ring, List<Card> leftCard, Map<Regulation, Integer> law) {
        law.put(Regulation.Dealer, 0);

        if (ring.size() == 2) {
            law.put(Regulation.SB, 0);
            law.put(Regulation.BB, 1);
        } else {
            law.put(Regulation.SB, 1);
            law.put(Regulation.BB, 2);
        }

        return new Texas(law, ring, leftCard);
    }

    /**
     * Method: start()
     */
    @Test
    public void testOther() throws Exception {
        TexasConfig config = new TexasConfig(3);
        config.smallBlind = 1;
        config.ante = 1;
        Texas texas = config.make();
        texas.start();

        assertEquals(1, texas.dealer().getId());
        assertEquals(2, texas.sbPlayer().getId());
        assertEquals(3, texas.bbPlayer().getId());
        assertEquals(1, texas.smallBlind());
        assertEquals(1, texas.ante());
    }

    /**
     * Method: start()
     */
    @Test
    public void testStart() throws Exception {
        TexasConfig config = new TexasConfig();
        Ring<Player> ring = Ring.create(2);
        ring.setValue(new Player(1, 500, new Hand(new ArrayList<>())));
        ring.getNext().setValue(new Player(2, 50, new Hand(new ArrayList<>())));
        config.ring = ring;
        config.smallBlind = 50;
        Texas texas = config.make();
        Move move = texas.start();
        assertEquals(1, texas.dealer().getId());
        assertEquals(1, texas.sbPlayer().getId());
        assertEquals(2, texas.bbPlayer().getId());
        assertEquals(50, texas.smallBlind());
        assertEquals(Move.Showdown, move);
    }

    @Test
    public void testCircle() throws Exception {
        TexasConfig config = new TexasConfig(5);
        Texas texas = config.make();
        texas.start();

        equalsOp(4, new Action(Optype.Call), Circle.Preflop, texas);
        equalsOp(5, new Action(Optype.Call), Circle.Preflop, texas);
        equalsOp(1, new Action(Optype.Call), Circle.Preflop, texas);
        equalsOp(2, new Action(Optype.Fold), Circle.Preflop, texas);
        equalsOp(3, new Action(Optype.Fold), Circle.Preflop, texas);

        equalsOp(4, createRaise(2), Circle.Flop, texas);
        equalsOp(5, new Action(Optype.Fold), Circle.Flop, texas);
        equalsOp(1, new Action(Optype.Call), Circle.Flop, texas);

        equalsOp(4, new Action(Optype.Check), Circle.Turn, texas);
        equalsOp(1, new Action(Optype.Check), Circle.Turn, texas);

        assertEquals(Circle.River, texas.circle());


        ////////////////////////////////////////////////////////////////////////
        config = new TexasConfig(5);
        texas = config.make();
        texas.start();

        equalsOp(4, new Action(Optype.Call), Circle.Preflop, texas);
        equalsOp(5, new Action(Optype.Call), Circle.Preflop, texas);
        equalsOp(1, new Action(Optype.Call), Circle.Preflop, texas);
        equalsOp(2, new Action(Optype.Call), Circle.Preflop, texas);
        equalsOp(3, new Action(Optype.Check), Circle.Preflop, texas);


        equalsOp(2, new Action(Optype.Fold), Circle.Flop, texas);
        equalsOp(3, new Action(Optype.Raise, 2), Circle.Flop, texas);
        equalsOp(4, new Action(Optype.Fold), Circle.Flop, texas);
        equalsOp(5, new Action(Optype.Fold), Circle.Flop, texas);
        equalsOp(1, new Action(Optype.Call), Circle.Flop, texas);

        assertEquals(Circle.Turn, texas.circle());

        ////////////////////////////////////////////////////////////////////////
        config = new TexasConfig(5);
        texas = config.make();
        texas.start();

        equalsOp(4, new Action(Optype.Raise, 4), Circle.Preflop, texas);
        equalsOp(5, new Action(Optype.Allin), Circle.Preflop, texas);
        equalsOp(1, new Action(Optype.Allin), Circle.Preflop, texas);
        equalsOp(2, new Action(Optype.Fold), Circle.Preflop, texas);
        equalsOp(3, new Action(Optype.Allin), Circle.Preflop, texas);
        equalsOp(4, new Action(Optype.Allin), Circle.Preflop, texas);
        assertTrue(texas.isOver());
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

        // 复现
        Ring<Player> ring = Ring.create(3);
        ring.setValue(new Player(1, 100, new Hand(new ArrayList<>())));
        ring.getNext().setValue(new Player(2, 200, new Hand(new ArrayList<>())));
        ring.getPrev().setValue(new Player(3, 200, new Hand(new ArrayList<>())));
        TexasConfig config = new TexasConfig();
        config.ring = ring;
        texas = config.make();
        assertEquals(Move.NextOp, texas.start());
        equalsOp(1, new Action(Optype.Allin), Move.NextOp, texas);
        equalsOp(2, new Action(Optype.Call), Move.NextOp, texas);
        equalsOp(3, new Action(Optype.Call), Move.CircleEnd, texas);
        equalsOp(2, new Action(Optype.Fold), Move.Showdown, texas);
    }

    @Test
    public void testOpPlayer() throws Exception {
        TexasConfig config = new TexasConfig(5);
        Texas texas = config.make();
        texas.start();

        // Preflop
        equalsOp(4, new Action(Optype.Call), Move.NextOp, texas);
        equalsOp(5, new Action(Optype.Call), Move.NextOp, texas);
        equalsOp(1, new Action(Optype.Call), Move.NextOp, texas);
        equalsOp(2, new Action(Optype.Call), Move.NextOp, texas);
        // 第一圈，大盲这种情况下多一次押注
        equalsOp(3, new Action(Optype.Check), Move.CircleEnd, texas);

        // Flop
        equalsOp(2, new Action(Optype.Fold), Move.NextOp, texas);
        equalsOp(3, new Action(Optype.Fold), Move.NextOp, texas);
        equalsOp(4, new Action(Optype.Raise, 2), Move.NextOp, texas);
        equalsOp(5, new Action(Optype.Call), Move.NextOp, texas);
        equalsOp(1, new Action(Optype.Fold), Move.CircleEnd, texas);

        // Turn
        equalsOp(4, new Action(Optype.Raise, 2), Move.NextOp, texas);
        equalsOp(5, new Action(Optype.Call), Move.CircleEnd, texas);

        // River
        equalsOp(4, new Action(Optype.Raise, 2), Move.NextOp, texas);
        equalsOp(5, new Action(Optype.Call), Move.Showdown, texas);

        //////////////////////////////////////////////////////////////////
        Ring<Player> ring = Ring.create(5);
        for (int i = 1; i <= 4; i++) {
            ring.setValue(new Player(i, 100, new Hand(new ArrayList<>())));
            ring = ring.getNext();
        }
        ring.setValue(new Player(5, 50, new Hand(new ArrayList<>())));
        config = new TexasConfig();
        config.ring = ring;
        texas = config.make();
        texas.start();

        equalsOp(4, new Action(Optype.Raise, 4), Move.NextOp, texas);
        equalsOp(5, new Action(Optype.Allin), Move.NextOp, texas);
        equalsOp(1, new Action(Optype.Call), Move.NextOp, texas);
        equalsOp(2, new Action(Optype.Fold), Move.NextOp, texas);
        equalsOp(3, new Action(Optype.Call), Move.NextOp, texas);
        equalsOp(4, new Action(Optype.Call), Move.CircleEnd, texas);

        equalsOp(3, new Action(Optype.Check), Move.NextOp, texas);
        equalsOp(4, new Action(Optype.Check), Move.NextOp, texas);
        equalsOp(1, new Action(Optype.Raise, 4), Move.NextOp, texas);
        equalsOp(3, new Action(Optype.Fold), Move.NextOp, texas);
        equalsOp(4, new Action(Optype.Call), Move.CircleEnd, texas);

        equalsOp(4, new Action(Optype.Raise, 5), Move.NextOp, texas);
        equalsOp(1, new Action(Optype.Call), Move.CircleEnd, texas);


        ////////////////////////////////////////////////////////////////////////
        config = new TexasConfig();
        texas = config.make();
        texas.start();

        equalsOp(1, new Action(Optype.Call), Move.NextOp, texas);
        equalsOp(2, new Action(Optype.Check), Move.CircleEnd, texas);

        assertEquals(Circle.Flop, texas.circle());
        assertTrue(texas.opPlayer().getId() == 2);

        ////////////////////////////////////////////////////////////////////////
        //  短牌：在“两倍前注”下, 开局后，所有玩家都call，最后应该到庄家还有一次option
        config = new TexasConfig();
        config.smallBlind = 0;
        config.ante = 1;
        config.laws = new HashMap<>();
        config.laws.put(Regulation.DoubleAnte, 1);
        texas = config.make();
        texas.start();
        equalsOp(2, new Action(Optype.Call), Move.NextOp, texas);
        equalsOp(1, new Action(Optype.Check), Move.CircleEnd, texas);

        ////////////////////////////////////////////////////////////////////////
        //  短牌：在“smallBlind == 0”下, 开局后，所有玩家都check，最后应该到庄家还有一次option
        config = new TexasConfig();
        config.smallBlind = 0;
        config.ante = 1;
        texas = config.make();
        texas.start();
        equalsOp(2, new Action(Optype.Check), Move.NextOp, texas);
        equalsOp(1, new Action(Optype.Check), Move.CircleEnd, texas);
    }

    @Test
    public void testAuth() throws Exception {
        TexasConfig config = new TexasConfig(5);
        Texas texas = config.make();
        texas.start();

        // chips: 2
        Player player = texas.opPlayer();
        player.changeChips(2 - player.getChips());
        equals(texas.auth(), Optype.Fold, Optype.Allin);

        // chips: 3
        player.changeChips(3 - player.getChips());
        equals(texas.auth(), Optype.Fold, Optype.Allin, Optype.Call);

        // chips: 10
        player.changeChips(10 - player.getChips());
//        equals(texas.auth(), Optype.Fold, Optype.Allin, Optype.Call);
        equalsOp(4, new Action(Optype.Call), Move.NextOp, texas);

        // chips: 1
        player = texas.opPlayer();
        player.changeChips(1 - player.getChips());
        equals(texas.auth(), Optype.Fold, Optype.Allin);
        equalsOp(5, new Action(Optype.Allin), Move.NextOp, texas);
    }

    private void equals(Map<Optype, Integer> auth, Optype... ops) {
        assertTrue(auth.size() == ops.length);
        for (Optype optype : ops) {
            assertTrue(auth.containsKey(optype));
        }
    }

    private Action createRaise(int chipsAdd) {
        return new Action(-1, Optype.Raise, 0, chipsAdd, 0, 0);
    }

    private void equalsOp(Integer opId, Action act, Move move, Texas texas) throws Exception {
        assertTrue(opId == texas.opPlayer().getId());
        assertEquals(move, texas.action(act));
    }

    private void equalsOp(Integer opId, Action act, Circle circle, Texas texas) throws Exception {
        assertEquals(opId.toString(), texas.opPlayer().getId() + "");
        assertEquals(circle, texas.circle());
        texas.action(act);
    }
}
