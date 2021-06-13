package com.texasthree.round.texas;


import com.texasthree.round.AllCard;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Hand Tester.
 *
 * @author <Authors name>
 * @version 1.0
 * @since <pre>Jun 12, 2021</pre>
 */
public class HandTest extends AllCard {

    @Test
    public void testTypeOfRoyalFlush() {
        var cardList = Arrays.asList(diamond10, diamondJ, diamondQ, diamondK, diamondA, spades5, heart5);
        testTypeOf(cardList, cardList.subList(0, 5), CardType.RoyalFlush);

        cardList = Arrays.asList(club10, clubJ, heart5, clubQ, clubK, clubA, spades5);
        testTypeOf(cardList, Arrays.asList(club10, clubJ, clubQ, clubK, clubA), CardType.RoyalFlush);
    }

    @Test
    public void testTypeOfStraightFlush() {
        var cardList = Arrays.asList(diamond10, diamondJ, diamondQ, diamondK, diamond9, spades5, heart5);
        testTypeOf(cardList, cardList.subList(0, 5), CardType.StraightFlush);

        cardList = Arrays.asList(diamond2, diamond3, diamond4, diamond5, diamond6, spades5, heart5);
        testTypeOf(cardList, Arrays.asList(diamond2, diamond6, diamond4, diamond5, diamond3), CardType.StraightFlush);

        cardList = Arrays.asList(spades5, spades6, spades7, spades8, diamondA, spades9, heart5);
        testTypeOf(cardList, Arrays.asList(spades5, spades6, spades9, spades8, spades7), CardType.StraightFlush);
    }

    @Test
    public void testTypeOfFourOfKind() {
        var cardList = Arrays.asList(diamond10, club10, heart10, spades10, spadesK, spades5, heart5);
        testTypeOf(cardList, cardList.subList(0, 5), CardType.FourOfKind);

        cardList = Arrays.asList(diamondQ, clubQ, heartQ, spadesQ, spadesK, spades5, heart5);
        testTypeOf(cardList, Arrays.asList(diamondQ, clubQ, heartQ, spadesQ, spadesK), CardType.FourOfKind);

        System.out.println(Hand.typeOf(cardList));
    }

    @Test
    public void testTypeOfFullHouse() {
        var cardList = Arrays.asList(diamond10, club10, heart10, diamond9, club9, heart2, heart5);
        testTypeOf(cardList, cardList.subList(0, 5), CardType.FullHouse);

        cardList = Arrays.asList(diamond7, club7, heart7, heart9, heart5, spades8, club8);
        testTypeOf(cardList, Arrays.asList(diamond7, club7, spades8, heart7, club8), CardType.FullHouse);
    }

    @Test
    public void testTypeOfFlush() {
        var cardList = Arrays.asList(diamondK, diamond10, diamondA, diamond3, diamond8, heart9, heart5);
        testTypeOf(cardList, cardList.subList(0, 5), CardType.Flush);

        cardList = Arrays.asList(spades6, spades7, spades8, spades9, spades2, spades3, heart5);
        testTypeOf(cardList, Arrays.asList(spades6, spades7, spades8, spades9, spades3), CardType.Flush);

        // 葫芦小于同花
//        cardList = Arrays.asList(diamond7, club7, heart7, spades8, club8, heart9, heart5, heartA, heart3);
//        Hand.typeOf(cardList, 6);
//        testTypeOf(cardList, Arrays.asList(heart9, heartA, heart7, heart5, heart3), CardType.Flush);
    }

    @Test
    public void testTypeOfStraight() {
        var cardList = Arrays.asList(club2, diamond3, spades4, spades5, heart6, heart9, heart5);
        testTypeOf(cardList, cardList.subList(0, 5), CardType.Straight);

        cardList = Arrays.asList(club10, diamondJ, spadesQ, spadesK, heartA, heart9, heart5);
        testTypeOf(cardList, Arrays.asList(club10, diamondJ, spadesQ, spadesK, heartA), CardType.Straight);

        // A, 2, 3, 4, 5
        cardList = Arrays.asList(club2, diamond3, spades4, heartA, heart9, heart10, spades5);
        testTypeOf(cardList, Arrays.asList(club2, diamond3, spades4, spades5, heartA), CardType.Straight);

        //  A, 6, 7, 8, 9
//        cardList = Arrays.asList(diamond6, spades7, spades8, heartA, heart9, heartQ, heartJ);
//        Hand.typeOf(cardList, 6);
//        testTypeOf(cardList, Arrays.asList(heartA, diamond6, spades7, spades8, heart9), CardType.Straight);
    }

