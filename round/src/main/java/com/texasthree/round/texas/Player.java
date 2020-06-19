package com.texasthree.round.texas;

public class Player {

    private int id;

    private int chips;

    private boolean leave;

    private Hand hand;

    public Player(int id, int chips, Hand hand) {
        this.id = id;
        this.chips = chips;
        this.hand = hand;
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

    public void leave() {
        leave = true;
    }

    public void changeChips(int change) {
        this.chips += change;
    }

    public Hand getHand() {
        return hand;
    }
}
