package com.texasthree.core;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * Poker Tester.
 *
 * @author <Authors name>
 * @version 1.0
 * @since <pre>11un 16, 2020</pre>
 */
public class PokerTest {

    private static AllCard c;

    @BeforeAll
    public static void before() {
        c = AllCard.getInstance();
    }

    /**
     * Method: typeOf(List<Card> list)
     */
    @Test
    public void testTypeOf() throws Exception {
        this.testTypeRoyalFlush();
        this.testTypeStraightFlush();
        this.testTypeFourOf13ind();
        this.testTypeFullHouse();
        this.testTypeFlush();
        this.testTypeStraight();
        this.testTypeThreeOf13ind();
        this.testTypeTwoPairs();
        this.testTypeOnePair();
        this.testTypeHighCard();
    }

    @Test
    public void compare() throws Exception {
        this.testCompareRoyalFlush();
        this.testCompareStraightFlush();
        this.testCompareFourOfKind();
        this.testCompareFourOfKind();
        this.testCompareFullHouse();
        this.testCompareFlush();
        this.testCompareStraight();
        this.testCompareThreeOfKind();
        this.testCompareTwoPairs();
        this.testCompareOnePair();
        this.testCompareHighCard();
    }

    /**
     * 比牌：皇家同花顺
     */
    private void testCompareRoyalFlush() {

        Hand main = Poker.typeOf(Arrays.asList(c.diamond10, c.diamondJ, c.diamondQ, c.diamondK, c.diamondA, c.spades5, c.heart5));

        // 皇家同花顺
        assertCompare(main, 0, Arrays.asList(c.club10, c.clubJ, c.clubQ, c.clubK, c.clubA, c.spades5, c.heart5));

        // 同花顺
        assertCompare(main, 1, Arrays.asList(c.diamond10, c.diamondJ, c.diamondQ, c.diamondK, c.diamond9, c.spades5, c.heart5));

        // 金刚
        assertCompare(main, 1, Arrays.asList(c.diamond10, c.club10, c.heart10, c.spades10, c.spadesK, c.spades5, c.heart5));

        // 葫芦
        assertCompare(main, 1, Arrays.asList(c.diamondK, c.diamond10, c.diamondA, c.diamond3, c.diamond8, c.heart9, c.heart5));

        // 同花
        assertCompare(main, 1, Arrays.asList(c.diamond10, c.club10, c.heart10, c.spades10, c.spadesK, c.spades5, c.heart5));

        // 顺子
        assertCompare(main, 1, Arrays.asList(c.club2, c.diamond3, c.spades4, c.spades5, c.heart6, c.heart9, c.heart5));

        // 三张
        assertCompare(main, 1, Arrays.asList(c.club2, c.diamond2, c.spades2, c.spades5, c.heart6, c.heart9, c.heartQ));

        // 两对
        assertCompare(main, 1, Arrays.asList(c.club4, c.diamond4, c.spades5, c.club5, c.heart6, c.heart9, c.heartQ));

        // 一对
        assertCompare(main, 1, Arrays.asList(c.club4, c.diamond4, c.spades5, c.club7, c.heart6, c.heart9, c.heartQ));

        // 高牌
        assertCompare(main, 1, Arrays.asList(c.club4, c.diamond2, c.spades5, c.club7, c.heart6, c.heart9, c.heartQ));
    }

    /**
     * 比牌：同花顺
     */
    private void testCompareStraightFlush() {
        Hand main = Poker.typeOf(Arrays.asList(c.diamond10, c.diamondJ, c.diamondQ, c.diamond8, c.diamond9, c.spades5, c.heart5));

        // 同花顺
        assertCompare(main, 1, Arrays.asList(c.spadesA, c.spades2, c.spades3, c.spades4, c.spades5));
        assertCompare(main, -1, Arrays.asList(c.diamond10, c.diamondJ, c.diamondQ, c.diamondK, c.diamond9, c.spades5, c.heart5));
        assertCompare(main, -1, Arrays.asList(c.club10, c.clubJ, c.clubQ, c.clubK, c.clubA, c.spades5, c.heart5));
        assertCompare(main, 0, Arrays.asList(c.diamond10, c.diamondJ, c.diamondQ, c.diamond8, c.diamond9, c.spades5, c.heart5));
        // 金刚
        assertCompare(main, 1, Arrays.asList(c.diamond10, c.club10, c.heart10, c.spades10, c.spadesK, c.spades5, c.heart5));
        // 葫芦
        assertCompare(main, 1, Arrays.asList(c.diamond10, c.club10, c.heart10, c.diamond9, c.club9, c.heart9, c.heart5));
        // 同花
        assertCompare(main, 1, Arrays.asList(c.diamondK, c.diamond10, c.diamondA, c.diamond3, c.diamond8, c.heart9, c.heart5));
        // 顺子
        assertCompare(main, 1, Arrays.asList(c.club2, c.diamond3, c.spades4, c.spades5, c.heart6, c.heart9, c.heart5));
        // 三条
        assertCompare(main, 1, Arrays.asList(c.club2, c.diamond2, c.spades2, c.spades5, c.heart6, c.heart9, c.heartQ));
        // 两对
        assertCompare(main, 1, Arrays.asList(c.club4, c.diamond4, c.spades5, c.club5, c.heart6, c.heart9, c.heartQ));
        // 一对
        assertCompare(main, 1, Arrays.asList(c.club4, c.diamond4, c.spades5, c.club7, c.heart6, c.heart9, c.heartQ));
        // 高牌
        assertCompare(main, 1, Arrays.asList(c.club4, c.diamond2, c.spades5, c.club7, c.heart6, c.heart9, c.heartQ));
    }

