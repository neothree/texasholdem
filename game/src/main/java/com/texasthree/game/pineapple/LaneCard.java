package com.texasthree.game.pineapple;

import com.texasthree.game.texas.Card;

/**
 * @author: neo
 * @create: 2021-06-20 09:58
 */
public class LaneCard {
    public Card card;
    public int channel;
    public boolean concurrent;

    LaneCard(Card card, int channel, boolean concurrent) {
        this.card = card;
        this.channel = channel;
        this.concurrent = concurrent;
    }
}
