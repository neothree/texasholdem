package com.texasthree.round.texas;


import com.texasthree.round.AllCard;
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
public class TexasTest extends AllCard {
    /**
     * Method: start()
     */
    @Test
    public void testOther() throws Exception {
        var texas = Texas.builder(3)
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

    @Test
    public void testCircle() throws Exception {
        var texas = Texas.builder(5).build();
        texas.start();

        // 1 dealer 2 sb 3 bb
        circleEquals(4, makeAct(Optype.Call), Circle.Preflop, texas);
        circleEquals(5, makeAct(Optype.Call), Circle.Preflop, texas);
        circleEquals(1, makeAct(Optype.Call), Circle.Preflop, texas);
        circleEquals(2, makeAct(Optype.Fold), Circle.Preflop, texas);
        circleEquals(3, makeAct(Optype.Fold), Circle.Preflop, texas);

        circleEquals(4, Action.raise(2), Circle.Flop, texas);
        circleEquals(5, makeAct(Optype.Fold), Circle.Flop, texas);
        circleEquals(1, makeAct(Optype.Call), Circle.Flop, texas);

        circleEquals(4, makeAct(Optype.Check), Circle.Turn, texas);
        circleEquals(1, makeAct(Optype.Check), Circle.Turn, texas);

        assertEquals(Circle.River, texas.circle());


        ////////////////////////////////////////////////////////////////////////
        texas = Texas.builder(5).build();
        texas.start();

        circleEquals(4, makeAct(Optype.Call), Circle.Preflop, texas);
        circleEquals(5, makeAct(Optype.Call), Circle.Preflop, texas);
        circleEquals(1, makeAct(Optype.Call), Circle.Preflop, texas);
        circleEquals(2, makeAct(Optype.Call), Circle.Preflop, texas);
        circleEquals(3, makeAct(Optype.Check), Circle.Preflop, texas);


        circleEquals(2, makeAct(Optype.Fold), Circle.Flop, texas);
        circleEquals(3, Action.raise(2), Circle.Flop, texas);
        circleEquals(4, makeAct(Optype.Fold), Circle.Flop, texas);
        circleEquals(5, makeAct(Optype.Fold), Circle.Flop, texas);
        circleEquals(1, makeAct(Optype.Call), Circle.Flop, texas);

        assertEquals(Circle.Turn, texas.circle());

        ////////////////////////////////////////////////////////////////////////
        texas = Texas.builder(5).build();
        texas.start();

        circleEquals(4, Action.raise(4), Circle.Preflop, texas);
        circleEquals(5, makeAct(Optype.Allin), Circle.Preflop, texas);
        circleEquals(1, makeAct(Optype.Allin), Circle.Preflop, texas);
        circleEquals(2, makeAct(Optype.Fold), Circle.Preflop, texas);
        circleEquals(3, makeAct(Optype.Allin), Circle.Preflop, texas);
        circleEquals(4, makeAct(Optype.Allin), Circle.Preflop, texas);
        assertTrue(texas.isOver());
    }

    @Test
    public void testAction() throws Exception {
        var texas = Texas.builder(2)
                .initChips(200)
                .build();
        texas.start();
        assertEquals(Move.NextOp, texas.action(Action.raise(198)));

        // 第一圈都 call, 大盲多一次押注
        texas = Texas.builder(3).build();
        assertEquals(Move.NextOp, texas.start());
        assertEquals(Move.NextOp, texas.action(makeAct(Optype.Call)));
        assertEquals(Move.NextOp, texas.action(makeAct(Optype.Call)));
        assertEquals(Move.CircleEnd, texas.action(makeAct(Optype.Check)));

        // 小盲为0
        texas = Texas.builder()
                .smallBlind(0)
                .build();
        assertEquals(Move.NextOp, texas.start());
        assertEquals(Move.NextOp, texas.action(makeAct(Optype.Check)));
        assertEquals(Move.CircleEnd, texas.action(makeAct(Optype.Check)));

        // TODO 两倍前注
//        var reg = new HashMap<Regulation, Integer>();
//        reg.put(Regulation.DoubleAnte, 1);
//        texas = Texas.builder()
//                .smallBlind(0)
//                .regulations(reg)
//                .build();
//        assertEquals(Move.NextOp, texas.start());
//        assertEquals(Move.NextOp, texas.action(makeAct(Optype.Call)));
//        assertEquals(Move.CircleEnd, texas.action(makeAct(Optype.Check)));

        // 复现
        Ring<Player> ring = Ring.create(3);
        ring.setValue(new Player(1, 100));
        ring.getNext().setValue(new Player(2, 200));
        ring.getPrev().setValue(new Player(3, 200));
        var config = Texas.builder().ring(ring);
        texas = config.build();
        assertEquals(Move.NextOp, texas.start());
        circleEquals(1, makeAct(Optype.Allin), Move.NextOp, texas);
        circleEquals(2, makeAct(Optype.Call), Move.NextOp, texas);
        circleEquals(3, makeAct(Optype.Call), Move.CircleEnd, texas);
        circleEquals(2, makeAct(Optype.Fold), Move.Showdown, texas);
    }

    @Test
    public void testOpPlayer() throws Exception {
        var texas = Texas.builder(5).build();
        texas.start();

        // Preflop
        circleEquals(4, makeAct(Optype.Call), Move.NextOp, texas);
        circleEquals(5, makeAct(Optype.Call), Move.NextOp, texas);
        circleEquals(1, makeAct(Optype.Call), Move.NextOp, texas);
        circleEquals(2, makeAct(Optype.Call), Move.NextOp, texas);
        // 第一圈，大盲这种情况下多一次押注
        circleEquals(3, makeAct(Optype.Check), Move.CircleEnd, texas);

        // Flop
        circleEquals(2, makeAct(Optype.Fold), Move.NextOp, texas);
        circleEquals(3, makeAct(Optype.Fold), Move.NextOp, texas);
        circleEquals(4, Action.raise(2), Move.NextOp, texas);
        circleEquals(5, makeAct(Optype.Call), Move.NextOp, texas);
        circleEquals(1, makeAct(Optype.Fold), Move.CircleEnd, texas);

        // Turn
        circleEquals(4, Action.raise(2), Move.NextOp, texas);
        circleEquals(5, makeAct(Optype.Call), Move.CircleEnd, texas);

        // River
        circleEquals(4, Action.raise(2), Move.NextOp, texas);
        circleEquals(5, makeAct(Optype.Call), Move.Showdown, texas);

        //////////////////////////////////////////////////////////////////
        Ring<Player> ring = Ring.create(5);
        for (int i = 1; i <= 4; i++) {
            ring.setValue(new Player(i, 100));
            ring = ring.getNext();
        }
        ring.setValue(new Player(5, 50));
        texas = Texas.builder()
                .ring(ring)
                .build();
        texas.start();

        circleEquals(4, Action.raise(4), Move.NextOp, texas);
        circleEquals(5, makeAct(Optype.Allin), Move.NextOp, texas);
        circleEquals(1, makeAct(Optype.Call), Move.NextOp, texas);
        circleEquals(2, makeAct(Optype.Fold), Move.NextOp, texas);
        circleEquals(3, makeAct(Optype.Call), Move.NextOp, texas);
        circleEquals(4, makeAct(Optype.Call), Move.CircleEnd, texas);

        circleEquals(3, makeAct(Optype.Check), Move.NextOp, texas);
        circleEquals(4, makeAct(Optype.Check), Move.NextOp, texas);
        circleEquals(1, Action.raise(4), Move.NextOp, texas);
        circleEquals(3, makeAct(Optype.Fold), Move.NextOp, texas);
        circleEquals(4, makeAct(Optype.Call), Move.CircleEnd, texas);

        circleEquals(4, Action.raise(5), Move.NextOp, texas);
        circleEquals(1, makeAct(Optype.Call), Move.CircleEnd, texas);


        ////////////////////////////////////////////////////////////////////////
        texas = Texas.builder().build();
        texas.start();

        circleEquals(1, makeAct(Optype.Call), Move.NextOp, texas);
        circleEquals(2, makeAct(Optype.Check), Move.CircleEnd, texas);

        assertEquals(Circle.Flop, texas.circle());
        assertEquals(2, texas.opPlayer().getId());

        ////////////////////////////////////////////////////////////////////////
        //  短牌：在“两倍前注”下, 开局后，所有玩家都call，最后应该到庄家还有一次option
        var regulations = new HashMap<Regulation, Integer>();
        regulations.put(Regulation.DoubleAnte, 1);
        texas = Texas.builder()
                .smallBlind(0)
                .ante(1)
                .regulations(regulations)
                .build();
        texas.start();
        circleEquals(2, makeAct(Optype.Call), Move.NextOp, texas);
        circleEquals(1, makeAct(Optype.Check), Move.CircleEnd, texas);

        ////////////////////////////////////////////////////////////////////////
        //  短牌：在“smallBlind == 0”下, 开局后，所有玩家都check，最后应该到庄家还有一次option
        texas = Texas.builder()
                .smallBlind(0)
                .ante(1)
                .build();
        texas.start();
        circleEquals(2, makeAct(Optype.Check), Move.NextOp, texas);
        circleEquals(1, makeAct(Optype.Check), Move.CircleEnd, texas);
    }

    @Test
    public void testAuth() throws Exception {
        var texas = Texas.builder(5).build();
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
//        equals(texas.auth(), Optype.fold, Optype.Allin, Optype.Call);
        circleEquals(4, makeAct(Optype.Call), Move.NextOp, texas);

        // chips: 1
        player = texas.opPlayer();
        player.changeChips(1 - player.getChips());
        equals(texas.auth(), Optype.Fold, Optype.Allin);
        circleEquals(5, makeAct(Optype.Allin), Move.NextOp, texas);
    }

    @Test
    public void testAnte() throws Exception {
        var texas = Texas.builder(3)
                .ante(1)
                .build();
        assertEquals(1, texas.ante());

        texas.start();
        assertEquals(3, texas.anteSum());
        assertEquals(6, texas.sumPot());
        assertEquals(99, texas.dealer().getChips());
        assertEquals(98, texas.sbPlayer().getChips());
        assertEquals(97, texas.bbPlayer().getChips());

        texas = Texas.builder(4)
                .ante(2)
                .build();
        assertEquals(2, texas.ante());
        texas.start();
        assertEquals(11, texas.sumPot());

        // 庄家自动压一个前注
        texas = Texas.builder()
                .smallBlind(1)
                .ante(20)
                .players(new Player(1, 10), new Player(1, 50))
                .build();
        texas.start();
        assertTrue(texas.isOver());
        // TODO
//        local result = round:MakeResult()
//        assert(result.playersMap[1].profit == 10)
    }

    /**
     * Method: start()
     */
    @Test
    public void testStart() throws Exception {
        Ring<Player> ring = Ring.create(2);
        ring.setValue(new Player(1, 500));
        ring.getNext().setValue(new Player(2, 50));
        var texas = Texas.builder()
                .ring(ring)
                .smallBlind(50)
                .build();
        var move = texas.start();
        assertEquals(1, texas.dealer().getId());
        assertEquals(1, texas.sbPlayer().getId());
        assertEquals(2, texas.bbPlayer().getId());
        assertEquals(50, texas.smallBlind());
        assertEquals(Move.Showdown, move);
    }

    private void equals(Map<Optype, Integer> auth, Optype... ops) {
        assertEquals(auth.size(), ops.length);
        for (var v : ops) {
            assertTrue(auth.containsKey(v));
        }
    }

    private void circleEquals(Integer opId, Action act, Move move, Texas texas) throws Exception {
        assertEquals(opId, texas.opPlayer().getId());
        assertEquals(move, texas.action(act));
    }

    private void circleEquals(Integer opId, Action act, Circle circle, Texas texas) throws Exception {
        assertEquals(opId.toString(), texas.opPlayer().getId() + "");
        assertEquals(circle, texas.circle());
        texas.action(act);
    }

    private Action makeAct(Optype optype) {
        return Action.of(optype);
    }
}