    /**
     * 比牌：金刚
     */
    private void testCompareFourOfKind() {
        Hand main = Poker.typeOf(Arrays.asList(c.diamond10, c.club10, c.heart10, c.spades10, c.spadesK, c.spades5, c.heart5));

        // 金刚
        assertCompare(main, 1, Arrays.asList(c.diamond10, c.club10, c.heart10, c.spades10, c.spadesQ, c.spades5, c.heart5));
        assertCompare(main, -1, Arrays.asList(c.club10, c.clubJ, c.clubQ, c.clubK, c.clubA, c.spades5, c.heart5));
        assertCompare(main, -1, Arrays.asList(c.diamond10, c.diamondJ, c.diamondQ, c.diamondK, c.diamond9, c.spades5, c.heart5));
        assertCompare(main, 0, Arrays.asList(c.diamond10, c.club10, c.heart10, c.spades10, c.spadesK, c.spades5, c.heart5));

        // 葫芦
        assertCompare(main, 1, Arrays.asList(c.diamond10, c.club10, c.heart10, c.diamond9, c.club9, c.heart9, c.heart5));
        // 同花
        assertCompare(main, 1, Arrays.asList(c.diamondK, c.diamond10, c.diamondA, c.diamond3, c.diamond8, c.heart9, c.heart5));
        // 顺子
        assertCompare(main, 1, Arrays.asList(c.club2, c.diamond3, c.spades4, c.spades5, c.heart6, c.heart9, c.heart5));
        //  三条
        assertCompare(main, 1, Arrays.asList(c.club2, c.diamond2, c.spades2, c.spades5, c.heart6, c.heart9, c.heartQ));
        // 两对
        assertCompare(main, 1, Arrays.asList(c.club4, c.diamond4, c.spades5, c.club5, c.heart6, c.heart9, c.heartQ));
        // 一对
        assertCompare(main, 1, Arrays.asList(c.club4, c.diamond4, c.spades5, c.club7, c.heart6, c.heart9, c.heartQ));
        // 高牌
        assertCompare(main, 1, Arrays.asList(c.club4, c.diamond2, c.spades5, c.club7, c.heart6, c.heart9, c.heartQ));
    }

