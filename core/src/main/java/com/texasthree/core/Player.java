package com.texasthree.core;

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
    }
}
