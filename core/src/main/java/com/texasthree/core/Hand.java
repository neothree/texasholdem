package com.texasthree.core;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * 手牌
 */
public class Hand implements Comparable<Hand> {
    /**
     * 手里的两张牌
     */
    private List<Card> two;
    /**
     * 牌型
     */
    private CardType type;
    /**
     * 最好的牌
     */
    private List<Card> best;
    /**
     * 关键牌
     */
    private List<Card> key;
    /**
     * 桌面的底牌
     */
    private List<Card> bottom;

    public Hand(List<Card> two) {
        this.two = two;
    }

    public void fresh(List<Card> bottom) {
        this.bottom = bottom;
        List<Card> list = new ArrayList<>();
        list.addAll(two);
        list.addAll(this.bottom);
        Hand hand = Poker.Best(list);
        this.type = hand.type;
        this.best = hand.best;
        this.key = hand.key;
    }

    @Override
    public int compareTo(Hand other) {
        return Poker.compare(this, other);
    }
}