    /**
     * 比牌：葫芦
     */
    private void testCompareFullHouse() {
        Hand main = Poker.typeOf(Arrays.asList(c.diamond10, c.club10, c.heart10, c.diamond9, c.club9, c.heart9, c.heart5));

        // 葫芦
        assertCompare(main, 1, Arrays.asList(c.diamond10, c.club10, c.heart10, c.diamond8, c.club8, c.heart9, c.heart5));
        assertCompare(main, -1, Arrays.asList(c.club10, c.clubJ, c.clubQ, c.clubK, c.clubA, c.spades5, c.heart5));
        assertCompare(main, -1, Arrays.asList(c.diamond10, c.diamondJ, c.diamondQ, c.diamondK, c.diamond9, c.spades5, c.heart5));
        assertCompare(main, -1, Arrays.asList(c.diamond10, c.club10, c.heart10, c.spades10, c.spadesK, c.spades5, c.heart5));
        assertCompare(main, 0, Arrays.asList(c.diamond10, c.club10, c.heart10, c.diamond9, c.club9, c.heart9, c.heart5));

        Hand m1 = Poker.typeOf(Arrays.asList(c.heart8, c.diamond8, c.heart6, c.spades5, c.club5, c.heart5, c.heart3));
        assertCompare(m1, -1, Arrays.asList(c.club6, c.diamond6, c.heart6, c.spades5, c.club5, c.heart5, c.heart3));

        // 同花
        assertCompare(main, 1, Arrays.asList(c.diamondK, c.diamond10, c.diamondA, c.diamond3, c.diamond8, c.heart9, c.heart5));
        // min = 6 -> 同花大于葫芦
        assertCompare(main, -1, Arrays.asList(c.diamondK, c.diamond10, c.diamondA, c.diamond3, c.diamond8, c.heart9, c.heart5), 6);
        assertCompare(main, -1, Arrays.asList(c.heart4, c.heart7, c.heart6, c.heart9, c.heartQ), 6);

        // 顺子
        assertCompare(main, 0, Arrays.asList(c.club2, c.diamond3, c.spades4, c.spades5, c.heart6, c.heart9, c.heart5));
        // 三条
        assertCompare(main, 0, Arrays.asList(c.club2, c.diamond2, c.spades2, c.spades5, c.heart6, c.heart9, c.heartQ));
        // 两对
        assertCompare(main, 0, Arrays.asList(c.club4, c.diamond4, c.spades5, c.club5, c.heart6, c.heart9, c.heartQ));
        // 一对
        assertCompare(main, 0, Arrays.asList(c.club4, c.diamond4, c.spades5, c.club7, c.heart6, c.heart9, c.heartQ));
        // 高牌
        assertCompare(main, 0, Arrays.asList(c.club4, c.diamond2, c.spades5, c.club7, c.heart6, c.heart9, c.heartQ));
    }

    /**
     * 比牌：同花
     */
    private void testCompareFlush() {
        Hand main = Poker.typeOf(Arrays.asList(c.diamondK, c.diamond10, c.diamondA, c.diamond3, c.diamond8, c.heart9, c.heart5));
        assertEquals(CardType.Flush, main.getType());

        // 同花
        assertCompare(main, -1, Arrays.asList(c.diamondK, c.diamond10, c.diamondA, c.diamond3, c.diamond9, c.heart10, c.heart5));
        assertCompare(main, -1, Arrays.asList(c.club10, c.clubJ, c.clubQ, c.clubK, c.clubA, c.spades5, c.heart5));
        assertCompare(main, -1, Arrays.asList(c.diamond10, c.diamondJ, c.diamondQ, c.diamondK, c.diamond9, c.spades5, c.heart5));
        assertCompare(main, -1, Arrays.asList(c.diamond10, c.club10, c.heart10, c.spades10, c.spadesK, c.spades5, c.heart5));
        assertCompare(main, -1, Arrays.asList(c.diamond10, c.club10, c.heart10, c.diamond9, c.club8, c.heart9, c.heart5));
        assertCompare(main, 0, Arrays.asList(c.diamondK, c.diamond10, c.diamondA, c.diamond3, c.diamond8, c.heart9, c.heart5));
        // 顺子
        assertCompare(main, 1, Arrays.asList(c.club2, c.diamond3, c.spades4, c.spades5, c.heart6, c.heart9, c.heart5));
        // 三条
        assertCompare(main, 1, Arrays.asList(c.club2, c.diamond2, c.spades2, c.spades5, c.heart6, c.heart9, c.heartQ));
        // 两对
        assertCompare(main, 1, Arrays.asList(c.club4, c.diamond4, c.spades5, c.club5, c.heart6, c.heart9, c.heartQ));
        // 一对
        assertCompare(main, 1, Arrays.asList(c.club4, c.diamond4, c.spades5, c.club7, c.heart6, c.heart9, c.heartQ));
        // 高牌
        assertCompare(main, 1, Arrays.asList(c.club4, c.diamond2, c.spades5, c.club7, c.heart6, c.heart9, c.heartQ));
    }

