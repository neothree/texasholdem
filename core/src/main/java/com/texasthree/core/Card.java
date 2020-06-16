package com.texasthree.core;

import java.util.Comparator;

public class Card implements Comparable<Card> {
    /**
     * 黑桃
     */
    public final static int Spades = 4;
    /**
     * 红桃
     */
    public final static int Hearts = 3;
    /**
     * 梅花
     */
    public final static int Clubs = 2;
    /**
     * 方块
     */
    public final static int Diamonds = 1;

    /**
     * 名称
     */
    public final String name;
    /**
     * 点数
     */
    public final Integer point;
    /**
     * 花色
     */
    public final Integer suit;

    public Card(String name, int point, int suit) {
        this.name = name;
        this.point = point;
        this.suit = suit;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int compareTo(Card other) {
        return this.point.compareTo(other.point);
    }
}