    @Test
    public void testTypeOfThreeOfKind() {
        var cardList = Arrays.asList(club2, diamond2, spades2, heart9, heartQ, spades5, heart6);
        testTypeOf(cardList, cardList.subList(0, 5), CardType.ThreeOfKind);

        cardList = Arrays.asList(club7, diamond7, spades7, spades5, heart6, heart9, heartQ);
        testTypeOf(cardList, Arrays.asList(club7, diamond7, spades7, heartQ, heart9), CardType.ThreeOfKind);

        cardList = Arrays.asList(club7, diamond7, spades7);
        testTypeOf(cardList, cardList, CardType.ThreeOfKind);
    }

    @Test
    public void testTypeOfTwoPairs() {
        var cardList = Arrays.asList(club4, diamond4, spades5, club5, heartQ, heart6, heart9);
        testTypeOf(cardList, cardList.subList(0, 5), CardType.TwoPairs);

        cardList = Arrays.asList(spades8, diamond8, spades10, heart10, heart6, heart9, heartQ);
        testTypeOf(cardList, Arrays.asList(spades8, diamond8, spades10, heart10, heartQ), CardType.TwoPairs);
    }

    @Test
    public void testTypeOfOnePair() {
        var cardList = Arrays.asList(club4, diamond4, club7, heart9, heartQ, spades5, heart6);
        testTypeOf(cardList, cardList.subList(0, 5), CardType.OnePair);

        cardList = Arrays.asList(club9, diamond9, spades5, club7, heart6, heartJ, heartQ);
        testTypeOf(cardList, Arrays.asList(club9, diamond9, club7, heartJ, heartQ), CardType.OnePair);

        cardList = Arrays.asList(club9, diamond9);
        testTypeOf(cardList, cardList, CardType.OnePair);

        cardList = Arrays.asList(club9, diamond9, spades5);
        testTypeOf(cardList, cardList, CardType.OnePair);
    }

    @Test
    public void testTypeOfHighCard() {
        var cardList = Arrays.asList(spades5, club7, heart6, heart9, heartQ, club4, diamond2);
        testTypeOf(cardList, cardList.subList(0, 5), CardType.HighCard);

        cardList = Arrays.asList(clubA, diamondK, spadesQ, clubJ, heart8, heart6, heart5);
        testTypeOf(cardList, Arrays.asList(clubJ, diamondK, spadesQ, heart8, clubA), CardType.HighCard);

        cardList = Arrays.asList(club9, heartQ);
        testTypeOf(cardList, cardList, CardType.HighCard);

        cardList = Arrays.asList(clubA, diamondK, spadesQ);
        testTypeOf(cardList, cardList, CardType.HighCard);

    }

    private static void testTypeOf(Collection<Card> cards, Collection<Card> best, CardType type) {
        var hand = Hand.typeOf(cards);
        assertEquals(type, hand.getType());
        var set = new HashSet<>(best);
        assertEquals(hand.getBest().size(), set.size());
        assertTrue(set.containsAll(hand.getBest()));
    }

    @Test
    public void testCompareRoyalFlush() {
        var cardList = Arrays.asList(diamond10, diamondJ, diamondQ, diamondK, diamondA, spades5, heart5);
        var other = Arrays.asList(club10, clubJ, clubQ, clubK, clubA, spades5, heart5);
        equalsCompare(cardList, other, 0, CardType.RoyalFlush);

        other = Arrays.asList(diamond10, diamondJ, diamondQ, diamondK, diamond9, spades5, heart5);
        equalsCompare(cardList, other, 1, CardType.StraightFlush);

        other = Arrays.asList(diamond10, club10, heart10, spades10, spadesK, spades5, heart5);
        equalsCompare(cardList, other, 1, CardType.FourOfKind);

        other = Arrays.asList(diamond10, club10, heart10, diamond9, club9, heart9, heart5);
        equalsCompare(cardList, other, 1, CardType.FullHouse);

        other = Arrays.asList(diamondK, diamond10, diamondA, diamond3, diamond8, heart9, heart5);
        equalsCompare(cardList, other, 1, CardType.Flush);

        other = Arrays.asList(club2, diamond3, spades4, spades5, heart6, heart9, heart5);
        equalsCompare(cardList, other, 1, CardType.Straight);

        other = Arrays.asList(club2, diamond2, spades2, spades5, heart6, heart9, heartQ);
        equalsCompare(cardList, other, 1, CardType.ThreeOfKind);

        other = Arrays.asList(club4, diamond4, spades5, club5, heart6, heart9, heartQ);
        equalsCompare(cardList, other, 1, CardType.TwoPairs);

        other = Arrays.asList(club4, diamond4, spades5, club7, heart6, heart9, heartQ);
        equalsCompare(cardList, other, 1, CardType.OnePair);

        other = Arrays.asList(club4, diamond2, spades5, club7, heart6, heart9, heartQ);
        equalsCompare(cardList, other, 1, CardType.HighCard);
    }