    /**
     * 比牌：顺子
     */
    private void testCompareStraight() {
        Hand main = Poker.typeOf(Arrays.asList(c.club7, c.diamond3, c.spades4, c.spades5, c.heart6, c.heart9, c.heart5));
        assertEquals(CardType.Straight, main.getType());

        // 顺子
        // A12345 < 34567
        assertCompare(main, 1, Arrays.asList(c.spadesA, c.spades2, c.club3, c.spades4, c.spades5));
        assertCompare(main, 1, Arrays.asList(c.club2, c.diamond3, c.spades4, c.spades5, c.heart6, c.heart9, c.heart5));
        assertCompare(main, -1, Arrays.asList(c.club10, c.clubJ, c.clubQ, c.clubK, c.clubA, c.spades5, c.heart5));
        assertCompare(main, -1, Arrays.asList(c.diamond10, c.diamondJ, c.diamondQ, c.diamondK, c.diamond9, c.spades5, c.heart5));
        assertCompare(main, -1, Arrays.asList(c.diamond10, c.club10, c.heart10, c.spades10, c.spadesK, c.spades5, c.heart5));
        assertCompare(main, -1, Arrays.asList(c.diamond10, c.club10, c.heart10, c.diamond9, c.club8, c.heart9, c.heart5));
        assertCompare(main, -1, Arrays.asList(c.diamondK, c.diamond10, c.diamondA, c.diamond3, c.diamond8, c.heart9, c.heart5));
        assertCompare(main, 0, Arrays.asList(c.club7, c.diamond3, c.spades4, c.spades5, c.heart6, c.heart9, c.heart5));
        // 三条
        assertCompare(main, 1, Arrays.asList(c.club2, c.diamond2, c.spades2, c.spades5, c.heart6, c.heart9, c.heartQ));
        // 两对
        assertCompare(main, 1, Arrays.asList(c.club4, c.diamond4, c.spades5, c.club5, c.heart6, c.heart9, c.heartQ));
        // 一对
        assertCompare(main, 1, Arrays.asList(c.club4, c.diamond4, c.spades5, c.club7, c.heart6, c.heart9, c.heartQ));
        // 高牌
        assertCompare(main, 1, Arrays.asList(c.club4, c.diamond2, c.spades5, c.club7, c.heart6, c.heart9, c.heartQ));

    }

    /**
     * 比牌：三条
     */
    private void testCompareThreeOfKind() {
        Hand main = Poker.typeOf(Arrays.asList(c.club3, c.diamond3, c.spades3, c.spades5, c.heart6, c.heart9, c.heartQ));
        assertEquals(CardType.ThreeOfKind, main.getType());

        // 三条
        assertCompare(main, 1, Arrays.asList(c.club2, c.diamond2, c.spades2, c.spades5, c.heart6, c.heart9, c.heartQ));
        assertCompare(main, -1, Arrays.asList(c.club10, c.clubJ, c.clubQ, c.clubK, c.clubA, c.spades5, c.heart5));
        assertCompare(main, -1, Arrays.asList(c.diamond10, c.diamondJ, c.diamondQ, c.diamondK, c.diamond9, c.spades5, c.heart5));
        assertCompare(main, -1, Arrays.asList(c.diamond10, c.club10, c.heart10, c.spades10, c.spadesK, c.spades5, c.heart5));
        assertCompare(main, -1, Arrays.asList(c.diamond10, c.club10, c.heart10, c.diamond9, c.club8, c.heart9, c.heart5));
        assertCompare(main, -1, Arrays.asList(c.diamondK, c.diamond10, c.diamondA, c.diamond3, c.diamond8, c.heart9, c.heart5));
        assertCompare(main, -1, Arrays.asList(c.club7, c.diamond3, c.spades4, c.spades5, c.heart6, c.heart9, c.heart5));
        assertCompare(main, 0, Arrays.asList(c.club3, c.diamond3, c.spades3, c.spades5, c.heart6, c.heart9, c.heartQ));
        // 两对
        assertCompare(main, 1, Arrays.asList(c.club4, c.diamond4, c.spades5, c.club5, c.heart6, c.heart9, c.heartQ));
        // 一对
        assertCompare(main, 1, Arrays.asList(c.club4, c.diamond4, c.spades5, c.club7, c.heart6, c.heart9, c.heartQ));
        // 高牌
        assertCompare(main, 1, Arrays.asList(c.club4, c.diamond2, c.spades5, c.club7, c.heart6, c.heart9, c.heartQ));

        // 三张牌
        main = Poker.typeOf(Arrays.asList(c.club3, c.diamond3, c.spades3));
        assertCompare(main, 1, Arrays.asList(c.club4, c.diamond2, c.spades5, c.club7, c.heart6, c.heart9, c.heartQ));
        assertCompare(main, 1, Arrays.asList(c.club4, c.diamond4, c.spades5));
        assertCompare(main, 1, Arrays.asList(c.club4, c.diamond4, c.spades5, c.club7, c.heart6, c.heart9, c.heartQ));
        assertCompare(main, 1, Arrays.asList(c.club2, c.diamond2, c.spades2));
        assertCompare(main, 1, Arrays.asList(c.club2, c.diamond2, c.spades2, c.club7, c.heart6, c.heart9, c.heartQ));
        assertCompare(main, -1, Arrays.asList(c.club4, c.diamond4, c.spades4, c.club7, c.heart6, c.heart9, c.heartQ));
        assertCompare(main, -1, Arrays.asList(c.club4, c.diamond4, c.spades4));

    }

