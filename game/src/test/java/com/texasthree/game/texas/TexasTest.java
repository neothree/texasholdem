package com.texasthree.game.texas;


import com.texasthree.game.AllCard;
import com.texasthree.game.Deck;
import com.texasthree.game.Tester;
import org.junit.jupiter.api.Test;

import java.util.*;

import static com.texasthree.game.texas.Optype.*;
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
        var texas = AssertTexas.builder(3)
                .smallBlind(1)
                .ante(1)
                .build()
                .start()

                .assertAnte(1)
                .assertSmallBlind(1)
                .assertDealer(1)
                .assertSbPlayer(2)
                .assertBbPlayer(3);

        // 52 张牌
        var set = new HashSet<>(texas.getLeftCard());
        for (var v : texas.players()) {
            set.addAll(v.getHand().getHold());
        }
        assertEquals(52, set.size());

        texas = AssertTexas.builder().build().start();
        Tester.assertException(texas::start, IllegalArgumentException.class);

        Tester.assertException(() -> AssertTexas.builder(2)
                .players(new Player(1, 0), new Player(1, 1))
                .smallBlind(1)
                .build(), IllegalArgumentException.class);
    }

    @Test
    public void testCircle() throws Exception {
        // 1 dealer 2 sb 3 bb
        AssertTexas.builder(5).build().start()
                .assertDealer(1)
                .assertSbPlayer(2)
                .assertBbPlayer(3)
                .assertOperatorAndCircle(4, Circle.PREFLOP).action(Call)
                .assertOperatorAndCircle(5, Circle.PREFLOP).action(Call)
                .assertOperatorAndCircle(1, Circle.PREFLOP).action(Call)
                .assertOperatorAndCircle(2, Circle.PREFLOP).action(Fold)
                .assertOperatorAndCircle(3, Circle.PREFLOP).action(Fold)

                .assertOperatorAndCircle(4, Circle.FLOP).action(Raise, 2)
                .assertOperatorAndCircle(5, Circle.FLOP).action(Fold)
                .assertOperatorAndCircle(1, Circle.FLOP).action(Call)

                .assertOperatorAndCircle(4, Circle.TURN).action(Check)
                .assertOperatorAndCircle(1, Circle.TURN).action(Check)

                .assertCircle(Circle.RIVER);


        AssertTexas.builder(5).build().start()
                .assertOperatorAndCircle(4, Circle.PREFLOP).action(Call)
                .assertOperatorAndCircle(5, Circle.PREFLOP).action(Call)
                .assertOperatorAndCircle(1, Circle.PREFLOP).action(Call)
                .assertOperatorAndCircle(2, Circle.PREFLOP).action(Call)
                .assertOperatorAndCircle(3, Circle.PREFLOP).action(Check)

                .assertOperatorAndCircle(2, Circle.FLOP).action(Fold)
                .assertOperatorAndCircle(3, Circle.FLOP).action(Raise, 2)
                .assertOperatorAndCircle(4, Circle.FLOP).action(Fold)
                .assertOperatorAndCircle(5, Circle.FLOP).action(Fold)
                .assertOperatorAndCircle(1, Circle.FLOP).action(Call)

                .assertCircle(Circle.TURN);


        AssertTexas.builder(5).build().start()
                .assertOperatorAndCircle(4, Circle.PREFLOP).action(Raise, 4)
                .assertOperatorAndCircle(5, Circle.PREFLOP).action(Allin)
                .assertOperatorAndCircle(1, Circle.PREFLOP).action(Allin)
                .assertOperatorAndCircle(2, Circle.PREFLOP).action(Fold)
                .assertOperatorAndCircle(3, Circle.PREFLOP).action(Allin)
                .assertOperatorAndCircle(4, Circle.PREFLOP).action(Allin)
                .assertIsOver(true);
    }

    @Test
    public void testAction() throws Exception {
        AssertTexas.builder(2).initChips(200)
                .build()
                .start().assertState(Transfer.NEXT_OP)
                .action(Raise, 198).assertState(Transfer.NEXT_OP);

        // 第一圈都 call, 大盲多一次押注
        AssertTexas.builder(3)
                .build()
                .start().assertState(Transfer.NEXT_OP)
                .action(Call).assertState(Transfer.NEXT_OP)
                .action(Call).assertState(Transfer.NEXT_OP)
                .action(Check).assertState(Transfer.CIRCLE_END);

        // 小盲为0
        AssertTexas.builder()
                .smallBlind(0)
                .build()
                .start().assertState(Transfer.NEXT_OP)
                .action(Check).assertState(Transfer.NEXT_OP)
                .action(Check).assertState(Transfer.CIRCLE_END);

        // TODO 两倍前注
//        var reg = new HashMap<Regulation, Integer>();
//        reg.put(Regulation.DoubleAnte, 1);
//        texas = Texas.builder()
//                .smallBlind(0)
//                .regulations(reg)
//                .build();
//        assertEquals(Transfer.NEXT_OP, texas.start().state());
//        assertEquals(Transfer.NEXT_OP, texas.action(Call));
//        assertEquals(Transfer.CIRCLE_END, texas.action(Check));

        // 复现
        AssertTexas.builder()
                .players(new Player(1, 100), new Player(2, 200), new Player(3, 200))
                .build()
                .start().assertState(Transfer.NEXT_OP)
                .action(Allin).assertState(Transfer.NEXT_OP)
                .action(Call).assertState(Transfer.NEXT_OP)
                .action(Call).assertState(Transfer.CIRCLE_END)
                .action(Fold).assertState(Transfer.SHOWDOWN);

        var room = AssertTexas.builder(2)
                .build()
                .start().assertState(Transfer.NEXT_OP)
                .assertOperator(1).action(Call).assertState(Transfer.NEXT_OP);
        var act = room.getAction(1);
        assertEquals(98, act.chipsLeft);
    }

    @Test
    public void testOperator() throws Exception {
        AssertTexas.builder(5).build().start()
                .assertCircle(Circle.PREFLOP)
                .assertOperator(4).action(Call).assertState(Transfer.NEXT_OP)
                .assertOperator(5).action(Call).assertState(Transfer.NEXT_OP)
                .assertOperator(1).action(Call).assertState(Transfer.NEXT_OP)
                .assertOperator(2).action(Call).assertState(Transfer.NEXT_OP)
                // 第一圈，大盲这种情况下多一次押注
                .assertOperator(3).action(Check).assertState(Transfer.CIRCLE_END)
                .assertCircle(Circle.FLOP)
                .assertOperator(2).action(Fold).assertState(Transfer.NEXT_OP)
                .assertOperator(3).action(Fold).assertState(Transfer.NEXT_OP)
                .assertOperator(4).action(Raise, 2).assertState(Transfer.NEXT_OP)
                .assertOperator(5).action(Call).assertState(Transfer.NEXT_OP)
                .assertOperator(1).action(Fold).assertState(Transfer.CIRCLE_END)
                .assertCircle(Circle.TURN)
                .assertOperator(4).action(Raise, 2).assertState(Transfer.NEXT_OP)
                .assertOperator(5).action(Call).assertState(Transfer.CIRCLE_END)
                .assertCircle(Circle.RIVER)
                .assertOperator(4).action(Raise, 2).assertState(Transfer.NEXT_OP)
                .assertOperator(5).action(Call).assertState(Transfer.SHOWDOWN);


        Ring<Player> ring = Ring.create(5);
        for (int i = 1; i <= 4; i++) {
            ring.setValue(new Player(i, 100));
            ring = ring.getNext();
        }
        ring.setValue(new Player(5, 50));
        AssertTexas.builder().ring(ring).build().start()
                .assertCircle(Circle.PREFLOP)
                .assertOperator(4).action(Raise, 4).assertState(Transfer.NEXT_OP)
                .assertOperator(5).action(Allin).assertState(Transfer.NEXT_OP)
                .assertOperator(1).action(Call).assertState(Transfer.NEXT_OP)
                .assertOperator(2).action(Fold).assertState(Transfer.NEXT_OP)
                .assertOperator(3).action(Call).assertState(Transfer.NEXT_OP)
                .assertOperator(4).action(Call).assertState(Transfer.CIRCLE_END)
                .assertCircle(Circle.FLOP)
                .assertOperator(3).action(Check).assertState(Transfer.NEXT_OP)
                .assertOperator(4).action(Check).assertState(Transfer.NEXT_OP)
                .assertOperator(1).action(Raise, 4).assertState(Transfer.NEXT_OP)
                .assertOperator(3).action(Fold).assertState(Transfer.NEXT_OP)
                .assertOperator(4).action(Call).assertState(Transfer.CIRCLE_END)
                .assertCircle(Circle.TURN)
                .assertOperator(4).action(Raise, 5).assertState(Transfer.NEXT_OP)
                .assertOperator(1).action(Call).assertState(Transfer.CIRCLE_END);


        AssertTexas.builder().build().start()
                .assertCircle(Circle.PREFLOP)
                .assertOperator(1).action(Call).assertState(Transfer.NEXT_OP)
                .assertOperator(2).action(Check).assertState(Transfer.CIRCLE_END)
                .assertCircle(Circle.FLOP)
                .assertOperator(2);


        //  短牌：在“两倍前注”下, 开局后，所有玩家都call，最后应该到庄家还有一次option
        var regulations = new HashMap<Regulation, Integer>();
        regulations.put(Regulation.DoubleAnte, 1);
        AssertTexas.builder()
                .smallBlind(0)
                .ante(1)
                .regulations(regulations)
                .build().start()
                .assertCircle(Circle.PREFLOP)
                .assertOperator(2).action(Call).assertState(Transfer.NEXT_OP)
                .assertOperator(1).action(Check).assertState(Transfer.CIRCLE_END);


        ////////////////////////////////////////////////////////////////////////
        //  短牌：在“smallBlind == 0”下, 开局后，所有玩家都check，最后应该到庄家还有一次option
        AssertTexas.builder()
                .smallBlind(0)
                .ante(1)
                .build().start()
                .assertCircle(Circle.PREFLOP)
                .assertOperator(2).action(Check).assertState(Transfer.NEXT_OP)
                .assertOperator(1).action(Check).assertState(Transfer.CIRCLE_END);
    }

    //    @Test
    public void testStraddle() throws Exception {
//        var texas = Texas.builder(4)
//                .straddle()
//                .build();
//        assertEquals(Transfer.NEXT_OP, texas.start().state());
//        assert(game:OpPlayer() == config.playerList[1])
//        assert(config.playerList[4]:Chips() == 196)
//        assertEquals(texas.operator().getId(), 1);
//        assertEquals(texas.getPlayerById(4).getChips(), 96);

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
        AssertTexas.builder().build().start()
                .assertIsOver(false)
                .leave(1).assertState(Transfer.SHOWDOWN)
                .assertIsOver(true);

        AssertTexas.builder()
                .build().start()
                .assertIsOver(false)
                .leave(2).assertState(Transfer.SHOWDOWN)
                .assertIsOver(true);

        AssertTexas.builder(3)
                .build().start()
                .leave(1).assertIsOver(false)
                .leave(2).assertIsOver(true);

        AssertTexas.builder(4)
                .build().start()
                .leave(1)
                .action(Fold)
                .assertOperator(2);

        AssertTexas.builder(4).build().start()
                .action(Call)
                .action(Call)
                .action(Call)
                .leave(2)
                .action(Check)
                .assertCircle(Circle.FLOP)
                .assertOperator(3);

        AssertTexas.builder(3).build().start()
                .leave(3)
                .action(Fold)
                .assertIsOver(true);
    }

    @Test
    public void testGetPots() throws Exception {
        AssertTexas
                .builder(5)
                .build()
                .start()
                .assertPots();

    }

    @Test
    public void testSettle() throws Exception {
        var result = AssertTexas
                .builder(5)
                .build()
                .start()
                .action(Fold)
                .action(Fold)
                .action(Fold)
                .action(Fold)
                .settle();
        assertEquals(5, result.size());
        assertEquals(3, result.getPlayer(3).pot.values().stream().reduce(Integer::sum).get());


        result = AssertTexas.builder()
                .players(createPlayers(2, 100))
                .communityCards(club9, club10, spadesA, heartK, club8)
                .build()
                .start()
                .action(Call)
                .action(Raise, 2)
                .action(Call)

                .action(Check)
                .action(Check)

                .action(Check)
                .action(Check)

                .action(Check)
                .action(Check)
                .settle();

        assertEquals(2, result.size());
        assertEquals(8, result.getPlayer(1).pot.values().stream().reduce(Integer::sum).get());

        // 所有人都allin的话, 结算看5张底牌
        result = AssertTexas.builder()
                .players(createPlayers(2, 10))
                .communityCards(club9, club10, spadesA, heartK, club8)
                .build()
                .start()
                .action(Allin)
                .action(Allin)
                .settle();
        assertEquals(20, result.getPlayer(1).pot.get(0));

        // 有人离开
        result = AssertTexas.builder()
                .players(createPlayersByChips(100, 90, 80, 70))
                .communityCards(club9, club10, spadesA, heartK, club8)
                .build()
                .start()
                .action(Allin)
                .action(Allin)
                .action(Allin)
                .leave(1)
                .leave(2)
                .action(Allin)
                .assertIsOver(true).settle();

        assertEquals(280, result.getPlayer(4).pot.get(0));
        assertEquals(30, result.getPlayer(3).pot.get(1));
        assertEquals(20, result.getPlayer(4).pot.get(2));
        assertEquals(10, result.getPlayer(4).pot.get(3));

        /////////////
        result = AssertTexas.builder(3)
                .communityCards(club9, club10, spadesA, heartK, club8)
                .build()
                .start()
                .leave(2)
                .leave(3)
                .assertIsOver(true)
                .settle();
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
        result = AssertTexas.builder()
                .players(createPlayersByChips(10, 20))
                .communityCards(club9, club10, spadesA, heartK, club8)
                .build()
                .start()
                .action(Allin)
                .action(Allin)
                .settle();
        var info = result.getPlayer(1);
        assertEquals(20, info.pot.get(0));
        assertEquals(10, info.getProfit());
        assertFalse(info.pot.containsKey(1));
        info = result.getPlayer(2);
        assertFalse(info.pot.containsKey(1));
        assertEquals(-10, info.getProfit());

        // 最后只有一个人的话不分池
        result = AssertTexas.builder()
                .players(createPlayersByChips(10, 20))
                .communityCards(club9, club10, spadesA, heartK, club8)
                .build()
                .start()
                .action(Fold)
                .settle();
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
        result = AssertTexas.builder()
                .players(createPlayersByChips(10, 100, 40))
                .communityCards(club9, club10, spadesA, heartK, club8)
                .build()
                .start()
                .action(Allin)
                .action(Call)
                .action(Call)
                .action(Raise, 20)
                .action(Allin)
                .action(Fold)
                .settle();
        assertEquals(20, result.getPlayer(1).getProfit());
        assertEquals(-30, result.getPlayer(2).getProfit());
        assertEquals(10, result.getPlayer(3).getProfit());
        assertEquals(10, result.getPlayer(3).refund);

        // 10 B
        // 10 20 C
        // 10 20 10 A
        // B 玩家弃牌，C 玩家赢
        result = AssertTexas.builder()
                .players(createPlayersByChips(40, 10, 40))
                .communityCards(club9, club10, spadesA, heartK, club8)
                .build()
                .start()
                .action(Call)
                .action(Allin)
                .action(Call)
                .action(Call)
                .action(Raise, 20)
                .action(Allin)
                .action(Fold)
                .settle();
        assertEquals(40, result.getPlayer(1).getProfit());
        assertEquals(-10, result.getPlayer(2).getProfit());
        assertEquals(-30, result.getPlayer(3).getProfit());
        assertEquals(10, result.getPlayer(1).refund);
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
        var texas = AssertTexas.builder(5).build().start()
                .leftChips(2).assertAuth(Fold, Allin).action(Allin)
                .leftChips(3).assertAuth(Fold, Allin, Call).action(Call)
                .leftChips(10).action(Call)
                .leftChips(1).assertAuth(Fold, Allin).action(Allin)
                .leftChips(3).assertAuth(Raise, Fold, Allin, Check).action(Check)
                .leftChips(2).assertAuth(Fold, Allin, Check).action(Allin);

        AssertTexas.builder(5).build().start()
                .leftChips(10).assertAuth(Fold, Call, Raise, Allin, BBlind2, BBlind3, BBlind4)
                .action(Raise, 10)
                .action(Fold)
                .leftChips(40).assertAuth(Fold, Call, Raise, Allin, Pot1_1, Pot1_2, Pot2_3);

        texas = AssertTexas.builder().build().start();
        var auth = texas.authority();
        assertEquals(7, auth.size());
        assertEquals(0, auth.get(Fold));
        assertEquals(1, auth.get(Call));
        assertEquals(4, auth.get(Raise));
        assertEquals(99, auth.get(Allin));
        assertEquals(5, auth.get(BBlind2));
        assertEquals(7, auth.get(BBlind3));
        assertEquals(9, auth.get(BBlind4));

        texas.action(Raise, 8);
        auth = texas.authority();
        assertEquals(7, auth.size());
        assertEquals(0, auth.get(Fold));
        assertEquals(7, auth.get(Call));
        assertEquals(16, auth.get(Raise));
        assertEquals(98, auth.get(Allin));
        assertEquals(16 + 2, auth.get(Pot1_2));
        assertEquals(19 + 2, auth.get(Pot2_3));
        assertEquals(25 + 2, auth.get(Pot1_1));

        // 最小加注线等于最大加注则只能allin, 没有加注
        texas = AssertTexas.builder()
                .players(new Player(1, 10), new Player(2, 100))
                .build().start()
                .action(Call)
                .action(Check)
                .action(Raise, 4);
        auth = texas.authority();
        assertTrue(auth.containsKey(Allin));
        assertFalse(auth.containsKey(Raise));

        // 只有弃牌操作复现
        AssertTexas.builder()
                .players(new Player(1, 100)
                        , new Player(2, 110)
                        , new Player(3, 110)
                        , new Player(4, 100))
                .build().start()
                .action(Raise, 70)
                .action(Allin).action(Call)
                .action(Allin).action(Fold)
                .assertAuth(Fold, Allin);

        // 对allin不足的人call后不能再allin
        // 需求先不要
        auth = AssertTexas.builder()
                .players(new Player(1, 40)
                        , new Player(2, 60)
                        , new Player(3, 80))
                .build().start()
                .action(Call)
                .action(Call)
                .action(Raise, 32)
                .action(Allin)
                .authority();
        assertTrue(auth.containsKey(Optype.Call));
//        equalsAuth(texas, null, Fold, Allin);

        // AllinOrFold
        AssertTexas.builder()
                .players(new Player(1, 200)
                        , new Player(2, 10000))
                .smallBlind(100)
                .regulation(Regulation.AllinOrFold, 1)
                .build().start()
                .assertAuth(Fold, Allin)
                .action(Allin)
                .assertAuth(Fold, Allin);
    }

    @Test
    public void testAnte() throws Exception {
        var texas = AssertTexas.builder(3).ante(1).build().start()
                .assertAnte(1)
                .assertAnteSum(3)
                .assertSumPot(6);
        assertEquals(99, texas.dealer().getChips());
        assertEquals(98, texas.sbPlayer().getChips());
        assertEquals(97, texas.bbPlayer().getChips());

        AssertTexas.builder(4).ante(2).build().start()
                .assertAnte(2)
                .assertSumPot(11);


        // 庄家自动压一个前注
        AssertTexas.builder().smallBlind(1).ante(20)
                .players(new Player(1, 10), new Player(1, 50))
                .build().start()
                .assertIsOver(true);
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
        AssertTexas.builder()
                .ring(ring)
                .smallBlind(50)
                .build().start()
                .assertDealer(1)
                .assertSbPlayer(1)
                .assertBbPlayer(2)
                .assertSmallBlind(50)
                .assertState(Transfer.SHOWDOWN);

        var texas = AssertTexas.builder().build().start();
        Tester.assertException(texas::start, IllegalArgumentException.class);
    }

    @Test
    public void testCommunityCards() throws Exception {
        AssertTexas.builder()
                .communityCards(club9, club10, spadesA, heartK, club8)
                .build().start()
                .assertCommunityCards()

                .action(Call)
                .action(Raise, 2)
                .action(Call)
                .assertCommunityCards(club9, club10, spadesA)

                .action(Check)
                .action(Check)
                .assertCommunityCards(club9, club10, spadesA, heartK)

                .action(Check)
                .action(Check)
                .assertCommunityCards(club9, club10, spadesA, heartK, club8)

                .action(Check)
                .action(Check)
                .assertCommunityCards(club9, club10, spadesA, heartK, club8);

        AssertTexas.builder()
                .communityCards(club9, club10, spadesA, heartK, club8)
                .players(new Player(1, 100), new Player(2, 200), new Player(3, 300))
                .build().start().action(Allin).action(Call).action(Fold)
                .assertCommunityCards(club9, club10, spadesA, heartK, club8);
    }

    @Test
    public void testIsOver() throws Exception {
        AssertTexas.builder().build().start()
                .assertIsOver(false)
                .action(Call)
                .assertIsOver(false)
                .action(Fold)
                .assertIsOver(true);

        AssertTexas.builder().build().start()
                .assertIsOver(false)
                .action(Call)
                .action(Raise, 2)
                .action(Call)
                .assertIsOver(false)
                .action(Check)
                .action(Check)
                .assertIsOver(false)
                .action(Check)
                .action(Check)
                .assertIsOver(false)
                .action(Check)
                .action(Check)
                .assertIsOver(true);


        // 大盲直接allin结束
        AssertTexas.builder()
                .players(new Player(1, 600), new Player(2, 200))
                .smallBlind(200)
                .build().start()
                .assertIsOver(true);

        // 大盲直接allin不结束
        AssertTexas.builder()
                .players(new Player(1, 600), new Player(2, 300))
                .smallBlind(200)
                .build().start()
                .assertIsOver(false);

        // 小盲直接allin结束
        AssertTexas.builder()
                .players(new Player(1, 200), new Player(2, 600))
                .smallBlind(200)
                .build().start()
                .assertIsOver(true);

        // 大小盲同时allin结束
        AssertTexas.builder()
                .players(new Player(1, 200), new Player(2, 300))
                .smallBlind(200)
                .build().start()
                .assertIsOver(true);

        // 通过Raise将所有的筹码加上, 实际是allin
        // 最低最低加注筹码刚好等于剩余筹码，只能allin
        AssertTexas.builder()
                .players(new Player(1, 750), new Player(2, 2000))
                .smallBlind(20)
                .build().start()
                .assertIsOver(false)
                .action(Call)
                .action(Raise, 710)
                .action(Allin)
                .assertIsOver(true);
    }
}
