package com.texasthree.game.texas;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author: neo
 * @create: 2022-09-02 12:49
 */
public class AssertTexas extends Texas {

    public static Builder builder(int playerNum) {
        var builder = new AssertTexas.Builder();
        builder.playerNum(playerNum);
        return builder;
    }

    public static class Builder extends Texas.Builder {

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
            this.deal();
            var regulations = this.regulations();
            var cc = this.getCommunityCards();
            return new AssertTexas(regulations, ring, cc);
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

    public AssertTexas assertAnte(int expect) {
        assertEquals(expect, ante());
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


}