    /**
     * 比牌：两对
     */
    private void testCompareTwoPairs() {
        Hand main = Poker.typeOf(Arrays.asList(c.club4, c.diamond4, c.spades5, c.club5, c.heart6, c.heart9, c.heartQ));
        assertEquals(CardType.TwoPairs, main.getType());

        // 皇家同花顺
        assertCompare(main, -1, Arrays.asList(c.club10, c.clubJ, c.clubQ, c.clubK, c.clubA, c.spades5, c.heart5));
        // 同花顺
        assertCompare(main, -1, Arrays.asList(c.diamond10, c.diamondJ, c.diamondQ, c.diamondK, c.diamond9, c.spades5, c.heart5));
        // 金刚
        assertCompare(main, -1, Arrays.asList(c.diamond10, c.club10, c.heart10, c.spades10, c.spadesK, c.spades5, c.heart5));
        // 葫芦
        assertCompare(main, -1, Arrays.asList(c.diamond10, c.club10, c.heart10, c.diamond9, c.club8, c.heart9, c.heart5));
        // 同花
        assertCompare(main, -1, Arrays.asList(c.diamondK, c.diamond10, c.diamondA, c.diamond3, c.diamond8, c.heart9, c.heart5));
        // 顺子
        assertCompare(main, -1, Arrays.asList(c.club7, c.diamond3, c.spades4, c.spades5, c.heart6, c.heart9, c.heart5));
        // 三条
        assertCompare(main, -1, Arrays.asList(c.club3, c.diamond3, c.spades3, c.spades5, c.heart6, c.heart9, c.heartQ));
        // 两对
        assertCompare(main, 1, Arrays.asList(c.club4, c.diamond4, c.spades5, c.club5, c.heart6, c.heart9, c.heartJ));
        assertCompare(main, 0, Arrays.asList(c.club4, c.diamond4, c.spades5, c.club5, c.heart6, c.heart9, c.heartQ));
        // 一对
        assertCompare(main, 1, Arrays.asList(c.club4, c.diamond4, c.spades5, c.club7, c.heart6, c.heart9, c.heartQ));
        // 高牌
        assertCompare(main, 1, Arrays.asList(c.club4, c.diamond2, c.spades5, c.club7, c.heart6, c.heart9, c.heartQ));

    }

    /**
     * 比牌：两对
     */
    private void testCompareOnePair() {
        Hand main = Poker.typeOf(Arrays.asList(c.club4, c.diamond4, c.spades5, c.club7, c.heart6, c.heart9, c.heartQ));
        assertEquals(CardType.OnePair, main.getType());

        assertCompare(main, 1, Arrays.asList(c.club4, c.diamond4, c.spades5, c.club7, c.heart6, c.heart9, c.heartJ));
        assertCompare(main, -1, Arrays.asList(c.club10, c.clubJ, c.clubQ, c.clubK, c.clubA, c.spades5, c.heart5));
        assertCompare(main, -1, Arrays.asList(c.diamond10, c.diamondJ, c.diamondQ, c.diamondK, c.diamond9, c.spades5, c.heart5));
        assertCompare(main, -1, Arrays.asList(c.diamond10, c.club10, c.heart10, c.spades10, c.spadesK, c.spades5, c.heart5));
        assertCompare(main, -1, Arrays.asList(c.diamond10, c.club10, c.heart10, c.diamond9, c.club8, c.heart9, c.heart5));
        assertCompare(main, -1, Arrays.asList(c.diamondK, c.diamond10, c.diamondA, c.diamond3, c.diamond8, c.heart9, c.heart5));
        assertCompare(main, -1, Arrays.asList(c.club7, c.diamond3, c.spades4, c.spades5, c.heart6, c.heart9, c.heart5));
        assertCompare(main, -1, Arrays.asList(c.club3, c.diamond3, c.spades3, c.spades5, c.heart6, c.heart9, c.heartQ));
        assertCompare(main, -1, Arrays.asList(c.club4, c.diamond4, c.spades5, c.club5, c.heart6, c.heart9, c.heartQ));
        assertCompare(main, 0, Arrays.asList(c.club4, c.diamond4, c.spades5, c.club7, c.heart6, c.heart9, c.heartQ));
        assertCompare(main, 1, Arrays.asList(c.club4, c.diamond2, c.spades5, c.club7, c.heart6, c.heart9, c.heartQ));

        // 三张牌
        assertCompare(main, 1, Arrays.asList(c.heart4, c.spades4, c.heart5));
        assertCompare(Poker.typeOf(Arrays.asList(c.heart4, c.spades4, c.heart5)), -1, Arrays.asList(c.club4, c.diamond4, c.spades5, c.club7, c.heart6, c.heart9, c.heartQ));
    }

