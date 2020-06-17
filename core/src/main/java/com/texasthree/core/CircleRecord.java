package com.texasthree.core;

import java.util.ArrayList;
import java.util.List;

public class CircleRecord {
    private Circle circle;
    public List<Card> board;
    private int potChips;
    private int playerNum;
    public List<Action> actions = new ArrayList<>();

    public CircleRecord(Circle circle, int potChips, int playerNum) {
        this.circle = circle;
        this.board = board;
        this.potChips = potChips;
        this.playerNum = playerNum;
    }

}
