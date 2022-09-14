package com.texasthree.game.texas;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author: neo
 * @create: 2022-09-02 12:49
 */
public class AssertTexas extends Texas {

    public static AssertTexas.Builder builder() {
        return new AssertTexas.Builder();
    }

    public static AssertTexas.Builder builder(int playerNum) {
        var builder = new AssertTexas.Builder();
        builder.playerNum(playerNum);
        return builder;
    }

    public static class Builder extends Texas.Builder {

        @Override
        public Builder communityCards(Card... board) {
            super.communityCards(board);
            return this;
        }

        @Override
        public Builder regulations(Map<Regulation, Integer> regulations) {
            super.regulations(regulations);
            return this;
        }

        @Override
        public Builder regulation(Regulation regulation, Integer value) {
            super.regulation(regulation, value);
            return this;
        }

        @Override
        public Builder ring(Ring<Player> ring) {
            super.ring(ring);
            return this;
        }

        @Override
        public Builder players(List<Player> players) {
            super.players(players);
            return this;
        }

        @Override
        public Builder players(Player... players) {
            super.players(players);
            return this;
        }

        @Override
        public Builder initChips(int initChips) {
            super.initChips(initChips);
            return this;
        }

        @Override
        public Builder smallBlind(int smallBlind) {
            super.smallBlind(smallBlind);
            return this;
        }

        @Override
        public Builder ante(int ante) {
            super.ante(ante);
            return this;
        }

        @Override
        public AssertTexas build() {
            var ring = this.getRing();
            var leftCards = this.deal();
            var regulations = this.regulations();
            return new AssertTexas(regulations, ring, leftCards);
        }
    }

    AssertTexas(Map<Regulation, Integer> regulations, Ring<Player> ring, List<Card> leftCard) {
        super(regulations, ring, leftCard);
    }

    @Override
    public AssertTexas start() {
        super.start();
        return this;
    }

    @Override
    AssertTexas action(Optype op) {
        super.action(op, 0);
        return this;
    }

    @Override
    public AssertTexas leave(Integer id) {
        super.leave(id);
        return this;
    }

    @Override
    public AssertTexas action(Optype op, int chipsAdd) {
        super.action(op, chipsAdd);
        return this;
    }


    public AssertTexas assertCircle(String expect) {
        assertEquals(expect, circle());
        return this;
    }

    public AssertTexas assertOperator(int expect) {
        assertEquals(expect, operator().getId());
        return this;
    }

    public AssertTexas assertOperatorAndCircle(int op, String circle) {
        assertEquals(op, operator().getId());
        assertEquals(circle, circle);
        return this;
    }

    public AssertTexas assertAnte(int expect) {
        assertEquals(expect, ante());
        return this;
    }

    public AssertTexas assertAnteSum(int expect) {
        assertEquals(expect, anteSum());
        return this;
    }

    public AssertTexas assertSumPot(int expect) {
        assertEquals(expect, sumPot());
        return this;
    }

    public AssertTexas assertSmallBlind(int expect) {
        assertEquals(expect, smallBlind());
        return this;
    }

    public AssertTexas assertDealer(int expect) {
        assertEquals(expect, dealer().getId());
        return this;
    }

    public AssertTexas assertSbPlayer(int expect) {
        assertEquals(expect, sbPlayer().getId());
        return this;
    }

    public AssertTexas assertBbPlayer(int expect) {
        assertEquals(expect, bbPlayer().getId());
        return this;
    }

    public AssertTexas assertIsOver(boolean expect) {
        assertEquals(expect, isOver());
        return this;
    }

    public AssertTexas assertState(Transfer expect) {
        assertEquals(expect, state());
        return this;
    }

    public AssertTexas assertPots(Integer ... pots) {
        var actual = this.getPots();
        assertEquals(pots.length, actual.size());
        for (var i = 0; i < pots.length ; i++) {
            assertEquals(pots[i], actual.get(i));
        }
        return this;
    }

    AssertTexas assertAuth(Optype... ops) throws Exception {
        var auth = this.authority();
        if (false) {
            var mp = new ObjectMapper();
            System.out.println(mp.writeValueAsString(auth));
        }
        assertEquals(auth.size(), ops.length);
        for (var v : ops) {
            assertTrue(auth.containsKey(v));
        }
        return this;
    }

    AssertTexas assertCommunityCards(Card... cards) throws Exception {
        var board = this.getCommunityCards();
        assertEquals(board.size(), cards.length);
        for (var i = 0; i < cards.length; i++) {
            assertEquals(board.get(i), cards[i]);
        }
        return this;
    }

    AssertTexas leftChips(int left) {
        var player = this.operator();
        player.changeChips(left - player.getChips());
        return this;
    }

}
