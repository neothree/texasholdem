package com.texasthree.game.texas;

import com.texasthree.game.AllCard;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InsuranceTest extends AllCard {

    @Test
    public void testOdds() {
        assertEquals(0, Insurance.odds(0).compareTo(BigDecimal.ZERO));
        for (var count = 1; count < 15; count++) {
            assertEquals(1, Insurance.odds(count).compareTo(BigDecimal.ZERO));
        }
        assertEquals(0, Insurance.odds(15).compareTo(BigDecimal.ZERO));
    }

    @Test
    public void testOuts() {
        var cards = new Card[]{
                diamondA, heartA,
                diamond4, heart4,
                diamond5, heart5};
        AssertInsurance.builder(cards)
                .leftCard(spades5, diamond7, spades4, club5, club10, spadesA)
                .build()
                .selectPot(0, Circle.TURN)
                .assertPot(2, 100, 300)
                .assertOuts(spadesA);

        AssertInsurance.builder(cards)
                .leftCard(spades5, diamond3, spades4, club5, club10, spadesA, club4)
                .build()
                .selectPot(0, Circle.TURN)
                .assertWinner(2)
                .assertOuts(spadesA, club4);
    }

    @Test
    public void testBuy() {
        var cards = new Card[]{
                diamondA, heartA,
                diamond5, heart5};
        AssertInsurance.builder(cards)
                .leftCard(spades5, diamond7, spades4, club9, club10, spadesA)
                .circle(Circle.TURN)
                .build()
                .assertPots(Circle.TURN, 1, 1)
                .assertPots(Circle.RIVER, 1, 1)

                .assertCircleFinished(false)
                .buy(0, 20, spadesA)
                .assertCircleFinished(true)
                .buyEnd()
                .assertCircleFinished(false)
                .buy(0, 20, spadesA)
                .assertCircleFinished(true)

        ;
    }
}