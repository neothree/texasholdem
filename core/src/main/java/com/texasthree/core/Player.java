package com.texasthree.core;

public class Player {

    private int id;

    private int chips;

    private Boolean leave;

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

    public Boolean getLeave() {
        return leave;
    }

    public void leave() {
    }
}
