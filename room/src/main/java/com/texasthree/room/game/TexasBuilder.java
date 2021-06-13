package com.texasthree.room.game;

import com.texasthree.room.User;
import com.texasthree.round.texas.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author : neo
 * create at:  2020-06-28  23:48
 * @description:
 */
public class TexasBuilder {

    private List<User> users = new ArrayList<>();

    private Map<String, Integer> position;

    private Map<Regulation, Integer> laws;

    public TexasBuilder laws(Map<Regulation, Integer> laws) {
        this.laws = laws;
        return this;
    }

    public TexasBuilder users(List<User> users) {
        this.users = users;
        return this;
    }

    public TexasBuilder position(Map<String, Integer> position) {
        this.position = position;
        return this;
    }

    public Texas build() {
        List<Card> leftCard = TableCard.getInstance().shuffle();

        Ring<Player> ring = Ring.create(users.size());
        for (int i = 0; i < users.size(); i++) {
            User v = users.get(i);
            ring.setValue(new Player(position.get(v.getId()), v.getChips(), new Hand(leftCard.subList(i, i + 2))));
            ring = ring.getNext();
        }
        leftCard = leftCard.subList(users.size() * 2, leftCard.size());
        return new Texas(laws, ring, leftCard);
    }
}
