package com.texasthree.round.texas;

import com.texasthree.round.TableCard;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author : neo
 * create at:  2020-06-28  23:48
 * @description:
 */
public class TexasBuilder {
    private int playerNum = 2;
    private int smallBlind = 1;
    private int ante = 0;
    private int initChips = 100;
    private Ring<Player> ring;
    private List<Card> leftCard;
    private Map<Regulation, Integer> regulations;

    public TexasBuilder() {

    }

    public TexasBuilder(int playerNum) {
        this.playerNum = playerNum;
    }

    public TexasBuilder playerNum(int playerNum) {
        this.playerNum = playerNum;
        return this;
    }

    public TexasBuilder smallBlind(int smallBlind) {
        this.smallBlind = smallBlind;
        return this;
    }

    public TexasBuilder ante(int ante) {
        this.ante = ante;
        return this;
    }

    public TexasBuilder initChips(int initChips) {
        this.initChips = initChips;
        return this;
    }

    public TexasBuilder ring(Ring<Player> ring) {
        this.ring = ring;
        return this;
    }

    public TexasBuilder regulations(Map<Regulation, Integer> regulations) {
        this.regulations = regulations;
        return this;
    }

    public TexasBuilder leftCard(List<Card> leftCard) {
        this.leftCard = leftCard;
        return this;
    }

    Texas build() {
        if (ring == null) {
            ring = Ring.create(playerNum);
            for (int i = 1; i <= playerNum; i++) {
                ring.setValue(new Player(i, initChips));
                ring = ring.getNext();
            }
        }
        this.playerNum = ring.size();

        if (this.leftCard == null) {
            this.leftCard = TableCard.getInstance().shuffle();
        }
        if (ring.value.getHand() == null) {
            for (int i = 1; i <= playerNum; i++) {
                ring.value.setHand(new Hand(leftCard.subList(i * 2, i * 2 + 2)));
                ring = ring.getNext();
            }
            leftCard = leftCard.subList(ring.size(), leftCard.size());
        }

        if (regulations == null) {
            regulations = new HashMap<>();
        }

        if (!regulations.containsKey(Regulation.SmallBlind)) {
            regulations.put(Regulation.SmallBlind, smallBlind);
        }
        if (!regulations.containsKey(Regulation.Ante)) {
            regulations.put(Regulation.Ante, ante);
        }
        if (!regulations.containsKey(Regulation.Dealer)) {
            regulations.put(Regulation.Dealer, 1);
        }

        this.ring = this.ring.move(v -> v.getId() == regulations.get(Regulation.Dealer));

        if (ring.size() == 2) {
            regulations.put(Regulation.SB, this.ring.value.getId());
            regulations.put(Regulation.BB, this.ring.getNext().value.getId());
        } else {
            regulations.put(Regulation.SB, this.ring.getNext().value.getId());
            regulations.put(Regulation.BB, this.ring.getNext().getNext().value.getId());
        }
        return new Texas(regulations, ring, leftCard);
    }
}
