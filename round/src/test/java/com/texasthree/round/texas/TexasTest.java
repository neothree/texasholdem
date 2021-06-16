package com.texasthree.round.texas;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.texasthree.round.AllCard;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

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
        assertEquals(Texas.NEXT_OP, texas.action(Action.raise(198)));

        // 第一圈都 call, 大盲多一次押注
        texas = Texas.builder(3).build();
        assertEquals(Texas.NEXT_OP, texas.start());
        assertEquals(Texas.NEXT_OP, texas.action(makeAct(Optype.Call)));
        assertEquals(Texas.NEXT_OP, texas.action(makeAct(Optype.Call)));
        assertEquals(Texas.CIRCLE_END, texas.action(makeAct(Optype.Check)));

        // 小盲为0
        texas = Texas.builder()
                .smallBlind(0)
                .build();
        assertEquals(Texas.NEXT_OP, texas.start());
        assertEquals(Texas.NEXT_OP, texas.action(makeAct(Optype.Check)));
        assertEquals(Texas.CIRCLE_END, texas.action(makeAct(Optype.Check)));

        // TODO 两倍前注
//        var reg = new HashMap<Regulation, Integer>();
//        reg.put(Regulation.DoubleAnte, 1);
//        texas = Texas.builder()
//                .smallBlind(0)
//                .regulations(reg)
//                .build();
//        assertEquals(Texas.NEXT_OP, texas.start());
//        assertEquals(Texas.NEXT_OP, texas.action(makeAct(Optype.Call)));
//        assertEquals(Texas.CIRCLE_END, texas.action(makeAct(Optype.Check)));

        // 复现
        texas = Texas.builder()
                .players(new Player(1, 100), new Player(2, 200), new Player(3, 200))
                .build();
        assertEquals(Texas.NEXT_OP, texas.start());
        circleEquals(1, makeAct(Optype.Allin), Texas.NEXT_OP, texas);
        circleEquals(2, makeAct(Optype.Call), Texas.NEXT_OP, texas);
        circleEquals(3, makeAct(Optype.Call), Texas.CIRCLE_END, texas);
        circleEquals(2, makeAct(Optype.Fold), Texas.SHOWDOWN, texas);
    }

    @Test
    public void testOpPlayer() throws Exception {
        var texas = Texas.builder(5).build();
        texas.start();

        // Preflop
        circleEquals(4, makeAct(Optype.Call), Texas.NEXT_OP, texas);
        circleEquals(5, makeAct(Optype.Call), Texas.NEXT_OP, texas);
        circleEquals(1, makeAct(Optype.Call), Texas.NEXT_OP, texas);
        circleEquals(2, makeAct(Optype.Call), Texas.NEXT_OP, texas);
        // 第一圈，大盲这种情况下多一次押注
        circleEquals(3, makeAct(Optype.Check), Texas.CIRCLE_END, texas);

        // Flop
        circleEquals(2, makeAct(Optype.Fold), Texas.NEXT_OP, texas);
        circleEquals(3, makeAct(Optype.Fold), Texas.NEXT_OP, texas);
        circleEquals(4, Action.raise(2), Texas.NEXT_OP, texas);
        circleEquals(5, makeAct(Optype.Call), Texas.NEXT_OP, texas);
        circleEquals(1, makeAct(Optype.Fold), Texas.CIRCLE_END, texas);

        // Turn
        circleEquals(4, Action.raise(2), Texas.NEXT_OP, texas);
        circleEquals(5, makeAct(Optype.Call), Texas.CIRCLE_END, texas);

        // River
        circleEquals(4, Action.raise(2), Texas.NEXT_OP, texas);
        circleEquals(5, makeAct(Optype.Call), Texas.SHOWDOWN, texas);

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

        circleEquals(4, Action.raise(4), Texas.NEXT_OP, texas);
        circleEquals(5, makeAct(Optype.Allin), Texas.NEXT_OP, texas);
        circleEquals(1, makeAct(Optype.Call), Texas.NEXT_OP, texas);
        circleEquals(2, makeAct(Optype.Fold), Texas.NEXT_OP, texas);
        circleEquals(3, makeAct(Optype.Call), Texas.NEXT_OP, texas);
        circleEquals(4, makeAct(Optype.Call), Texas.CIRCLE_END, texas);

        circleEquals(3, makeAct(Optype.Check), Texas.NEXT_OP, texas);
        circleEquals(4, makeAct(Optype.Check), Texas.NEXT_OP, texas);
        circleEquals(1, Action.raise(4), Texas.NEXT_OP, texas);
        circleEquals(3, makeAct(Optype.Fold), Texas.NEXT_OP, texas);
        circleEquals(4, makeAct(Optype.Call), Texas.CIRCLE_END, texas);

        circleEquals(4, Action.raise(5), Texas.NEXT_OP, texas);
        circleEquals(1, makeAct(Optype.Call), Texas.CIRCLE_END, texas);


        ////////////////////////////////////////////////////////////////////////
        texas = Texas.builder().build();
        texas.start();

        circleEquals(1, makeAct(Optype.Call), Texas.NEXT_OP, texas);
        circleEquals(2, makeAct(Optype.Check), Texas.CIRCLE_END, texas);

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
        circleEquals(2, makeAct(Optype.Call), Texas.NEXT_OP, texas);
        circleEquals(1, makeAct(Optype.Check), Texas.CIRCLE_END, texas);

        ////////////////////////////////////////////////////////////////////////
        //  短牌：在“smallBlind == 0”下, 开局后，所有玩家都check，最后应该到庄家还有一次option
        texas = Texas.builder()
                .smallBlind(0)
                .ante(1)
                .build();
        texas.start();
        circleEquals(2, makeAct(Optype.Check), Texas.NEXT_OP, texas);
        circleEquals(1, makeAct(Optype.Check), Texas.CIRCLE_END, texas);
    }

    @Test
    public void testStraddle() throws Exception {
        var texas = Texas.builder(4)
                .straddle()
                .build();
        assertEquals(Texas.NEXT_OP, texas.start());
//        assert(round:OpPlayer() == config.playerList[1])
//        assert(config.playerList[4]:Chips() == 196)
        assertEquals(texas.opPlayer().getId(), 1);
        assertEquals(texas.getPlayerById(4).getChips(), 96);

        // TODO
//        local config = {
//                leftCard = {c.club9, c.club10, c.spades1, c.heart13, c.club8},
//                playerList = {
//                        Player:Ctor(1, 1, 200),
//                Player:Ctor(2, 2, 200),
//                Player:Ctor(3, 3, 200),
//                Player:Ctor(4, 4, 3),
//        },
//        playing = {straddle = true,}
//    }
//        local round = CreateRoundByConfig(config)
//        local move = round:Start()
//        round:Action({op = Texas.DefAction.Allin})
//        round:Action({op = Texas.DefAction.Allin})
//        round:Action({op = Texas.DefAction.Allin})
//        local result = round:MakeResult()
//        assert(result.playerList[4].profit == -3)
    }

    @Test
    public void testLeave() throws Exception {
        var texas = Texas.builder()
                .build();
        texas.start();
        var move = texas.leave(1);
        assertEquals(Texas.SHOWDOWN, move);
        assertTrue(texas.isOver());

        //////////////
        texas = Texas.builder()
                .build();
        texas.start();
        move = texas.leave(2);
        assertEquals(Texas.SHOWDOWN, move);
        assertTrue(texas.isOver());

        //////////////
        texas = Texas.builder(3)
                .build();
        texas.start();
        texas.leave(1);
        assertEquals(2, texas.opPlayer().getId());
        assertFalse(texas.isOver());
        texas.leave(2);
        assertTrue(texas.isOver());

        // 离开玩家自动弃牌
        texas = Texas.builder(4)
                .build();
        texas.start();
        texas.leave(1);
        texas.action(Action.fold());
        assertEquals(2, texas.opPlayer().getId());

        //////////////////////////////
        texas = Texas.builder(4).build();
        texas.start();
        texas.action(Action.call());
        texas.action(Action.call());
        texas.action(Action.call());
        texas.leave(2);
        texas.action(Action.check());
        assertEquals(Circle.Flop, texas.circle());
        assertEquals(3, texas.opPlayer().getId());

        //////////////////////////////
        texas = Texas.builder(3).build();
        texas.start();
        texas.leave(3);
        texas.action(Action.fold());
        assertTrue(texas.isOver());
    }

    @Test
    public void testMakeResult() throws Exception {
        // TODO
    }

    @Test
    public void testAuth() throws Exception {
        var texas = Texas.builder(5).build();
        texas.start();

        equalsAuth(texas, 2, Optype.Fold, Optype.Allin);
        texas.action(Action.of(Optype.Allin));

        equalsAuth(texas, 3, Optype.Fold, Optype.Allin, Optype.Call);
        texas.action(Action.of(Optype.Call));

        var player = texas.opPlayer();
        player.changeChips(10 - player.getChips());
        texas.action(Action.of(Optype.Call));

        equalsAuth(texas, 1, Optype.Fold, Optype.Allin);
        texas.action(Action.of(Optype.Allin));

        equalsAuth(texas, 3, Optype.Raise, Optype.Fold, Optype.Allin, Optype.Check);
        texas.action(Action.of(Optype.Check));

        equalsAuth(texas, 2, Optype.Fold, Optype.Allin, Optype.Check);
        texas.action(Action.of(Optype.Allin));

        player = texas.opPlayer();
        player.changeChips(10 - player.getChips());
        texas.action(Action.of(Optype.Call));

        ////////////////////////////////////////////////////////////////////////////////
        texas = Texas.builder(5).build();
        texas.start();

        equalsAuth(texas, 10, Optype.Fold, Optype.Call, Optype.Raise, Optype.Allin, Optype.BBlind2, Optype.BBlind3, Optype.BBlind4);
        texas.action(Action.raise(10));

        texas.action(Action.of(Optype.Fold));

        equalsAuth(texas, 40, Optype.Fold, Optype.Call, Optype.Raise, Optype.Allin, Optype.Pot1_1, Optype.Pot1_2, Optype.Pot2_3);

        ////////////////////////////////////////////////////////////////////////////////
        texas = Texas.builder().build();
        texas.start();
        var auth = texas.auth();
        assertEquals(7, auth.size());
        assertEquals(0, auth.get(Optype.Fold));
        assertEquals(1, auth.get(Optype.Call));
        assertEquals(4, auth.get(Optype.Raise));
        assertEquals(99, auth.get(Optype.Allin));
        assertEquals(5, auth.get(Optype.BBlind2));
        assertEquals(7, auth.get(Optype.BBlind3));
        assertEquals(9, auth.get(Optype.BBlind4));

        texas.action(Action.raise(8));
        auth = texas.auth();
        assertEquals(7, auth.size());
        assertEquals(0, auth.get(Optype.Fold));
        assertEquals(7, auth.get(Optype.Call));
        assertEquals(16, auth.get(Optype.Raise));
        assertEquals(98, auth.get(Optype.Allin));
        assertEquals(16 + 2, auth.get(Optype.Pot1_2));
        assertEquals(19 + 2, auth.get(Optype.Pot2_3));
        assertEquals(25 + 2, auth.get(Optype.Pot1_1));

        // 最小加注线等于最大加注则只能allin, 没有加注
        texas = Texas.builder()
                .players(new Player(1, 10), new Player(2, 100))
                .build();
        texas.start();
        texas.action(Action.of(Optype.Call));
        texas.action(Action.of(Optype.Check));
        texas.action(Action.raise(4));
        auth = texas.auth();
        assertTrue(auth.containsKey(Optype.Allin));
        assertFalse(auth.containsKey(Optype.Raise));

        // 只有弃牌操作复现
        texas = Texas.builder()
                .players(new Player(1, 100)
                        , new Player(2, 110)
                        , new Player(3, 110)
                        , new Player(4, 100))
                .build();
        texas.start();
        texas.action(Action.raise(70));
        texas.action(Action.of(Optype.Allin));
        texas.action(Action.of(Optype.Call));
        texas.action(Action.of(Optype.Allin));
        texas.action(Action.of(Optype.Fold));
        equalsAuth(texas, null, Optype.Fold, Optype.Allin);

        // 对allin不足的人call后不能再allin
        // 需求先不要
        texas = Texas.builder()
                .players(new Player(1, 40)
                        , new Player(2, 60)
                        , new Player(3, 80))
                .build();
        texas.start();
        texas.action(Action.of(Optype.Call));
        texas.action(Action.of(Optype.Call));
        texas.action(Action.raise(32));
        texas.action(Action.of(Optype.Allin));
        assertTrue(auth.containsKey(Optype.Call));
//        equalsAuth(texas, null, Optype.Fold, Optype.Allin);

        // AllinOrFold
        texas = Texas.builder()
                .players(new Player(1, 200)
                        , new Player(2, 10000))
                .smallBlind(100)
                .regulation(Regulation.AllinOrFold, 1)
                .build();
        texas.start();
        equalsAuth(texas, null, Optype.Fold, Optype.Allin);
        texas.action(Action.of(Optype.Allin));
        equalsAuth(texas, null, Optype.Fold, Optype.Allin);
    }

    private void equalsAuth(Texas texas, Integer chipsLeft, Optype... ops) throws Exception {
        var player = texas.opPlayer();
        if (chipsLeft != null) {
            player.changeChips(chipsLeft - player.getChips());
        }

        var auth = texas.auth();
        if (false) {
            var mp = new ObjectMapper();
            System.out.println(mp.writeValueAsString(auth));
        }
        assertEquals(auth.size(), ops.length);
        for (var v : ops) {
            assertTrue(auth.containsKey(v));
        }
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
        assertEquals(Texas.SHOWDOWN, move);
    }

    @Test
    public void testBoard() throws Exception {
        var texas = Texas.builder()
                .board(club9, club10, spadesA, heartK, club8)
                .build();
        texas.start();
        assertTrue(texas.board().isEmpty());

        texas.action(Action.call());
        texas.action(Action.raise(2));
        texas.action(Action.call());
        equalsBoard(texas, club9, club10, spadesA);

        texas.action(Action.check());
        texas.action(Action.check());
        equalsBoard(texas, club9, club10, spadesA, heartK);

        texas.action(Action.check());
        texas.action(Action.check());
        equalsBoard(texas, club9, club10, spadesA, heartK, club8);

        texas.action(Action.check());
        texas.action(Action.check());
        equalsBoard(texas, club9, club10, spadesA, heartK, club8);

        texas = Texas.builder()
                .board(club9, club10, spadesA, heartK, club8)
                .players(new Player(1, 100), new Player(2, 200), new Player(3, 300))
                .build();
        texas.start();
        texas.action(Action.allin());
        texas.action(Action.call());
        texas.action(Action.fold());
        equalsBoard(texas, club9, club10, spadesA, heartK, club8);
    }

    @Test
    public void testIsOver() throws Exception {
        var texas = Texas.builder().build();
        texas.start();

        assertFalse(texas.isOver());
        texas.action(Action.call());
        assertFalse(texas.isOver());
        texas.action(Action.fold());
        assertTrue(texas.isOver());

        ////////////////////////////////////
        texas = Texas.builder().build();
        texas.start();
        assertFalse(texas.isOver());

        texas.action(Action.call());
        texas.action(Action.raise(2));
        texas.action(Action.call());
        assertFalse(texas.isOver());

        texas.action(Action.check());
        texas.action(Action.check());
        assertFalse(texas.isOver());

        texas.action(Action.check());
        texas.action(Action.check());
        assertFalse(texas.isOver());

        texas.action(Action.check());
        texas.action(Action.check());
        assertTrue(texas.isOver());

        ////////////////////////////////////
        // 大盲直接allin结束
        texas = Texas.builder()
                .players(new Player(1, 600), new Player(2, 200))
                .smallBlind(200)
                .build();
        texas.start();
        assertTrue(texas.isOver());

        // 大盲直接allin不结束
        texas = Texas.builder()
                .players(new Player(1, 600), new Player(2, 300))
                .smallBlind(200)
                .build();
        texas.start();
        assertFalse(texas.isOver());

        // 小盲直接allin结束
        texas = Texas.builder()
                .players(new Player(1, 200), new Player(2, 600))
                .smallBlind(200)
                .build();
        texas.start();
        assertTrue(texas.isOver());

        // 大小盲同时allin结束
        texas = Texas.builder()
                .players(new Player(1, 200), new Player(2, 300))
                .smallBlind(200)
                .build();
        texas.start();
        assertTrue(texas.isOver());

        // 通过Raise将所有的筹码加上, 实际是allin
        // 最低最低加注筹码刚好等于剩余筹码，只能allin
        texas = Texas.builder()
                .players(new Player(1, 750), new Player(2, 2000))
                .smallBlind(20)
                .build();
        texas.start();
        assertFalse(texas.isOver());

        texas.action(Action.call());
        texas.action(Action.raise(710));
        texas.action(Action.allin());
        assertTrue(texas.isOver());
    }

    private void equalsBoard(Texas texas, Card... cards) throws Exception {
        var board = texas.board();
        assertEquals(board.size(), cards.length);
        for (var i = 0; i < cards.length; i++) {
            assertEquals(board.get(i), cards[i]);
        }
    }

    private void circleEquals(Integer opId, Action act, String move, Texas texas) throws Exception {
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