    /**
     * 比牌：高牌
     */
    private void testCompareHighCard() {
        Hand main = Poker.typeOf(Arrays.asList(c.club4, c.diamond2, c.spades5, c.club7, c.heart6, c.heart9, c.heartQ));
        assertEquals(CardType.HighCard, main.getType());

        assertCompare(main, 1, Arrays.asList(c.club4, c.diamond2, c.spades5, c.club7, c.heart6, c.heart9, c.heartJ));
        assertCompare(main, -1, Arrays.asList(c.club10, c.clubJ, c.clubQ, c.clubK, c.clubA, c.spades5, c.heart5));
        assertCompare(main, -1, Arrays.asList(c.diamond10, c.diamondJ, c.diamondQ, c.diamondK, c.diamond9, c.spades5, c.heart5));
        assertCompare(main, -1, Arrays.asList(c.diamond10, c.club10, c.heart10, c.spades10, c.spadesK, c.spades5, c.heart5));
        assertCompare(main, -1, Arrays.asList(c.diamond10, c.club10, c.heart10, c.diamond9, c.club8, c.heart9, c.heart5));
        assertCompare(main, -1, Arrays.asList(c.diamondK, c.diamond10, c.diamondA, c.diamond3, c.diamond8, c.heart9, c.heart5));
        assertCompare(main, -1, Arrays.asList(c.club3, c.diamond3, c.spades3, c.spades5, c.heart6, c.heart9, c.heartQ));
        assertCompare(main, -1, Arrays.asList(c.club4, c.diamond4, c.spades5, c.club5, c.heart6, c.heart9, c.heartQ));
        assertCompare(main, -1, Arrays.asList(c.club4, c.diamond4, c.spades5, c.club7, c.heart6, c.heart9, c.heartQ));
        assertCompare(main, 0, Arrays.asList(c.club4, c.diamond2, c.spades5, c.club7, c.heart6, c.heart9, c.heartQ));

        // 三张牌
        main = Poker.typeOf(Arrays.asList(c.club4, c.heart9, c.heartQ));
        assertEquals(CardType.HighCard, main.getType());

        assertCompare(main, 1, Arrays.asList(c.diamond2, c.spades5, c.club7));
        assertCompare(main, 1, Arrays.asList(c.club4, c.spades9, c.spadesQ));
        assertCompare(main, 1, Arrays.asList(c.club4, c.spades9, c.spadesQ, c.club3, c.club2));

        main = Poker.typeOf(Arrays.asList(c.heart5, c.clubK, c.heart4));
        assertEquals(CardType.HighCard, main.getType());

        assertCompare(main, 1, Arrays.asList(c.club8, c.heart7, c.heart2, c.diamond4, c.spadesJ));

    }

    /**
     * 牌型：皇家同花顺
     */
    private void testTypeRoyalFlush() {
        tell(Arrays.asList(c.diamond10, c.diamondJ, c.diamondQ, c.diamondK, c.diamondA, c.spades5, c.heart5),
                Arrays.asList(c.diamond10, c.diamondJ, c.diamondQ, c.diamondK, c.diamondA),
                CardType.RoyalFlush);

        tell(Arrays.asList(c.club10, c.clubJ, c.clubQ, c.clubK, c.clubA, c.club5, c.heart5),
                Arrays.asList(c.club10, c.clubJ, c.clubQ, c.clubK, c.clubA),
                CardType.RoyalFlush);
    }

    /**
     * 牌型：同花顺
     */
    private void testTypeStraightFlush() {
        tell(Arrays.asList(c.diamond10, c.diamondJ, c.diamondQ, c.diamondK, c.diamond9, c.spades5, c.heart5),
                Arrays.asList(c.diamond10, c.diamondJ, c.diamondQ, c.diamondK, c.diamond9),
                CardType.StraightFlush);

        tell(Arrays.asList(c.diamond2, c.diamond3, c.diamond4, c.diamond5, c.diamond6, c.spades5, c.heart5),
                Arrays.asList(c.diamond2, c.diamond6, c.diamond4, c.diamond5, c.diamond3),
                CardType.StraightFlush);

        tell(Arrays.asList(c.spades5, c.spades6, c.spades7, c.spades8, c.diamondA, c.spades9, c.heart5),
                Arrays.asList(c.spades5, c.spades6, c.spades9, c.spades8, c.spades7),
                CardType.StraightFlush);
    }

