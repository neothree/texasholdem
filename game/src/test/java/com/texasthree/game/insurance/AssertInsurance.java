package com.texasthree.game.insurance;

import com.texasthree.game.texas.*;

import java.math.BigDecimal;
import java.util.*;
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
        if (cards.length > 0) {
            builder.players(cards);
        }
        return builder;
    }

    static List<Player> cardsToPlayers(Card... cards) {
        assertTrue(cards.length > 0);
        assertEquals(0, cards.length % 2);
        var players = new ArrayList<Player>();
        for (var i = 0; i < cards.length / 2; i++) {
            var p = new Player(i, 0);
            var index = i * 2;
            p.setHand(new Hand(Arrays.asList(cards[index], cards[index + 1])));
            players.add(p);
        }
        return players;
    }

    public static class Builder {

        private List<Player> players;

        private List<Card> leftCard = new ArrayList<>();

        private String circle = Circle.TURN;

        private List<Divide> pots = new ArrayList<>();

        public Builder players(List<Player> players) {
            this.players = players;
            return this;
        }

        public Builder players(Card... cards) {
            this.players(cardsToPlayers(cards));
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

    AssertInsurance assertCirclePot(int id, int winner, int size) {
        var pots = this.getCirclePots();
        assertEquals(size, pots.size());
        var pot = pots.get(id);
        assertEquals(winner, pot.applicant);
        return this;
    }

    private List<InsurancePot> getCirclePots() {
        var circle = this.insurance.getCircle();
        return this.insurance.getPots().stream().filter(v -> v.circle.equals(circle)).collect(Collectors.toList());
    }


    AssertInsurance assertPots(String circle, int winner, int size) {
        var pots = this.insurance.getPots().stream()
                .filter(v -> v.circle.equals(circle))
                .collect(Collectors.toList());

        assertEquals(size, pots.size());
        assertTrue(pots.stream().allMatch(v -> v.applicant == winner));
        return this;
    }

    AssertInsurance assertOuts(Card... expect) {
        assertCards(pot.getOuts(), expect);
        return this;
    }

    AssertInsurance assertCommunityCards(Card... expect) {
        assertCards(insurance.getCommunityCards(), expect);
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

    private List<Claim> claim;

    AssertInsurance assertClaim(int id, int amount) {
        if (this.claim == null) {
            this.claim = this.insurance.claims();
        }
        assertEquals(amount, this.claim.stream()
                .filter(v -> v.applicant == id)
                .mapToInt(v -> v.profit)
                .sum());
        return this;
    }

    public static void assertCards(List<Card> a, Card... b) {
        var bs = Arrays.asList(b);
        var set = new HashSet<>(a);
        assertEquals(set.size(), bs.size());
        assertTrue(set.containsAll(bs));
    }
}