    @Test
    public void testCompareStraightFlush() {
        var cardList = Arrays.asList(diamond10, diamondJ, diamondQ, diamond8, diamond9, spades5, heart5);

        var other = Arrays.asList(club10, clubJ, clubQ, clubK, clubA, spades5, heart5);
        equalsCompare(cardList, other, -1, CardType.RoyalFlush);

        other = Arrays.asList(diamond10, diamondJ, diamondQ, diamondK, diamond9, spades5, heart5);
        equalsCompare(cardList, other, -1, CardType.StraightFlush);

        other = Arrays.asList(diamond10, diamondJ, diamondQ, diamond8, diamond9, spades5, heart5);
        equalsCompare(cardList, other, 0, CardType.StraightFlush);

        other = Arrays.asList(spadesA, spades2, spades3, spades4, spades5);
        equalsCompare(cardList, other, 1, CardType.StraightFlush);

        other = Arrays.asList(diamond10, club10, heart10, spades10, spadesK, spades5, heart5);
        equalsCompare(cardList, other, 1, CardType.FourOfKind);

        other = Arrays.asList(diamond10, club10, heart10, diamond9, club9, heart9, heart5);
        equalsCompare(cardList, other, 1, CardType.FullHouse);

        other = Arrays.asList(diamondK, diamond10, diamondA, diamond3, diamond8, heart9, heart5);
        equalsCompare(cardList, other, 1, CardType.Flush);

        other = Arrays.asList(club2, diamond3, spades4, spades5, heart6, heart9, heart5);
        equalsCompare(cardList, other, 1, CardType.Straight);

        other = Arrays.asList(club2, diamond2, spades2, spades5, heart6, heart9, heartQ);
        equalsCompare(cardList, other, 1, CardType.ThreeOfKind);

        other = Arrays.asList(club4, diamond4, spades5, club5, heart6, heart9, heartQ);
        equalsCompare(cardList, other, 1, CardType.TwoPairs);

        other = Arrays.asList(club4, diamond4, spades5, club7, heart6, heart9, heartQ);
        equalsCompare(cardList, other, 1, CardType.OnePair);

        other = Arrays.asList(club4, diamond2, spades5, club7, heart6, heart9, heartQ);
        equalsCompare(cardList, other, 1, CardType.HighCard);
    }

    @Test
    public void testCompareFourOfKind() {
        var cardList = Arrays.asList(diamond10, club10, heart10, spades10, spadesQ, spades5, heart5);

        var other = Arrays.asList(club10, clubJ, clubQ, clubK, clubA, spades5, heart5);
        equalsCompare(cardList, other, -1, CardType.RoyalFlush);

        other = Arrays.asList(diamond10, diamondJ, diamondQ, diamond8, diamond9, spades5, heart5);
        equalsCompare(cardList, other, -1, CardType.StraightFlush);

        other = Arrays.asList(diamondJ, clubJ, heartJ, spadesJ, spadesK, spades5, heart5);
        equalsCompare(cardList, other, -1, CardType.FourOfKind);

        other = Arrays.asList(diamond10, club10, heart10, spades10, spadesK, spades5, heart5);
        equalsCompare(cardList, other, -1, CardType.FourOfKind);

        other = Arrays.asList(diamond10, club10, heart10, spades10, spadesQ, spades5, heart5);
        equalsCompare(cardList, other, 0, CardType.FourOfKind);

        other = Arrays.asList(diamond10, club10, heart10, spades10, spades9, spades5, heart5);
        equalsCompare(cardList, other, 1, CardType.FourOfKind);

        other = Arrays.asList(diamond10, club10, heart10, diamond9, club9, heart9, heart5);
        equalsCompare(cardList, other, 1, CardType.FullHouse);

        other = Arrays.asList(diamondK, diamond10, diamondA, diamond3, diamond8, heart9, heart5);
        equalsCompare(cardList, other, 1, CardType.Flush);

        other = Arrays.asList(club2, diamond3, spades4, spades5, heart6, heart9, heart5);
        equalsCompare(cardList, other, 1, CardType.Straight);

        other = Arrays.asList(club2, diamond2, spades2, spades5, heart6, heart9, heartQ);
        equalsCompare(cardList, other, 1, CardType.ThreeOfKind);

        other = Arrays.asList(club4, diamond4, spades5, club5, heart6, heart9, heartQ);
        equalsCompare(cardList, other, 1, CardType.TwoPairs);

        other = Arrays.asList(club4, diamond4, spades5, club7, heart6, heart9, heartQ);
        equalsCompare(cardList, other, 1, CardType.OnePair);

        other = Arrays.asList(club4, diamond2, spades5, club7, heart6, heart9, heartQ);
        equalsCompare(cardList, other, 1, CardType.HighCard);
    }

