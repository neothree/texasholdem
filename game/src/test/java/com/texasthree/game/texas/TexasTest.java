package com.texasthree.game.texas;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.texasthree.game.AllCard;
import com.texasthree.game.Deck;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

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
        equalsCircle(4, Optype.Call, Circle.PREFLOP, texas);
        equalsCircle(5, Optype.Call, Circle.PREFLOP, texas);
        equalsCircle(1, Optype.Call, Circle.PREFLOP, texas);
        equalsCircle(2, Optype.Fold, Circle.PREFLOP, texas);
        equalsCircle(3, Optype.Fold, Circle.PREFLOP, texas);

        equalsCircle(4, Optype.Raise, 2, Circle.FLOP, texas);
        equalsCircle(5, Optype.Fold, Circle.FLOP, texas);
        equalsCircle(1, Optype.Call, Circle.FLOP, texas);

        equalsCircle(4, Optype.Check, Circle.TURN, texas);
        equalsCircle(1, Optype.Check, Circle.TURN, texas);

        assertEquals(Circle.RIVER, texas.circle());


        ////////////////////////////////////////////////////////////////////////
        texas = Texas.builder(5).build();
        texas.start();

        equalsCircle(4, Optype.Call, Circle.PREFLOP, texas);
        equalsCircle(5, Optype.Call, Circle.PREFLOP, texas);
        equalsCircle(1, Optype.Call, Circle.PREFLOP, texas);
        equalsCircle(2, Optype.Call, Circle.PREFLOP, texas);
        equalsCircle(3, Optype.Check, Circle.PREFLOP, texas);


        equalsCircle(2, Optype.Fold, Circle.FLOP, texas);
        equalsCircle(3, Optype.Raise, 2, Circle.FLOP, texas);
        equalsCircle(4, Optype.Fold, Circle.FLOP, texas);
        equalsCircle(5, Optype.Fold, Circle.FLOP, texas);
        equalsCircle(1, Optype.Call, Circle.FLOP, texas);

        assertEquals(Circle.TURN, texas.circle());

        ////////////////////////////////////////////////////////////////////////
        texas = Texas.builder(5).build();
        texas.start();

        equalsCircle(4, Optype.Raise, 4, Circle.PREFLOP, texas);
        equalsCircle(5, Optype.Allin, Circle.PREFLOP, texas);
        equalsCircle(1, Optype.Allin, Circle.PREFLOP, texas);
        equalsCircle(2, Optype.Fold, Circle.PREFLOP, texas);
        equalsCircle(3, Optype.Allin, Circle.PREFLOP, texas);
        equalsCircle(4, Optype.Allin, Circle.PREFLOP, texas);
        assertTrue(texas.isOver());
    }

    @Test
    public void testAction() throws Exception {
        var texas = Texas.builder(2)
                .initChips(200)
                .build();
        texas.start();
        assertEquals(Texas.STATE_NEXT_OP, texas.action(Optype.Raise, 198));

        // 第一圈都 call, 大盲多一次押注
        texas = Texas.builder(3).build();
        assertEquals(Texas.STATE_NEXT_OP, texas.start());
        assertEquals(Texas.STATE_NEXT_OP, texas.action(Optype.Call));
        assertEquals(Texas.STATE_NEXT_OP, texas.action(Optype.Call));
        assertEquals(Texas.STATE_CIRCLE_END, texas.action(Optype.Check));

        // 小盲为0
        texas = Texas.builder()
                .smallBlind(0)
                .build();
        assertEquals(Texas.STATE_NEXT_OP, texas.start());
        assertEquals(Texas.STATE_NEXT_OP, texas.action(Optype.Check));
        assertEquals(Texas.STATE_CIRCLE_END, texas.action(Optype.Check));

        // TODO 两倍前注
//        var reg = new HashMap<Regulation, Integer>();
//        reg.put(Regulation.DoubleAnte, 1);
//        texas = Texas.builder()
//                .smallBlind(0)
//                .regulations(reg)
//                .build();
//        assertEquals(Texas.STATE_NEXT_OP, texas.start());
//        assertEquals(Texas.STATE_NEXT_OP, texas.action(Optype.Call));
//        assertEquals(Texas.STATE_CIRCLE_END, texas.action(Optype.Check));

        // 复现
        texas = Texas.builder()
                .players(new Player(1, 100), new Player(2, 200), new Player(3, 200))
                .build();
        assertEquals(Texas.STATE_NEXT_OP, texas.start());
        equalsAction(1, Optype.Allin, Texas.STATE_NEXT_OP, texas);
        equalsAction(2, Optype.Call, Texas.STATE_NEXT_OP, texas);
        equalsAction(3, Optype.Call, Texas.STATE_CIRCLE_END, texas);
        equalsAction(2, Optype.Fold, Texas.STATE_SHOWDOWN, texas);
    }

    @Test
    public void testOperator() throws Exception {
        var texas = Texas.builder(5).build();
        texas.start();

        // PREFLOP
        equalsAction(4, Optype.Call, Texas.STATE_NEXT_OP, texas);
        equalsAction(5, Optype.Call, Texas.STATE_NEXT_OP, texas);
        equalsAction(1, Optype.Call, Texas.STATE_NEXT_OP, texas);
        equalsAction(2, Optype.Call, Texas.STATE_NEXT_OP, texas);
        // 第一圈，大盲这种情况下多一次押注
        equalsAction(3, Optype.Check, Texas.STATE_CIRCLE_END, texas);

        // FLOP
        equalsAction(2, Optype.Fold, Texas.STATE_NEXT_OP, texas);
        equalsAction(3, Optype.Fold, Texas.STATE_NEXT_OP, texas);
        equalsAction(4, Optype.Raise, 2, Texas.STATE_NEXT_OP, texas);
        equalsAction(5, Optype.Call, Texas.STATE_NEXT_OP, texas);
        equalsAction(1, Optype.Fold, Texas.STATE_CIRCLE_END, texas);

        // TURN
        equalsAction(4, Optype.Raise, 2, Texas.STATE_NEXT_OP, texas);
        equalsAction(5, Optype.Call, Texas.STATE_CIRCLE_END, texas);

        // RIVER
        equalsAction(4, Optype.Raise, 2, Texas.STATE_NEXT_OP, texas);
        equalsAction(5, Optype.Call, Texas.STATE_SHOWDOWN, texas);

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

        equalsAction(4, Optype.Raise, 4, Texas.STATE_NEXT_OP, texas);
        equalsAction(5, Optype.Allin, Texas.STATE_NEXT_OP, texas);
        equalsAction(1, Optype.Call, Texas.STATE_NEXT_OP, texas);
        equalsAction(2, Optype.Fold, Texas.STATE_NEXT_OP, texas);
        equalsAction(3, Optype.Call, Texas.STATE_NEXT_OP, texas);
        equalsAction(4, Optype.Call, Texas.STATE_CIRCLE_END, texas);

        equalsAction(3, Optype.Check, Texas.STATE_NEXT_OP, texas);
        equalsAction(4, Optype.Check, Texas.STATE_NEXT_OP, texas);
        equalsAction(1, Optype.Raise, 4, Texas.STATE_NEXT_OP, texas);
        equalsAction(3, Optype.Fold, Texas.STATE_NEXT_OP, texas);
        equalsAction(4, Optype.Call, Texas.STATE_CIRCLE_END, texas);

        equalsAction(4, Optype.Raise, 5, Texas.STATE_NEXT_OP, texas);
        equalsAction(1, Optype.Call, Texas.STATE_CIRCLE_END, texas);


        ////////////////////////////////////////////////////////////////////////
        texas = Texas.builder().build();
        texas.start();

        equalsAction(1, Optype.Call, Texas.STATE_NEXT_OP, texas);
        equalsAction(2, Optype.Check, Texas.STATE_CIRCLE_END, texas);

        assertEquals(Circle.FLOP, texas.circle());
        assertEquals(2, texas.operator().getId());

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
        equalsAction(2, Optype.Call, Texas.STATE_NEXT_OP, texas);
        equalsAction(1, Optype.Check, Texas.STATE_CIRCLE_END, texas);

        ////////////////////////////////////////////////////////////////////////
        //  短牌：在“smallBlind == 0”下, 开局后，所有玩家都check，最后应该到庄家还有一次option
        texas = Texas.builder()
                .smallBlind(0)
                .ante(1)
                .build();
        texas.start();
        equalsAction(2, Optype.Check, Texas.STATE_NEXT_OP, texas);
        equalsAction(1, Optype.Check, Texas.STATE_CIRCLE_END, texas);
    }

    @Test
    public void testStraddle() throws Exception {
        var texas = Texas.builder(4)
                .straddle()
                .build();
        assertEquals(Texas.STATE_NEXT_OP, texas.start());
//        assert(game:OpPlayer() == config.playerList[1])
//        assert(config.playerList[4]:Chips() == 196)
        assertEquals(texas.operator().getId(), 1);
        assertEquals(texas.getPlayerById(4).getChips(), 96);

        // TODO
//        local config = {
//                leftCard = {club9, club10, spades1, heart13, club8},
//                playerList = {
//                        Player:Ctor(1, 1, 200),
//                Player:Ctor(2, 2, 200),
//                Player:Ctor(3, 3, 200),
//                Player:Ctor(4, 4, 3),
//        },
//        playing = {straddle = true,}
//    }
//        local game = CreateRoundByConfig(config)
//        local state = game:Start()
//        game:Action({op = Texas.DefAction.Allin})
//        game:Action({op = Texas.DefAction.Allin})
//        game:Action({op = Texas.DefAction.Allin})
//        local result = game:MakeResult()
//        assert(result.playerList[4].profit == -3)
    }

    @Test
    public void testLeave() throws Exception {
        var texas = Texas.builder()
                .build();
        texas.start();
        var state = texas.leave(1);
        assertEquals(Texas.STATE_SHOWDOWN, state);
        assertTrue(texas.isOver());

        //////////////
        texas = Texas.builder()
                .build();
        texas.start();
        state = texas.leave(2);
        assertEquals(Texas.STATE_SHOWDOWN, state);
        assertTrue(texas.isOver());

        //////////////
        texas = Texas.builder(3)
                .build();
        texas.start();
        texas.leave(1);
        assertEquals(2, texas.operator().getId());
        assertFalse(texas.isOver());
        texas.leave(2);
        assertTrue(texas.isOver());

        // 离开玩家自动弃牌
        texas = Texas.builder(4)
                .build();
        texas.start();
        texas.leave(1);
        texas.action(Optype.Fold);
        assertEquals(2, texas.operator().getId());

        //////////////////////////////
        texas = Texas.builder(4).build();
        texas.start();
        texas.action(Optype.Call);
        texas.action(Optype.Call);
        texas.action(Optype.Call);
        texas.leave(2);
        texas.action(Optype.Check);
        assertEquals(Circle.FLOP, texas.circle());
        assertEquals(3, texas.operator().getId());

        //////////////////////////////
        texas = Texas.builder(3).build();
        texas.start();
        texas.leave(3);
        texas.action(Optype.Fold);
        assertTrue(texas.isOver());
    }

    @Test
    public void testSettle() throws Exception {
        var texas = Texas.builder(5).build();
        texas.start();
        texas.action(Optype.Fold);
        texas.action(Optype.Fold);
        texas.action(Optype.Fold);
        texas.action(Optype.Fold);
        var result = texas.settle();
        assertEquals(5, result.size());
        assertEquals(3, result.getPlayer(3).pot.values().stream().reduce(Integer::sum).get());


        texas = Texas.builder()
                .players(createPlayers(2, 100))
                .board(club9, club10, spadesA, heartK, club8)
                .build();
        texas.start();
        texas.action(Optype.Call);
        texas.action(Optype.Raise, 2);
        texas.action(Optype.Call);

        texas.action(Optype.Check);
        texas.action(Optype.Check);

        texas.action(Optype.Check);
        texas.action(Optype.Check);

        texas.action(Optype.Check);
        texas.action(Optype.Check);
        result = texas.settle();
        assertEquals(2, result.size());
        assertEquals(8, result.getPlayer(1).pot.values().stream().reduce(Integer::sum).get());

        // 所有人都allin的话, 结算看5张底牌
        texas = Texas.builder()
                .players(createPlayers(2, 10))
                .board(club9, club10, spadesA, heartK, club8)
                .build();
        texas.start();
        texas.action(Optype.Allin);
        texas.action(Optype.Allin);
        result = texas.settle();
        assertEquals(20, result.getPlayer(1).pot.get(0));

        // 有人离开
        texas = SimpleTexas.builder()
                .players(createPlayersByChips(100, 90, 80, 70))
                .build();
        texas.start();
        texas.action(Optype.Allin);
        texas.action(Optype.Allin);
        texas.action(Optype.Allin);
        texas.leave(1);
        texas.leave(2);
        texas.action(Optype.Allin);
        assertTrue(texas.isOver());
        result = texas.settle();
        assertEquals(280, result.getPlayer(4).pot.get(0));
        assertEquals(30, result.getPlayer(3).pot.get(1));
        assertEquals(20, result.getPlayer(4).pot.get(2));
        assertEquals(10, result.getPlayer(4).pot.get(3));

        /////////////
        texas = SimpleTexas.builder(3).build();
        texas.start();

        texas.leave(2);
        texas.leave(3);
        assertTrue(texas.isOver());
        result = texas.settle();
        assertEquals(3, result.getPlayer(1).pot.get(0));

        // TODO ------------- 短牌 --------------------------------
//        local playerList = {
//                Player:Ctor(1, 1, 20),
//                Player:Ctor(2, 2, 20),
//    }
//        config = {
//                playerCard = {{c.diamond1, c.diamond2}, {c.club1, c.heart1}},
//                leftCard = {c.diamond4, c.diamond7, c.diamond8, c.heart7, c.spades1},
//                smallBlind = 0,
//                ante = 1,
//                playerList = playerList,
//                playing = {cardMinPoint = 6},
//        }
//        game = CreateRoundByConfig(config)
//
//        game:Start()
//        game:Action({op = Texas.DefAction.Check})
//        game:Action({op = Texas.DefAction.Check})
//        game:Action({op = Texas.DefAction.Check})
//
//        game:Action({op = Texas.DefAction.Check})
//        game:Action({op = Texas.DefAction.Check})
//
//        game:Action({op = Texas.DefAction.Check})
//        game:Action({op = Texas.DefAction.Check})
//
//        game:Action({op = Texas.DefAction.Check})
//        game:Action({op = Texas.DefAction.Check})
//
//        assert(game:IsOver())
//        history = game:MakeResult()
//        assert(history.playerList[1].pot[1] == 2)

        // 结算后最后一个单人单池筹码返回
        texas = SimpleTexas.builder()
                .players(createPlayersByChips(10, 20))
                .build();
        texas.start();
        texas.action(Optype.Allin);
        texas.action(Optype.Allin);

        result = texas.settle();
        var info = result.getPlayer(1);
        assertEquals(20, info.pot.get(0));
        assertEquals(10, info.getProfit());
        assertFalse(info.pot.containsKey(1));
        info = result.getPlayer(2);
        assertFalse(info.pot.containsKey(1));
        assertEquals(-10, info.getProfit());

        // 最后只有一个人的话不分池
        texas = SimpleTexas.builder()
                .players(createPlayersByChips(10, 20))
                .build();
        texas.start();
        texas.action(Optype.Fold);

        result = texas.settle();
        info = result.getPlayer(1);
        assertFalse(info.pot.containsKey(1));
        assertFalse(info.pot.containsKey(2));
        assertEquals(-1, info.getProfit());
        info = result.getPlayer(2);
        assertEquals(3, info.pot.get(0));
        assertFalse(info.pot.containsKey(2));
        assertEquals(1, info.getProfit());

//        TODO -- 四张手牌
//        config = {
//                playerList = {
//                        Player:Ctor(1, 1, 20),
//                Player:Ctor(2, 2, 20),
//        },
//        playerCard = {
//                {c.club10, c.heart8, c.diamond1, c.heart1},
//                {c.club1, c.heart10, c.spades11, c.spades13}},
//                leftCard = {c.diamond4, c.diamond7, c.spades8, c.heart7, c.spades1},
//    }
//        game = CreateRoundByConfig(config)
//        game:Start()
//        game:Action({op = Texas.DefAction.Allin})
//        game:Action({op = Texas.DefAction.Allin})
//        history = game:MakeResult()
//        assert(history.playerList[1].pot[1] == 40)

        // 10 A
        // 10 20 B
        // 10 20 10 C
        // B 玩家弃牌，A 玩家赢
        texas = SimpleTexas.builder()
                .players(createPlayersByChips(10, 100, 40))
                .build();
        texas.start();
        texas.action(Optype.Allin);
        texas.action(Optype.Call);
        texas.action(Optype.Call);
        texas.action(Optype.Raise, 20);
        texas.action(Optype.Allin);
        texas.action(Optype.Fold);

        result = texas.settle();
        assertEquals(20, result.getPlayer(1).getProfit());
        assertEquals(-30, result.getPlayer(2).getProfit());
        assertEquals(10, result.getPlayer(3).getProfit());
        assertEquals(10, result.getPlayer(3).refund);

        // 10 B
        // 10 20 C
        // 10 20 10 A
        // B 玩家弃牌，C 玩家赢
        texas = SimpleTexas.builder()
                .players(createPlayersByChips(40, 10, 40))
                .build();
        texas.start();
        texas.action(Optype.Call);
        texas.action(Optype.Allin);
        texas.action(Optype.Call);
        texas.action(Optype.Call);

        texas.action(Optype.Raise, 20);
        texas.action(Optype.Allin);
        texas.action(Optype.Fold);

        result = texas.settle();
        assertEquals(40, result.getPlayer(1).getProfit());
        assertEquals(-10, result.getPlayer(2).getProfit());
        assertEquals(-30, result.getPlayer(3).getProfit());
        assertEquals(10, result.getPlayer(1).refund);
    }

    private static class SimpleTexas {
        private Texas.Builder builder = Texas.builder();

        private static SimpleTexas builder() {
            return new SimpleTexas();
        }

        private static SimpleTexas builder(int num) {
            var ret = new SimpleTexas();
            ret.builder.playerNum(num);
            return ret;
        }

        SimpleTexas players(Player... players) {
            this.builder.players(players);
            return this;
        }

        SimpleTexas players(List<Player> players) {
            this.builder.players(players);
            return this;
        }

        SimpleTexas board(Card... board) {
            this.builder.board(board);
            return this;
        }

        Texas build() {
            if (this.builder.getCommunityCards() == null) {
                this.builder.board(club9, club10, spadesA, heartK, club8);
            }

            return this.builder.build();
        }
    }

    private List<Player> createPlayersByChips(int... initChips) {
        var ret = new ArrayList<Player>();
        for (var i = 0; i < initChips.length; i++) {
            var player = new Player(i + 1, initChips[i]);
            ret.add(player);
        }
        return giveHand(ret);
    }

    private List<Player> createPlayers(int num, int init) {
        var list = new ArrayList<Player>();
        for (var i = 1; i <= num; i++) {
            list.add(new Player(i, init));
        }
        return giveHand(list);
    }

    private List<Player> giveHand(List<Player> players) {
        players.get(0).setHand(new Hand(Arrays.asList(spadesA, heartA)));
        for (var i = 2; i <= players.size(); i++) {
            var p = players.get(i - 1);
            var id = i * 10 + 4;
            var c1 = Deck.getInstance().getCardById(id);
            assertNotNull(c1);
            var c2 = Deck.getInstance().getCardById(id - 1);
            assertNotNull(c2);
            p.setHand(new Hand(Arrays.asList(c1, c2)));
        }
        return players;
    }

    @Test
    public void testAuth() throws Exception {
        var texas = Texas.builder(5).build();
        texas.start();

        equalsAuth(texas, 2, Optype.Fold, Optype.Allin);
        texas.action(Optype.Allin);

        equalsAuth(texas, 3, Optype.Fold, Optype.Allin, Optype.Call);
        texas.action(Optype.Call);

        var player = texas.operator();
        player.changeChips(10 - player.getChips());
        texas.action(Optype.Call);

        equalsAuth(texas, 1, Optype.Fold, Optype.Allin);
        texas.action(Optype.Allin);

        equalsAuth(texas, 3, Optype.Raise, Optype.Fold, Optype.Allin, Optype.Check);
        texas.action(Optype.Check);

        equalsAuth(texas, 2, Optype.Fold, Optype.Allin, Optype.Check);
        texas.action(Optype.Allin);

        player = texas.operator();
        player.changeChips(10 - player.getChips());
        texas.action(Optype.Call);

        ////////////////////////////////////////////////////////////////////////////////
        texas = Texas.builder(5).build();
        texas.start();

        equalsAuth(texas, 10, Optype.Fold, Optype.Call, Optype.Raise, Optype.Allin, Optype.BBlind2, Optype.BBlind3, Optype.BBlind4);
        texas.action(Optype.Raise, 10);

        texas.action(Optype.Fold);

        equalsAuth(texas, 40, Optype.Fold, Optype.Call, Optype.Raise, Optype.Allin, Optype.Pot1_1, Optype.Pot1_2, Optype.Pot2_3);

        ////////////////////////////////////////////////////////////////////////////////
        texas = Texas.builder().build();
        texas.start();
        var auth = texas.authority();
        assertEquals(7, auth.size());
        assertEquals(0, auth.get(Optype.Fold));
        assertEquals(1, auth.get(Optype.Call));
        assertEquals(4, auth.get(Optype.Raise));
        assertEquals(99, auth.get(Optype.Allin));
        assertEquals(5, auth.get(Optype.BBlind2));
        assertEquals(7, auth.get(Optype.BBlind3));
        assertEquals(9, auth.get(Optype.BBlind4));

        texas.action(Optype.Raise, 8);
        auth = texas.authority();
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
        texas.action(Optype.Call);
        texas.action(Optype.Check);
        texas.action(Optype.Raise, 4);
        auth = texas.authority();
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
        texas.action(Optype.Raise, 70);
        texas.action(Optype.Allin);
        texas.action(Optype.Call);
        texas.action(Optype.Allin);
        texas.action(Optype.Fold);
        equalsAuth(texas, null, Optype.Fold, Optype.Allin);

        // 对allin不足的人call后不能再allin
        // 需求先不要
        texas = Texas.builder()
                .players(new Player(1, 40)
                        , new Player(2, 60)
                        , new Player(3, 80))
                .build();
        texas.start();
        texas.action(Optype.Call);
        texas.action(Optype.Call);
        texas.action(Optype.Raise, 32);
        texas.action(Optype.Allin);
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
        texas.action(Optype.Allin);
        equalsAuth(texas, null, Optype.Fold, Optype.Allin);
    }

    private void equalsAuth(Texas texas, Integer chipsLeft, Optype... ops) throws Exception {
        var player = texas.operator();
        if (chipsLeft != null) {
            player.changeChips(chipsLeft - player.getChips());
        }

        var auth = texas.authority();
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
//        local result = game:MakeResult()
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
        var state = texas.start();
        assertEquals(1, texas.dealer().getId());
        assertEquals(1, texas.sbPlayer().getId());
        assertEquals(2, texas.bbPlayer().getId());
        assertEquals(50, texas.smallBlind());
        assertEquals(Texas.STATE_SHOWDOWN, state);
    }

    @Test
    public void testCommunityCards() throws Exception {
        var texas = Texas.builder()
                .board(club9, club10, spadesA, heartK, club8)
                .build();
        texas.start();
        assertTrue(texas.getCommunityCards().isEmpty());

        texas.action(Optype.Call);
        texas.action(Optype.Raise, 2);
        texas.action(Optype.Call);
        equalsCommunityCards(texas, club9, club10, spadesA);

        texas.action(Optype.Check);
        texas.action(Optype.Check);
        equalsCommunityCards(texas, club9, club10, spadesA, heartK);

        texas.action(Optype.Check);
        texas.action(Optype.Check);
        equalsCommunityCards(texas, club9, club10, spadesA, heartK, club8);

        texas.action(Optype.Check);
        texas.action(Optype.Check);
        equalsCommunityCards(texas, club9, club10, spadesA, heartK, club8);

        texas = Texas.builder()
                .board(club9, club10, spadesA, heartK, club8)
                .players(new Player(1, 100), new Player(2, 200), new Player(3, 300))
                .build();
        texas.start();
        texas.action(Optype.Allin);
        texas.action(Optype.Call);
        texas.action(Optype.Fold);
        equalsCommunityCards(texas, club9, club10, spadesA, heartK, club8);
    }

    @Test
    public void testIsOver() throws Exception {
        var texas = Texas.builder().build();
        texas.start();

        assertFalse(texas.isOver());
        texas.action(Optype.Call);
        assertFalse(texas.isOver());
        texas.action(Optype.Fold);
        assertTrue(texas.isOver());

        ////////////////////////////////////
        texas = Texas.builder().build();
        texas.start();
        assertFalse(texas.isOver());

        texas.action(Optype.Call);
        texas.action(Optype.Raise, 2);
        texas.action(Optype.Call);
        assertFalse(texas.isOver());

        texas.action(Optype.Check);
        texas.action(Optype.Check);
        assertFalse(texas.isOver());

        texas.action(Optype.Check);
        texas.action(Optype.Check);
        assertFalse(texas.isOver());

        texas.action(Optype.Check);
        texas.action(Optype.Check);
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

        texas.action(Optype.Call);
        texas.action(Optype.Raise, 710);
        texas.action(Optype.Allin);
        assertTrue(texas.isOver());
    }

    private void equalsCommunityCards(Texas texas, Card... cards) throws Exception {
        var board = texas.getCommunityCards();
        assertEquals(board.size(), cards.length);
        for (var i = 0; i < cards.length; i++) {
            assertEquals(board.get(i), cards[i]);
        }
    }

    private void equalsAction(Integer opId, Optype op, String state, Texas texas) throws Exception {
        assertEquals(opId, texas.operator().getId());
        assertEquals(state, texas.action(op));
    }

    private void equalsAction(Integer opId, Optype op, int chipsAdd, String state, Texas texas) throws Exception {
        assertEquals(opId, texas.operator().getId());
        assertEquals(state, texas.action(op, chipsAdd));
    }


    private void equalsCircle(Integer opId, Optype op, String circle, Texas texas) throws Exception {
        equalsCircle(opId, op, 0, circle, texas);
    }

    private void equalsCircle(Integer opId, Optype op, int chipsAdd, String circle, Texas texas) throws Exception {
        assertEquals(opId.toString(), texas.operator().getId() + "");
        assertEquals(circle, texas.circle());
        texas.action(op, chipsAdd);
    }
}
