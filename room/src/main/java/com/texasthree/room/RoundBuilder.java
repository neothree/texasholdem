package com.texasthree.room;

import com.texasthree.round.texas.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author : neo
 * create at:  2020-06-28  23:48
 * @description:
 */
public class RoundBuilder {

    List<User> users;


    public Texas build() {
        Map<Law, Integer> laws = new HashMap<>();
        List<Card> leftCard = TableCard.getInstance().shuffle();

        Ring<Player> ring = Ring.create(3);
        return new Texas(laws, ring, leftCard);
    }
}