    /**
     * 牌型：金刚
     */
    private void testTypeFourOf13ind() {
        tell(Arrays.asList(c.diamond10, c.club10, c.heart10, c.spades10, c.spadesK, c.spades5, c.heart5),
                Arrays.asList(c.diamond10, c.club10, c.heart10, c.spadesK, c.spades10),
                CardType.FourOfKind);

        tell(Arrays.asList(c.diamondQ, c.clubQ, c.heartQ, c.spadesQ, c.spadesK, c.spades5, c.heart5),
                Arrays.asList(c.diamondQ, c.clubQ, c.heartQ, c.spadesK, c.spadesQ),
                CardType.FourOfKind);
    }

    /**
     * 牌型：葫芦
     */
    private void testTypeFullHouse() {
        tell(Arrays.asList(c.diamond10, c.club10, c.heart10, c.diamond9, c.club9, c.heart2, c.heart5),
                Arrays.asList(c.diamond10, c.club10, c.heart10, c.diamond9, c.club9),
                CardType.FullHouse);

        tell(Arrays.asList(c.diamond7, c.club7, c.heart7, c.spades8, c.club8, c.heart9, c.heart5),
                Arrays.asList(c.diamond7, c.club7, c.heart7, c.spades8, c.club8),
                CardType.FullHouse);

        // TODO 确定是哪个5
        tell(Arrays.asList(c.club6, c.diamond6, c.heart6, c.spades5, c.club5, c.heart5, c.heart3),
                Arrays.asList(c.club6, c.diamond6, c.heart6, c.spades5, c.club5),
                CardType.FullHouse);
    }

    /**
     * 牌型：同花
     */
    private void testTypeFlush() {
        tell(Arrays.asList(c.diamondK, c.diamond10, c.diamondA, c.diamond3, c.diamond8, c.heart9, c.heart5),
                Arrays.asList(c.diamondK, c.diamond10, c.diamondA, c.diamond3, c.diamond8),
                CardType.Flush);

        tell(Arrays.asList(c.spades6, c.spades7, c.spades8, c.spades9, c.spades2, c.spades3, c.heart5),
                Arrays.asList(c.spades6, c.spades7, c.spades8, c.spades9, c.spades3),
                CardType.Flush);

        tell(Arrays.asList(c.spades6, c.spades8, c.spades7, c.spades9, c.spades2, c.spades3, c.heart5),
                Arrays.asList(c.spades6, c.spades7, c.spades8, c.spades9, c.spades3),
                CardType.Flush);

        // 葫芦小于同花
        tell(Arrays.asList(c.diamond7, c.club7, c.heart7, c.spades8, c.club8, c.heart9, c.heart5, c.heartA, c.heart3),
                Arrays.asList(c.heart9, c.heartA, c.heart7, c.heart5, c.heart3),
                CardType.Flush,
                6);
    }

    /**
     * 顺子
     */
    private void testTypeStraight() {
        tell(Arrays.asList(c.club10, c.diamondJ, c.spadesQ, c.spadesK, c.heartA, c.heart9, c.heart5),
                Arrays.asList(c.club10, c.diamondJ, c.spadesQ, c.spadesK, c.heartA),
                CardType.Straight);

        tell(Arrays.asList(c.club2, c.diamond3, c.spades4, c.spades5, c.heart6, c.heart9, c.heart5),
                Arrays.asList(c.club2, c.diamond3, c.spades4, c.spades5, c.heart6),
                CardType.Straight);

        //  1, 2, 3, 4, 5
        tell(Arrays.asList(c.club2, c.diamond3, c.spades4, c.spades5, c.heartA, c.heart9, c.heart5),
                Arrays.asList(c.club2, c.diamond3, c.spades4, c.spades5, c.heartA),
                CardType.Straight);

        //  1, 6, 7, 8, 9
        tell(Arrays.asList(c.diamond6, c.spades7, c.spades8, c.heartA, c.heart9, c.heartQ, c.heartJ),
                Arrays.asList(c.heartA, c.diamond6, c.spades7, c.spades8, c.heart9),
                CardType.Straight,
                6);
    }