    @Test
    public void testCompareFullHouse() {
        var cardList = Arrays.asList(diamond10, club10, heart10, diamond9, club9, heart9, heart5);

        var other = Arrays.asList(club10, clubJ, clubQ, clubK, clubA, spades5, heart5);
        equalsCompare(cardList, other, -1, CardType.RoyalFlush);

        other = Arrays.asList(diamond10, diamondJ, diamondQ, diamond8, diamond9, spades5, heart5);
        equalsCompare(cardList, other, -1, CardType.StraightFlush);

        other = Arrays.asList(diamond10, club10, heart10, spades10, spadesK, spades5, heart5);
        equalsCompare(cardList, other, -1, CardType.FourOfKind);

        other = Arrays.asList(diamondJ, clubJ, heartJ, diamond9, club9, heart9, heart5);
        equalsCompare(cardList, other, -1, CardType.FullHouse);

        other = Arrays.asList(diamond10, club10, heart10, diamondA, clubA, heart9, heart5);
        equalsCompare(cardList, other, -1, CardType.FullHouse);

        other = Arrays.asList(diamond10, club10, heart10, diamond9, club9, heart9, heart5);
        equalsCompare(cardList, other, 0, CardType.FullHouse);

        other = Arrays.asList(diamond10, club10, heart10, diamond8, club8, heart9, heart5);
        equalsCompare(cardList, other, 1, CardType.FullHouse);

        other = Arrays.asList(diamond7, club7, heart7, diamondQ, clubQ, heart9, heart5);
        equalsCompare(cardList, other, 1, CardType.FullHouse);

        other = Arrays.asList(diamondK, diamond10, diamondA, diamond3, diamond8, heart9, heart5);
        equalsCompare(cardList, other, 1, CardType.Flush);

        other = Arrays.asList(club2, diamond3, spades4, spades5, heart6, heart9, heart5);
        equalsCompare(cardList, other, 1, CardType.Straight);

        other = Arrays.asList(club2, diamond2, spades2, spades5, heart6, heart9, heartQ);
        equalsCompare(cardList, other, 1, CardType.ThreeOfKind);

        other = Arrays.asList(club4, diamond4, spades5, club5, heart6, heart9, heartQ);
        equalsCompare(cardList, other, 1, CardType.TwoPairs);

        other = Arrays.asList(club4, diamond4, spades5, club7, heart6, heart9, heartQ);
        equalsCompare(cardList, other, 1, CardType.OnePair);

        other = Arrays.asList(club4, diamond2, spades5, club7, heart6, heart9, heartQ);
        equalsCompare(cardList, other, 1, CardType.HighCard);
    }

    @Test
    public void testCompareFlush() {
        var cardList = Arrays.asList(diamondK, diamond10, diamondA, diamond3, diamond8, heart9, heart5);

        var other = Arrays.asList(club10, clubJ, clubQ, clubK, clubA, spades5, heart5);
        equalsCompare(cardList, other, -1, CardType.RoyalFlush);

        other = Arrays.asList(diamond10, diamondJ, diamondQ, diamond8, diamond9, spades5, heart5);
        equalsCompare(cardList, other, -1, CardType.StraightFlush);

        other = Arrays.asList(diamond10, club10, heart10, spades10, spadesK, spades5, heart5);
        equalsCompare(cardList, other, -1, CardType.FourOfKind);

        other = Arrays.asList(diamondJ, clubJ, heartJ, diamond9, club9, heart9, heart5);
        equalsCompare(cardList, other, -1, CardType.FullHouse);

        other = Arrays.asList(diamondK, diamond10, diamondA, diamondQ, diamond8, heart9, heart5);
        equalsCompare(cardList, other, -1, CardType.Flush);

        other = Arrays.asList(diamondK, diamond10, diamondA, diamond3, diamond8, heart9, heart5);
        equalsCompare(cardList, other, 0, CardType.Flush);

        other = Arrays.asList(diamondQ, diamond10, diamondA, diamond3, diamond8, heart9, heart5);
        equalsCompare(cardList, other, 1, CardType.Flush);

        other = Arrays.asList(club2, diamond3, spades4, spades5, heart6, heart9, heart5);
        equalsCompare(cardList, other, 1, CardType.Straight);

        other = Arrays.asList(club2, diamond2, spades2, spades5, heart6, heart9, heartQ);
        equalsCompare(cardList, other, 1, CardType.ThreeOfKind);

        other = Arrays.asList(club4, diamond4, spades5, club5, heart6, heart9, heartQ);
        equalsCompare(cardList, other, 1, CardType.TwoPairs);

        other = Arrays.asList(club4, diamond4, spades5, club7, heart6, heart9, heartQ);
        equalsCompare(cardList, other, 1, CardType.OnePair);

        other = Arrays.asList(club4, diamond2, spades5, club7, heart6, heart9, heartQ);
        equalsCompare(cardList, other, 1, CardType.HighCard);
    }

