package com.texasthree.round.texas;

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

    public void leave() {
        leave = true;
    }

    public void changeChips(int change) {
        this.chips += change;
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
}