    /**
     * 牌型：三张
     */
    private void testTypeThreeOf13ind() {
        tell(Arrays.asList(c.club2, c.diamond2, c.spades2, c.spades5, c.heart6, c.heart9, c.heartQ),
                Arrays.asList(c.club2, c.diamond2, c.spades2, c.heart9, c.heartQ),
                CardType.ThreeOfKind);

        tell(Arrays.asList(c.club7, c.diamond7, c.spades7, c.spades5, c.heart6, c.heart9, c.heartQ),
                Arrays.asList(c.club7, c.diamond7, c.spades7, c.heartQ, c.heart9),
                CardType.ThreeOfKind);

        // 三张牌
        tell(Arrays.asList(c.club7, c.diamond7, c.spades7),
                Arrays.asList(c.club7, c.diamond7, c.spades7),
                CardType.ThreeOfKind);

    }

    /**
     * 牌型：两对
     */
    private void testTypeTwoPairs() {
        tell(Arrays.asList(c.club4, c.diamond4, c.spades5, c.club5, c.heart6, c.heart9, c.heartQ),
                Arrays.asList(c.club4, c.diamond4, c.spades5, c.club5, c.heartQ),
                CardType.TwoPairs);

        tell(Arrays.asList(c.spades8, c.diamond8, c.spades10, c.heart10, c.heart6, c.heart9, c.heartQ),
                Arrays.asList(c.spades8, c.diamond8, c.spades10, c.heart10, c.heartQ),
                CardType.TwoPairs);

    }

    /**
     * 牌型：一对
     */
    private void testTypeOnePair() {
        tell(Arrays.asList(c.club4, c.diamond4, c.spades5, c.club7, c.heart6, c.heart9, c.heartQ),
                Arrays.asList(c.club4, c.diamond4, c.heartQ, c.heart9, c.club7),
                CardType.OnePair);

        tell(Arrays.asList(c.club9, c.diamond9, c.spades5, c.club7, c.heart6, c.heartJ, c.heartQ),
                Arrays.asList(c.club9, c.diamond9, c.club7, c.heartJ, c.heartQ),
                CardType.OnePair);

        tell(Arrays.asList(c.club9, c.diamond9),
                Arrays.asList(c.club9, c.diamond9),
                CardType.OnePair);

        tell(Arrays.asList(c.heartQ, c.spadesQ, c.spades5),
                Arrays.asList(c.heartQ, c.spadesQ, c.spades5),
                CardType.OnePair);
    }

    /**
     * 牌型：高牌
     */
    private void testTypeHighCard() {
        tell(Arrays.asList(c.club4, c.diamond2, c.spades5, c.club7, c.heart6, c.heart9, c.heartQ),
                Arrays.asList(c.heartQ, c.heart9, c.spades5, c.club7, c.heart6),
                CardType.HighCard);

        tell(Arrays.asList(c.clubA, c.diamondK, c.spadesQ, c.clubJ, c.heart8, c.heart6, c.heart5),
                Arrays.asList(c.clubJ, c.diamondK, c.spadesQ, c.heart8, c.clubA),
                CardType.HighCard);

        tell(Arrays.asList(c.club9, c.heartQ),
                Arrays.asList(c.club9, c.heartQ),
                CardType.HighCard);

        // 三张牌
        tell(Arrays.asList(c.clubA, c.diamondK, c.spadesQ),
                Arrays.asList(c.clubA, c.diamondK, c.spadesQ),
                CardType.HighCard);
    }

    private void tell(List<Card> source, List<Card> find, CardType type, int min) {
        Hand hand = Poker.typeOf(source, min);
        assertEquals(type, hand.getType());
        assertTrue(equals(hand.getBest(), find));
    }

    private void tell(List<Card> source, List<Card> find, CardType type) {
        tell(source, find, type, 2);
    }

    private boolean equals(List<Card> source, List<Card> args) {
        if (args.size() != source.size()) {
            return false;
        }
        Set<Card> set = new HashSet<>();
        source.forEach(v -> set.add(v));
        if (source.size() != set.size()) {
            return false;
        }
        args.forEach(v -> set.remove(v));
        return set.size() == 0;
    }

    private void assertCompare(Hand main, int ret, List<Card> other) {
        assertEquals(ret, Poker.compare(main, Poker.typeOf(other)), Poker.MIN_POINT);
    }

    private void assertCompare(Hand main, int ret, List<Card> other, int min) {
        assertEquals(ret, Poker.compare(main, Poker.typeOf(other), min));
    }

} 
