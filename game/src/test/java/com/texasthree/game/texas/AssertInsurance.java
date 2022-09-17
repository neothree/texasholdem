package com.texasthree.game.texas;

import com.texasthree.game.insurance.Insurance;
import com.texasthree.game.insurance.InsurancePot;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author: neo
 * @create: 2022-09-12 22:45
 */
public class AssertInsurance {
    static Builder builder(Card... cards) {
        var builder = new Builder();
        builder.players(cards);
        return builder;
    }

    public static class Builder {

        private List<Player> players;

        private List<Card> leftCard = new ArrayList<>();

        private String circle = Circle.TURN;

        private List<Divide> pots = new ArrayList<>();

        public Builder players(Player... players) {
            this.players = Arrays.asList(players);
            return this;
        }

        public Builder players(Card... cards) {
            assertTrue(cards.length > 0);
            assertEquals(0, cards.length % 2);
            this.players = new ArrayList<>();
            for (var i = 0; i < cards.length / 2; i++) {
                var p = new Player(i, 0);
                var index = i * 2;
                p.setHand(new Hand(Arrays.asList(cards[index], cards[index + 1])));
                this.players.add(p);
            }

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
            if (players.isEmpty()) {
                throw new IllegalArgumentException();
            }
            if (pots.isEmpty()) {
                var p = new Divide(0);
                var m = players.stream().map(Player::getId).collect(Collectors.toSet());
                p.add(m, 100, m);
                this.pots.add(p);
            }
            return new AssertInsurance(players, leftCard, circle, pots);
        }
    }

    private final Insurance insurance;

    private AssertInsurance(List<Player> players, List<Card> leftCard, String circle, List<Divide> pots) {
        this.insurance = new Insurance(players, leftCard, circle, pots);
    }

    AssertInsurance buy(int potId, int amount, Card... outs) {
        this.insurance.buy(potId, amount, Arrays.asList(outs));
        return this;
    }

    AssertInsurance end() {
        this.insurance.end();
        return this;
    }

    private InsurancePot pot;

    AssertInsurance selectPot(int potId, String circle) {
        this.pot = this.insurance.getPots().stream()
                .filter(v -> v.id == potId && v.circle.equals(circle))
                .findFirst().get();
        return this;
    }

    AssertInsurance assertWinner(int id) {
        assertEquals(id, pot.applicant);
        return this;
    }

    AssertInsurance assertPot(int id, int chipsBet, int chips) {
        assertEquals(id, pot.applicant);
        assertEquals(chipsBet, pot.chipsBet);
        assertEquals(chips, pot.sum);
        return this;
    }

    AssertInsurance assertPots(String circle, int winner, int size) {
        var pots = this.insurance.getPots().stream()
                .filter(v -> v.circle.equals(circle))
                .collect(Collectors.toList());

        assertTrue(pots.stream().allMatch(v -> v.applicant == winner));
        assertEquals(size, pots.size());
        return this;
    }

    AssertInsurance assertOuts(Card... expect) {
        assertCards(pot.getOuts(), Arrays.asList(expect));
        return this;
    }

    AssertInsurance assertCommunityCards(Card... expect) {
        assertCards(insurance.getCommunityCards(), Arrays.asList(expect));
        return this;
    }

    AssertInsurance assertOdds(BigDecimal expect) {
        assertEquals(0, pot.getOdds().compareTo(expect));
        return this;
    }

    AssertInsurance assertCircle(String expect) {
        assertEquals(expect, this.insurance.getCircle());
        return this;
    }

    AssertInsurance assertCircleFinished(boolean expect) {
        assertEquals(expect, this.insurance.circleFinished());
        return this;
    }

    AssertInsurance assertFinished(boolean expect) {
        assertEquals(expect, this.insurance.finished());
        return this;
    }

    private void assertCards(List<Card> a, List<Card> b) {
        var set = new HashSet<>(a);
        assertEquals(set.size(), b.size());
        assertTrue(set.containsAll(b));
    }
}
