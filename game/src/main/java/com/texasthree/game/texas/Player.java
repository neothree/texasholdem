package com.texasthree.game.texas;

import java.util.ArrayList;
import java.util.List;

public class Player {

    private int id;

    private int chips;

    private boolean leave;

    private Hand hand;

    public Player(int id, int chips) {
        this.id = id;
        this.chips = chips;
    }

    public int getId() {
        return id;
    }

    public int getChips() {
        return chips;
    }

    public Boolean isLeave() {
        return leave;
    }

    public Boolean inGame() {
        return !leave;
    }

    void leave() {
        leave = true;
    }

    void minus(int value) {
        if (value < 0) {
            throw new IllegalArgumentException();
        }
        if (this.chips < value) {
            throw new IllegalArgumentException("玩家筹码不足 chips=" + chips + "  value=" + value);
        }
        this.chips -= value;
    }

    public Hand getHand() {
        return hand;
    }

    public void setHand(Hand hand) {
        this.hand = hand;
    }

    @Override
    public String toString() {
        return new StringBuilder()
                .append("id=").append(id)
                .append(", chips=").append(chips)
                .append(", leave=").append(leave)
                .append(", hand=").append(hand)
                .toString();
    }

    @Override
    public boolean equals(Object other) {
        return (other instanceof Player) && ((Player) other).getId() == this.id;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    public static List<Player> winners(List<Player> players) {
        var winners = new ArrayList<Player>();
        winners.add(players.get(0));
        for (var i = 1; i < players.size(); i++) {
            var other = players.get(i);
            var com = other.getHand().compareTo(winners.get(0).getHand());
            if (com == 0) {
                winners.add(other);
            } else if (com > 0) {
                winners.clear();
                winners.add(other);
            }
        }
        return winners;
    }
}