    @Test
    public void testCompareStraight() {
        var cardList = Arrays.asList(club7, diamond3, spades4, spades5, heart6, heart9, heart5);

        var other = Arrays.asList(club10, clubJ, clubQ, clubK, clubA, spades5, heart5);
        equalsCompare(cardList, other, -1, CardType.RoyalFlush);

        other = Arrays.asList(diamond10, diamondJ, diamondQ, diamond8, diamond9, spades5, heart5);
        equalsCompare(cardList, other, -1, CardType.StraightFlush);

        other = Arrays.asList(diamond10, club10, heart10, spades10, spadesK, spades5, heart5);
        equalsCompare(cardList, other, -1, CardType.FourOfKind);

        other = Arrays.asList(diamondJ, clubJ, heartJ, diamond9, club9, heart9, heart5);
        equalsCompare(cardList, other, -1, CardType.FullHouse);

        other = Arrays.asList(diamondK, diamond10, diamondA, diamondQ, diamond8, heart9, heart5);
        equalsCompare(cardList, other, -1, CardType.Flush);

        other = Arrays.asList(club7, diamond8, spades4, spades5, heart6, heart9, heart5);
        equalsCompare(cardList, other, -1, CardType.Straight);

        other = Arrays.asList(club7, diamond3, spades4, spades5, heart6, heart9, heart5);
        equalsCompare(cardList, other, 0, CardType.Straight);

        other = Arrays.asList(club2, diamond3, spades4, spades5, heart6, heart9, heart5);
        equalsCompare(cardList, other, 1, CardType.Straight);

        // A12345 < 34567
        other = Arrays.asList(spadesA, spades2, club3, spades4, spades5);
        equalsCompare(cardList, other, 1, CardType.Straight);

        other = Arrays.asList(club2, diamond2, spades2, spades5, heart6, heart9, heartQ);
        equalsCompare(cardList, other, 1, CardType.ThreeOfKind);

        other = Arrays.asList(club4, diamond4, spades5, club5, heart6, heart9, heartQ);
        equalsCompare(cardList, other, 1, CardType.TwoPairs);

        other = Arrays.asList(club4, diamond4, spades5, club7, heart6, heart9, heartQ);
        equalsCompare(cardList, other, 1, CardType.OnePair);

        other = Arrays.asList(club4, diamond2, spades5, club7, heart6, heart9, heartQ);
        equalsCompare(cardList, other, 1, CardType.HighCard);
    }

