package com.texasthree.game.insurance;

import com.texasthree.game.AllCard;
import com.texasthree.game.texas.Card;
import com.texasthree.game.texas.Circle;
import com.texasthree.game.texas.Divide;
import com.texasthree.game.texas.Player;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

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
    public void testPot() {
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

        // p1 只有一个玩家, 不买保险
        var players = AssertInsurance.cardsToPlayers(cards);
        var p0 = new Divide(0);
        var m = players.stream().map(Player::getId).collect(Collectors.toSet());
        p0.add(m, 100, m);
        assertTrue(p0.compare());
        var p1 = new Divide(1);
        var member = new HashSet<Integer>();
        member.add(players.get(0).getId());
        p1.add(m, 10, member);
        assertFalse(p1.compare());
        AssertInsurance.builder()
                .players(players)
                .pots(p0, p1)
                .leftCard(spades5, diamond3, spades4, club5, club10, spadesA, club4)
                .build()
                .assertPots(Circle.TURN, 2, 1);
    }

    @Test
    public void testBuy() {
        var cards = new Card[]{
                diamondA, heartA,
                diamond5, heart5};
        AssertInsurance.builder(cards)
                .leftCard(spades5, diamond7, spades4, club9, club10, spadesA)
                .build()
                .assertCircle(Circle.TURN)
                .end()
                .assertCircle(Circle.RIVER)
                .end()
                .assertFinished(true);

        AssertInsurance.builder(cards)
                .leftCard(spades5, diamond7, spades4, spadesA, club10, club5)
                .build()
                .assertCircle(Circle.TURN)
                .assertCirclePot(0, 1, 1)
                .end()
                .assertCircle(Circle.RIVER)
                .assertCirclePot(0, 0, 1)
                .end()
                .assertFinished(true);

        AssertInsurance.builder(cards)
                .leftCard(spades5, diamond7, spades4, club9, club10, spadesA)
                .circle(Circle.TURN)
                .build()
                .assertPots(Circle.TURN, 1, 1)
                .assertPots(Circle.RIVER, 1, 1)

                .assertCircle(Circle.TURN)
                .assertCommunityCards(spades5, diamond7, spades4)
                .assertCircleFinished(false)
                .buy(0, 20, spadesA)
                .assertCircleFinished(true)
                .end()
                .assertFinished(false)

                .assertCircle(Circle.RIVER)
                .assertCommunityCards(spades5, diamond7, spades4, club9)
                .assertCircleFinished(false)
                .buy(0, 20, spadesA)
                .assertCircleFinished(true)
                .assertFinished(true)
                .assertCommunityCards(spades5, diamond7, spades4, club9, club10)
        ;
    }
}