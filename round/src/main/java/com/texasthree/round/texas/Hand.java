package com.texasthree.round.texas;

import java.util.ArrayList;
import java.util.List;

/**
 * 手牌
 */
public class Hand implements Comparable<Hand> {
    /**
     * 手里的两张牌
     */
    private List<Card> list;
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

    public Hand(List<Card> list) {
        this.list = list;
    }

    public Hand(List<Card> best, CardType type) {
        this.best = best;
        this.type = type;
    }

    public void fresh(List<Card> bottom) {
        this.bottom = bottom;
        List<Card> list = new ArrayList<>();
        list.addAll(this.list);
        list.addAll(this.bottom);
        Hand hand = Poker.typeOf(list);
        this.type = hand.type;
        this.best = hand.best;
        this.key = hand.key;
    }

    @Override
    public int compareTo(Hand other) {
        return Poker.compare(this, other);
    }

    public List<Card> getList() {
        return list;
    }

    public CardType getType() {
        return type;
    }

    public List<Card> getBest() {
        return best;
    }

    public List<Card> getKey() {
        return key;
    }

    public List<Card> getBottom() {
        return bottom;
    }
}