    @Test
    public void testCompareThreeOfKind() {
        var cardList = Arrays.asList(club3, diamond3, spades3, spades5, heart6, heart9, heartQ);

        var other = Arrays.asList(club10, clubJ, clubQ, clubK, clubA, spades5, heart5);
        equalsCompare(cardList, other, -1, CardType.RoyalFlush);

        other = Arrays.asList(diamond10, diamondJ, diamondQ, diamond8, diamond9, spades5, heart5);
        equalsCompare(cardList, other, -1, CardType.StraightFlush);

        other = Arrays.asList(diamond10, club10, heart10, spades10, spadesK, spades5, heart5);
        equalsCompare(cardList, other, -1, CardType.FourOfKind);

        other = Arrays.asList(diamondJ, clubJ, heartJ, diamond9, club9, heart9, heart5);
        equalsCompare(cardList, other, -1, CardType.FullHouse);

        other = Arrays.asList(diamondK, diamond10, diamondA, diamondQ, diamond8, heart9, heart5);
        equalsCompare(cardList, other, -1, CardType.Flush);

        other = Arrays.asList(club7, diamond8, spades4, spades5, heart6, heart9, heart5);
        equalsCompare(cardList, other, -1, CardType.Straight);

        other = Arrays.asList(club4, diamond4, spades4, spades5, heart6, heart9, heartJ);
        equalsCompare(cardList, other, -1, CardType.ThreeOfKind);

        other = Arrays.asList(club3, diamond3, spades3, spades5, heart6, heart9, heartK);
        equalsCompare(cardList, other, -1, CardType.ThreeOfKind);

        other = Arrays.asList(club3, diamond3, spades3, spades5, heart6, heart9, heartQ);
        equalsCompare(cardList, other, 0, CardType.ThreeOfKind);

        other = Arrays.asList(club3, diamond3, spades3, spades5, heart6, heart9, heart10);
        equalsCompare(cardList, other, 1, CardType.ThreeOfKind);

        other = Arrays.asList(club2, diamond2, spades2, spades5, heart6, heart9, heartQ);
        equalsCompare(cardList, other, 1, CardType.ThreeOfKind);

        other = Arrays.asList(club4, diamond4, spades5, club5, heart6, heart9, heartQ);
        equalsCompare(cardList, other, 1, CardType.TwoPairs);

        other = Arrays.asList(club4, diamond4, spades5, club7, heart6, heart9, heartQ);
        equalsCompare(cardList, other, 1, CardType.OnePair);

        other = Arrays.asList(club4, diamond2, spades5, club7, heart6, heart9, heartQ);
        equalsCompare(cardList, other, 1, CardType.HighCard);

        // 三张牌
        cardList = Arrays.asList(club3, diamond3, spades3);

        other = Arrays.asList(club3, diamond3, spades3, spades5, heart6, heart9, heart7);
        equalsCompare(cardList, other, -1, CardType.ThreeOfKind);

        other = Arrays.asList(club3, diamond3, spades3);
        equalsCompare(cardList, other, 0, CardType.ThreeOfKind);

        other = Arrays.asList(club2, diamond2, spades2, spades5, heart6, heart9, heartQ);
        equalsCompare(cardList, other, 1, CardType.ThreeOfKind);

        other = Arrays.asList(club4, diamond4, spades5, club5, heart6, heart9, heartQ);
        equalsCompare(cardList, other, 1, CardType.TwoPairs);

        other = Arrays.asList(club4, diamond4, spades5, club7, heart6, heart9, heartQ);
        equalsCompare(cardList, other, 1, CardType.OnePair);

        other = Arrays.asList(club4, diamond2, spades5, club7, heart6, heart9, heartQ);
        equalsCompare(cardList, other, 1, CardType.HighCard);
    }

    @Test
    public void testCompareTwoPairs() {
        var cardList = Arrays.asList(club4, diamond4, spades5, club5, heart6, heart9, heartQ);

        var other = Arrays.asList(club10, clubJ, clubQ, clubK, clubA, spades5, heart5);
        equalsCompare(cardList, other, -1, CardType.RoyalFlush);

        other = Arrays.asList(diamond10, diamondJ, diamondQ, diamond8, diamond9, spades5, heart5);
        equalsCompare(cardList, other, -1, CardType.StraightFlush);

        other = Arrays.asList(diamond10, club10, heart10, spades10, spadesK, spades5, heart5);
        equalsCompare(cardList, other, -1, CardType.FourOfKind);

        other = Arrays.asList(diamondJ, clubJ, heartJ, diamond9, club9, heart9, heart5);
        equalsCompare(cardList, other, -1, CardType.FullHouse);

        other = Arrays.asList(diamondK, diamond10, diamondA, diamondQ, diamond8, heart9, heart5);
        equalsCompare(cardList, other, -1, CardType.Flush);

        other = Arrays.asList(club7, diamond8, spades4, spades5, heart6, heart9, heart5);
        equalsCompare(cardList, other, -1, CardType.Straight);

        other = Arrays.asList(club4, diamond4, spades4, spades5, heart6, heart9, heartJ);
        equalsCompare(cardList, other, -1, CardType.ThreeOfKind);

        other = Arrays.asList(club7, diamond7, spades5, club6, heart6, heart9, heartQ);
        equalsCompare(cardList, other, -1, CardType.TwoPairs);

        other = Arrays.asList(club4, diamond4, spades5, club6, heart6, heart9, heartQ);
        equalsCompare(cardList, other, -1, CardType.TwoPairs);

        other = Arrays.asList(club4, diamond4, spades5, club5, heart6, heart9, heartK);
        equalsCompare(cardList, other, -1, CardType.TwoPairs);

        other = Arrays.asList(club4, diamond4, spades5, club5, heart6, heart9, heartQ);
        equalsCompare(cardList, other, 0, CardType.TwoPairs);

        other = Arrays.asList(club4, diamond4, spades5, club5, heart6, heart9, heartJ);
        equalsCompare(cardList, other, 1, CardType.TwoPairs);

        other = Arrays.asList(club3, diamond3, spades5, club5, heart6, heart9, heartQ);
        equalsCompare(cardList, other, 1, CardType.TwoPairs);

        other = Arrays.asList(club3, diamond3, spades2, club2, heart6, heart9, heartQ);
        equalsCompare(cardList, other, 1, CardType.TwoPairs);

        other = Arrays.asList(club4, diamond4, spades5, club7, heart6, heart9, heartQ);
        equalsCompare(cardList, other, 1, CardType.OnePair);

        other = Arrays.asList(club4, diamond2, spades5, club7, heart6, heart9, heartQ);
        equalsCompare(cardList, other, 1, CardType.HighCard);
    }

