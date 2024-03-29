package com.texasthree.game.texas;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

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


    public int getId() {
        return point * 10 + suit;
    }

    @Override
    public int compareTo(Card other) {
        return this.point.compareTo(other.point);
    }

    public int compareToWithSuit(Card other) {
        var ret = this.compareTo(other);
        if (ret != 0) {
            return ret;
        }
        return this.suit.compareTo(other.suit);

    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof Card)) {
            return false;
        }
        var o = (Card) other;
        return this.point.equals(o.point) && this.suit.equals(o.suit);
    }

    @Override
    public int hashCode() {
        return getId();
    }


    public static List<Card> removeList(List<Card> src, List<Card> others) {
        var set = new HashSet<>(others);
        return src.stream().filter(v -> !set.contains(v)).collect(Collectors.toList());
    }
}
