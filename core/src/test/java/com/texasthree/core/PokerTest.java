package com.texasthree.core;

import com.alibaba.fastjson.JSONObject;
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
 * @since <pre>Jun 16, 2020</pre>
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
    public void testTypeOfList() throws Exception {
        this.testRoyalFlush();
        this.testStraightFlush();
        this.testFourOfKind();
        this.testFullHouse();
        this.testFlush();
        this.testStraight();
        this.testThreeOfKind();
        this.testTwoPairs();
        this.testOnePair();
        this.testHighCard();
    }

    /**
     * 牌型：皇家同花顺
     */
    private void testRoyalFlush() {

        tell(Arrays.asList(c.D10, c.DJ, c.DQ, c.DK, c.DA, c.S5, c.H5),
                Arrays.asList(c.D10, c.DJ, c.DQ, c.DK, c.DA),
                CardType.RoyalFlush);

        tell(Arrays.asList(c.C10, c.CJ, c.CQ, c.CK, c.CA, c.C5, c.H5),
                Arrays.asList(c.C10, c.CJ, c.CQ, c.CK, c.CA),
                CardType.RoyalFlush);
    }

    /**
     * 牌型：同花顺
     */
    private void testStraightFlush() {
        tell(Arrays.asList(c.D10, c.DJ, c.DQ, c.DK, c.D9, c.S5, c.H5),
                Arrays.asList(c.D10, c.DJ, c.DQ, c.DK, c.D9),
                CardType.StraightFlush);

        tell(Arrays.asList(c.D2, c.D3, c.D4, c.D5, c.D6, c.S5, c.H5),
                Arrays.asList(c.D2, c.D6, c.D4, c.D5, c.D3),
                CardType.StraightFlush);

        tell(Arrays.asList(c.S5, c.S6, c.S7, c.S8, c.DA, c.S9, c.H5),
                Arrays.asList(c.S5, c.S6, c.S9, c.S8, c.S7),
                CardType.StraightFlush);
    }

    /**
     * 牌型：金刚
     */
    private void testFourOfKind() {
        tell(Arrays.asList(c.D10, c.C10, c.H10, c.S10, c.SK, c.S5, c.H5),
                Arrays.asList(c.D10, c.C10, c.H10, c.SK, c.S10),
                CardType.FourOfKind);

        tell(Arrays.asList(c.DQ, c.CQ, c.HQ, c.SQ, c.SK, c.S5, c.H5),
                Arrays.asList(c.DQ, c.CQ, c.HQ, c.SK, c.SQ),
                CardType.FourOfKind);
    }

    private void testFullHouse() {
        tell(Arrays.asList(c.D10, c.C10, c.H10, c.D9, c.C9, c.H2, c.H5),
                Arrays.asList(c.D10, c.C10, c.H10, c.D9, c.C9),
                CardType.FullHouse);

        tell(Arrays.asList(c.D7, c.C7, c.H7, c.S8, c.C8, c.H9, c.H5),
                Arrays.asList(c.D7, c.C7, c.H7, c.S8, c.C8),
                CardType.FullHouse);
    }

    private void testFlush() {
        tell(Arrays.asList(c.DK, c.D10, c.DA, c.D3, c.D8, c.H9, c.H5),
                Arrays.asList(c.DK, c.D10, c.DA, c.D3, c.D8 ),
                CardType.Flush);

        tell(Arrays.asList(c.S6, c.S7, c.S8, c.S9, c.S2, c.S3, c.H5),
                Arrays.asList(c.S6, c.S7, c.S8, c.S9, c.S3),
                CardType.Flush);

        tell(Arrays.asList(c.S6, c.S8, c.S7, c.S9, c.S2, c.S3, c.H5),
                Arrays.asList(c.S6, c.S7, c.S8, c.S9, c.S3),
                CardType.Flush);

        // 葫芦小于同花
        tell(Arrays.asList(c.D7, c.C7, c.H7, c.S8, c.C8, c.H9, c.H5, c.HA, c.H3),
                Arrays.asList(c.H9, c.HA, c.H7, c.H5, c.H3 ),
                CardType.Flush,
                6);
    }

    /**
     *
     */
    private void testStraight() {
        tell(Arrays.asList(c.C10, c.DJ, c.SQ, c.SK, c.HA, c.H9, c.H5),
                Arrays.asList(c.C10, c.DJ, c.SQ, c.SK, c.HA),
                CardType.Straight);

        tell(Arrays.asList(c.C2, c.D3, c.S4, c.S5, c.H6, c.H9, c.H5),
                Arrays.asList(c.C2, c.D3, c.S4, c.S5, c.H6 ),
                CardType.Straight);

        //  A, 2, 3, 4, 5
        tell(Arrays.asList(c.C2, c.D3, c.S4, c.S5, c.HA, c.H9, c.H5),
                Arrays.asList(c.C2, c.D3, c.S4, c.S5, c.HA  ),
                CardType.Straight);

        //  A, 6, 7, 8, 9
        tell(Arrays.asList(c.D6, c.S7, c.S8, c.HA, c.H9, c.HQ, c.HJ),
                Arrays.asList(c.HA, c.D6, c.S7, c.S8, c.H9 ),
                CardType.Straight,
                6);
    }

    private void testThreeOfKind() {
        tell(Arrays.asList(c.C2, c.D2, c.S2, c.S5, c.H6, c.H9, c.HQ),
                Arrays.asList(c.C2, c.D2, c.S2, c.H9, c.HQ ),
                CardType.ThreeOfKind);

        tell(Arrays.asList(c.C7, c.D7, c.S7, c.S5, c.H6, c.H9, c.HQ),
                Arrays.asList(c.C7, c.D7, c.S7, c.HQ, c.H9 ),
                CardType.ThreeOfKind);

        // 三张牌
        tell(Arrays.asList(c.C7, c.D7, c.S7),
                Arrays.asList(c.C7, c.D7, c.S7),
                CardType.ThreeOfKind);

    }

    private void testTwoPairs() {
        tell(Arrays.asList(c.C4, c.D4, c.S5, c.C5, c.H6, c.H9, c.HQ),
                Arrays.asList(c.C4, c.D4, c.S5, c.C5, c.HQ ),
                CardType.TwoPairs);

        tell(Arrays.asList(c.S8, c.D8, c.S10, c.H10, c.H6, c.H9, c.HQ),
                Arrays.asList(c.S8, c.D8, c.S10, c.H10, c.HQ ),
                CardType.TwoPairs);

    }


    private void testOnePair() {
        tell(Arrays.asList(c.C4, c.D4, c.S5, c.C7, c.H6, c.H9, c.HQ),
                Arrays.asList( c.C4, c.D4, c.HQ, c.H9, c.C7 ),
                CardType.OnePair);

        tell(Arrays.asList(c.C9, c.D9, c.S5, c.C7, c.H6, c.HJ, c.HQ),
                Arrays.asList(  c.C9, c.D9, c.C7, c.HJ, c.HQ ),
                CardType.OnePair);

        tell(Arrays.asList(c.C9, c.D9 ),
                Arrays.asList(  c.C9, c.D9 ),
                CardType.OnePair);

        tell(Arrays.asList(c.HQ, c.SQ, c.S5),
                Arrays.asList(  c.HQ, c.SQ, c.S5),
                CardType.OnePair);
    }

    private void testHighCard() {
        tell(Arrays.asList(c.C4, c.D2, c.S5, c.C7, c.H6, c.H9, c.HQ),
                Arrays.asList( c.HQ, c.H9, c.S5, c.C7, c.H6 ),
                CardType.HighCard);

        tell(Arrays.asList(c.CA, c.DK, c.SQ, c.CJ, c.H8, c.H6, c.H5),
                Arrays.asList(c.CJ, c.DK, c.SQ, c.H8, c.CA ),
                CardType.HighCard);

        tell(Arrays.asList(c.C9, c.HQ),
                Arrays.asList(c.C9, c.HQ),
                CardType.HighCard);

        // 三张牌
        tell(Arrays.asList(c.CA, c.DK, c.SQ),
                Arrays.asList(c.CA, c.DK, c.SQ ),
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

} 