    @Test
    public void testCompareOnePair() {
        var cardList = Arrays.asList(club4, diamond4, spades5, club7, heart6, heart9, heartQ);

        var other = Arrays.asList(club10, clubJ, clubQ, clubK, clubA, spades5, heart5);
        equalsCompare(cardList, other, -1, CardType.RoyalFlush);

        other = Arrays.asList(diamond10, diamondJ, diamondQ, diamond8, diamond9, spades5, heart5);
        equalsCompare(cardList, other, -1, CardType.StraightFlush);

        other = Arrays.asList(diamond10, club10, heart10, spades10, spadesK, spades5, heart5);
        equalsCompare(cardList, other, -1, CardType.FourOfKind);

        other = Arrays.asList(diamondJ, clubJ, heartJ, diamond9, club9, heart9, heart5);
        equalsCompare(cardList, other, -1, CardType.FullHouse);

        other = Arrays.asList(diamondK, diamond10, diamondA, diamondQ, diamond8, heart9, heart5);
        equalsCompare(cardList, other, -1, CardType.Flush);

        other = Arrays.asList(club7, diamond8, spades4, spades5, heart6, heart9, heart5);
        equalsCompare(cardList, other, -1, CardType.Straight);

        other = Arrays.asList(club4, diamond4, spades4, spades5, heart6, heart9, heartJ);
        equalsCompare(cardList, other, -1, CardType.ThreeOfKind);

        other = Arrays.asList(club4, diamond4, spades5, club5, heart6, heart9, heartQ);
        equalsCompare(cardList, other, -1, CardType.TwoPairs);

        other = Arrays.asList(club4, diamond4, spades5, club7, heart6, heart9, heartK);
        equalsCompare(cardList, other, -1, CardType.OnePair);

        other = Arrays.asList(club4, diamond4, spades5, club7, heart6, heart10, heartQ);
        equalsCompare(cardList, other, -1, CardType.OnePair);

        other = Arrays.asList(club4, diamond4, spades5, club8, heart6, heart9, heartQ);
        equalsCompare(cardList, other, -1, CardType.OnePair);

        other = Arrays.asList(club4, diamond4, spades5, club7, heart6, heart9, heartQ);
        equalsCompare(cardList, other, 0, CardType.OnePair);

        other = Arrays.asList(club4, diamond4, spades5, club7, heart6, heart9, heartJ);
        equalsCompare(cardList, other, 1, CardType.OnePair);

        other = Arrays.asList(club4, diamond4, spades5, club7, heart6, heart2, heartQ);
        equalsCompare(cardList, other, 1, CardType.OnePair);

        other = Arrays.asList(club4, diamond4, spades5, club3, heart6, heart8, heartQ);
        equalsCompare(cardList, other, 1, CardType.OnePair);

        other = Arrays.asList(club3, diamond3, spades5, club8, heart6, heart9, heartK);
        equalsCompare(cardList, other, 1, CardType.OnePair);

        other = Arrays.asList(club4, diamond2, spades5, club7, heart6, heart9, heartQ);
        equalsCompare(cardList, other, 1, CardType.HighCard);
    }

