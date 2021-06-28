package com.texasthree.game.pineapple;

import com.texasthree.game.texas.Card;

/**
 * @author: neo
 * @create: 2021-06-20 09:58
 */
public class RowCard {
    public Card card;
    public int row;
    public boolean concurrent;

    RowCard(Card card, int row, boolean concurrent) {
        this.card = card;
        this.row = row;
        this.concurrent = concurrent;
    }


    @Override
    public String toString() {
        return new StringBuilder()
                .append("[")
                .append("card=").append(card)
                .append("row=").append(row)
                .append("concurrent=").append(concurrent)
                .toString();
    }

}
