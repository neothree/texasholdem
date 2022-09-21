package com.texasthree.game.insurance;

import com.texasthree.game.AllCard;
import com.texasthree.game.texas.Card;
import com.texasthree.game.texas.Circle;
import com.texasthree.game.texas.Player;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InsurancePotTest extends AllCard {

    @Test
    public void testOdds() {
        assertEquals(0, InsurancePot.odds(0).compareTo(BigDecimal.ZERO));
        for (var count = 1; count < 15; count++) {
            assertEquals(1, InsurancePot.odds(count).compareTo(BigDecimal.ZERO));
        }
        assertEquals(0, InsurancePot.odds(15).compareTo(BigDecimal.ZERO));
    }

    @Test
    public void testOuts() {
        var players = AssertInsurance.cardsToPlayers(
                diamondA, heartA,
                diamond2, heart2,
                diamond3, heart3,
                diamond4, heart4,
                diamond5, heart5);
        var communityCards = Arrays.asList(spades5, diamond7, spades4);
        var leftCard = Arrays.asList(club5, club10, spadesA);
        assertOuts(4, players, communityCards, leftCard, new BigDecimal(31), spadesA);

        communityCards = Arrays.asList(spades5, spades3, spades4);
        leftCard = Arrays.asList(club5, club10, spadesA, club3);
        assertOuts(4, players, communityCards, leftCard, new BigDecimal(16), spadesA, club3);

        communityCards = Arrays.asList( spades5, spades3, spades4, heart10);
        leftCard = Arrays.asList(club5, club10, spadesA, club3);
        assertOuts(4, players, communityCards, leftCard, new BigDecimal(16), spadesA, club3);

        communityCards = Arrays.asList( spades5, spades3, spades4, spadesA, clubA);
        leftCard = Arrays.asList(club5, club10, club3, club2);
        assertOuts(0, players, communityCards, leftCard, new BigDecimal(0));

        players = AssertInsurance.cardsToPlayers(
                diamondA, heartA,
                diamond2, heart2,
                diamond3, heart3,
                diamond4, heart4);
        communityCards = Arrays.asList(  spades5, spades3, spades4, heart10);
        leftCard = Arrays.asList(club5, club10, spadesA, club3);
        assertOuts(3, players, communityCards, leftCard, new BigDecimal(16), spadesA, club3);

        players = AssertInsurance.cardsToPlayers(
                diamondA, heartA,
                diamond2, heart2,
                diamond3, heart3);
        assertOuts(2, players, communityCards, leftCard, new BigDecimal(31), spadesA);

        players = AssertInsurance.cardsToPlayers(
                diamondA, diamond4,
                heartA, heart10
        );
        communityCards = Arrays.asList(spades2, heart2, spadesQ, heart6);
        leftCard = Arrays.asList(clubK, club10, spades5, club3);
        assertOuts(1, players, communityCards, leftCard, new BigDecimal(0));


    }

    private void assertOuts(int applicant, List<Player> players, List<Card> communityCards, List<Card> leftCard, BigDecimal odds, Card... outs) {
        var pot = new InsurancePot(0, 10, 10, Circle.TURN, applicant, players, communityCards, leftCard);
        assertEquals(odds, pot.getOdds());
        AssertInsurance.assertCards(pot.getOuts(), outs);
    }
}