    @Test
    public void testCompareHighCard() {
        var cardList = Arrays.asList(club4, diamond2, spades5, club7, heart6, heart9, heartQ);

        var other = Arrays.asList(club10, clubJ, clubQ, clubK, clubA, spades5, heart5);
        equalsCompare(cardList, other, -1, CardType.RoyalFlush);

        other = Arrays.asList(diamond10, diamondJ, diamondQ, diamond8, diamond9, spades5, heart5);
        equalsCompare(cardList, other, -1, CardType.StraightFlush);

        other = Arrays.asList(diamond10, club10, heart10, spades10, spadesK, spades5, heart5);
        equalsCompare(cardList, other, -1, CardType.FourOfKind);

        other = Arrays.asList(diamondJ, clubJ, heartJ, diamond9, club9, heart9, heart5);
        equalsCompare(cardList, other, -1, CardType.FullHouse);

        other = Arrays.asList(diamondK, diamond10, diamondA, diamondQ, diamond8, heart9, heart5);
        equalsCompare(cardList, other, -1, CardType.Flush);

        other = Arrays.asList(club7, diamond8, spades4, spades5, heart6, heart9, heart5);
        equalsCompare(cardList, other, -1, CardType.Straight);

        other = Arrays.asList(club4, diamond4, spades4, spades5, heart6, heart9, heartJ);
        equalsCompare(cardList, other, -1, CardType.ThreeOfKind);

        other = Arrays.asList(club7, diamond7, spades5, club6, heart6, heart9, heartQ);
        equalsCompare(cardList, other, -1, CardType.TwoPairs);

        other = Arrays.asList(club4, diamond4, spades5, club7, heart6, heart9, heartQ);
        equalsCompare(cardList, other, -1, CardType.OnePair);

        other = Arrays.asList(club4, diamond2, spades5, club7, heart6, heart9, heartK);
        equalsCompare(cardList, other, -1, CardType.HighCard);

        other = Arrays.asList(club4, diamond2, spades5, club7, heart6, heartJ, heartQ);
        equalsCompare(cardList, other, -1, CardType.HighCard);

        other = Arrays.asList(club4, diamond2, spades5, club7, heart6, heart9, heartQ);
        equalsCompare(cardList, other, 0, CardType.HighCard);

        other = Arrays.asList(club4, diamond2, spades5, club7, heart6, heart9, heartJ);
        equalsCompare(cardList, other, 1, CardType.HighCard);

        other = Arrays.asList(club4, diamond2, spades3, club7, heart6, heart9, heartQ);
        equalsCompare(cardList, other, 1, CardType.HighCard);
    }

    private void equalsCompare(Collection<Card> a, Collection<Card> b, int result, CardType type) {
        var bb = Hand.typeOf(b);
        assertEquals(type, bb.getType());
        assertEquals(result, Hand.compare(Hand.typeOf(a), bb));
    }

    @Test
    public void testKeysOf() {
        var cardList = Arrays.asList(diamond10, diamondJ, diamondQ, diamondK, diamondA);
        equalsKeysOf(cardList, CardType.RoyalFlush, cardList);

        cardList = Arrays.asList(diamond10, diamond9, diamondQ, diamondK, diamondJ);
        equalsKeysOf(cardList, CardType.StraightFlush, cardList);

        cardList = Arrays.asList(diamond10, club10, heart10, spadesK, spades10);
        equalsKeysOf(cardList, CardType.FourOfKind, Arrays.asList(diamond10, club10, heart10, spades10));

        cardList = Arrays.asList(diamond10, club10, heart10, diamond9, club9);
        equalsKeysOf(cardList, CardType.FullHouse, cardList);

        cardList = Arrays.asList(diamondK, diamond10, diamondA, diamond3, diamond8 );
        equalsKeysOf(cardList, CardType.Flush, cardList);

        cardList = Arrays.asList(club2, diamond3, spades4, spades5, heart6 );
        equalsKeysOf(cardList, CardType.Straight, cardList);

        cardList = Arrays.asList(club2, diamond2, spades2, heart9, heartQ);
        equalsKeysOf(cardList, CardType.ThreeOfKind, Arrays.asList(club2, diamond2, spades2));

        cardList = Arrays.asList(club2, diamond2, spades2);
        equalsKeysOf(cardList, CardType.ThreeOfKind, Arrays.asList(club2, diamond2, spades2));

        cardList = Arrays.asList(club4, diamond4, spades5, club5, heart6);
        equalsKeysOf(cardList, CardType.TwoPairs, Arrays.asList(club4, diamond4, spades5, club5));

        cardList = Arrays.asList(club4, diamond4, heartQ, heart9, club7 );
        equalsKeysOf(cardList, CardType.OnePair, Arrays.asList(club4, diamond4));

        cardList = Arrays.asList(club4, diamond4, heartQ);
        equalsKeysOf(cardList, CardType.OnePair, Arrays.asList(club4, diamond4));

        cardList = Arrays.asList(club4, diamond4);
        equalsKeysOf(cardList, CardType.OnePair, cardList);

        cardList = Arrays.asList(heartQ, heart9, spades5, club7, heart6);
        equalsKeysOf(cardList, CardType.HighCard, Arrays.asList(heartQ));

        cardList = Arrays.asList(heartQ, heart9, spades5);
        equalsKeysOf(cardList, CardType.HighCard, Arrays.asList(heartQ));
    }

    private void equalsKeysOf(Collection<Card> cardList, CardType type, Collection<Card> expect) {
        var keys = Hand.keysOf(cardList, type);
        var set = new HashSet<>(keys);
        assertEquals(expect.size(), set.size());
        assertTrue(set.containsAll(expect));
    }
}
