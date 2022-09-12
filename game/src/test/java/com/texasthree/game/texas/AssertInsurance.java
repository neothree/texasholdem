package com.texasthree.game.texas;

import java.util.Arrays;
import java.util.List;

/**
 * @author: neo
 * @create: 2022-09-12 22:45
 */
public class AssertInsurance {
    static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private List<Card> communityCards;

        private List<Player> players;

        private List<Card> leftCard;

        private String circle;

        private List<Divide> pots;

        public Builder communityCards(Card... communityCards) {
            this.communityCards = Arrays.asList(communityCards);
            return this;
        }


        public Builder players(Player... players) {
            this.players = Arrays.asList(players);
            return this;
        }

        public Builder leftCard(Card... cards) {
            this.leftCard = Arrays.asList(cards);
            return this;
        }

        public Builder pots(Divide... pots) {
            this.pots = Arrays.asList(pots);
            return this;
        }

        public Builder circle(String circle) {
            this.circle = circle;
            return this;
        }

        AssertInsurance build() {
            return new AssertInsurance(players, communityCards, leftCard, circle, pots);
        }
    }

    private final Insurance insurance;

    private AssertInsurance(List<Player> players, List<Card> communityCards, List<Card> leftCard, String circle, List<Divide> pots) {
        this.insurance = new Insurance(players, communityCards, leftCard, circle, pots);
    }

    AssertInsurance buy(int potId, int amount, List<Card> outs) {
        this.insurance.buy(potId, amount, outs);
        return this;
    }
}
