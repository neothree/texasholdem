package com.texasthree.game.pineapple;

import com.texasthree.game.texas.Card;

/**
 * @author: neo
 * @create: 2021-06-20 09:58
 */
public class RowCard {

    /**
     * 首道
     */
    public static final int ROW_HEAD = 0;
    public static final int SIZE_HEAD = 3;
    /**
     * 中道
     */
    public static final int ROW_MIDDLE = 1;
    public static final int SIZE_MIDDLE = 5;
    /**
     * 尾道
     */
    public static final int ROW_TAIL = 2;
    public static final int SIZE_TAIL = 5;

    public Card card;

    public int row;
    /**
     * 预先发牌
     */
    public boolean beforehand;

    RowCard(Card card, int row, boolean beforehand) {
        if (row != ROW_HEAD && row != ROW_MIDDLE && row != ROW_TAIL) {
            throw new IllegalArgumentException();
        }
        if (card == null) {
            throw new IllegalArgumentException();
        }
        this.card = card;
        this.row = row;
        this.beforehand = beforehand;
    }


    @Override
    public String toString() {
        return new StringBuilder()
                .append("[")
                .append("card=").append(card)
                .append("row=").append(row)
                .append("beforehand=").append(beforehand)
                .toString();
    }

}
