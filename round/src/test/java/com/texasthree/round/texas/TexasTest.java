package com.texasthree.round.texas;


import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Texas Tester.
 *
 * @author <Authors name>
 * @version 1.0
 * @since <pre>Jun 17, 2020</pre>
 */
public class TexasTest {


    /**
     * Method: start()
     */
    @Test
    public void testOther() throws Exception {
        var texas = new TexasBuilder(3)
                .smallBlind(1)
                .ante(1)
                .build();
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
        Ring<Player> ring = Ring.create(2);
        ring.setValue(new Player(1, 500));
        ring.getNext().setValue(new Player(2, 50));
        var builder = new TexasBuilder()
                .ring(ring)
                .smallBlind(50);
        var texas = builder.build();
        var move = texas.start();
        assertEquals(1, texas.dealer().getId());
        assertEquals(1, texas.sbPlayer().getId());
        assertEquals(2, texas.bbPlayer().getId());
        assertEquals(50, texas.smallBlind());
        assertEquals(Move.Showdown, move);
    }

    @Test
    public void testCircle() throws Exception {
        var builder = new TexasBuilder(5);
        Texas texas = builder.build();
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
        builder = new TexasBuilder(5);
        texas = builder.build();
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
        builder = new TexasBuilder(5);
        texas = builder.build();
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
        var texas = new TexasBuilder(2).initChips(200).build();
        texas.start();
        assertEquals(Move.NextOp, texas.action(createRaise(198)));

        /**
         * 第一圈都 call, 大盲多一次押注
         */
        texas = new TexasBuilder(3).build();
        assertEquals(Move.NextOp, texas.start());
        assertEquals(Move.NextOp, texas.action(new Action(Optype.Call)));
        assertEquals(Move.NextOp, texas.action(new Action(Optype.Call)));
        assertEquals(Move.CircleEnd, texas.action(new Action(Optype.Check)));

        // 小盲为0
        texas = new TexasBuilder(2).initChips(100).smallBlind(0).build();
        assertEquals(Move.NextOp, texas.start());
        assertEquals(Move.NextOp, texas.action(new Action(Optype.Check)));
        assertEquals(Move.CircleEnd, texas.action(new Action(Optype.Check)));

        // 复现
        Ring<Player> ring = Ring.create(3);
        ring.setValue(new Player(1, 100));
        ring.getNext().setValue(new Player(2, 200));
        ring.getPrev().setValue(new Player(3, 200));
        var config = new TexasBuilder().ring(ring);
        texas = config.build();
        assertEquals(Move.NextOp, texas.start());
        equalsOp(1, new Action(Optype.Allin), Move.NextOp, texas);
        equalsOp(2, new Action(Optype.Call), Move.NextOp, texas);
        equalsOp(3, new Action(Optype.Call), Move.CircleEnd, texas);
        equalsOp(2, new Action(Optype.Fold), Move.Showdown, texas);
    }

    @Test
    public void testOpPlayer() throws Exception {
        TexasBuilder config = new TexasBuilder(5);
        Texas texas = config.build();
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
            ring.setValue(new Player(i, 100));
            ring = ring.getNext();
        }
        ring.setValue(new Player(5, 50));
        config = new TexasBuilder().ring(ring);
        texas = config.build();
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
        config = new TexasBuilder();
        texas = config.build();
        texas.start();

        equalsOp(1, new Action(Optype.Call), Move.NextOp, texas);
        equalsOp(2, new Action(Optype.Check), Move.CircleEnd, texas);

        assertEquals(Circle.Flop, texas.circle());
        assertTrue(texas.opPlayer().getId() == 2);

        ////////////////////////////////////////////////////////////////////////
        //  短牌：在“两倍前注”下, 开局后，所有玩家都call，最后应该到庄家还有一次option
        var regulations = new HashMap<Regulation, Integer>();
        regulations.put(Regulation.DoubleAnte, 1);
        config = new TexasBuilder()
                .smallBlind(0)
                .ante(1)
                .regulations(regulations);
        texas = config.build();
        texas.start();
        equalsOp(2, new Action(Optype.Call), Move.NextOp, texas);
        equalsOp(1, new Action(Optype.Check), Move.CircleEnd, texas);

        ////////////////////////////////////////////////////////////////////////
        //  短牌：在“smallBlind == 0”下, 开局后，所有玩家都check，最后应该到庄家还有一次option
        config = new TexasBuilder().smallBlind(0).ante(1);
        texas = config.build();
        texas.start();
        equalsOp(2, new Action(Optype.Check), Move.NextOp, texas);
        equalsOp(1, new Action(Optype.Check), Move.CircleEnd, texas);
    }

    @Test
    public void testAuth() throws Exception {
        TexasBuilder config = new TexasBuilder(5);
        Texas texas = config.build();
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
