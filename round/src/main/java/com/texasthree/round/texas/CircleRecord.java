package com.texasthree.round.texas;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CircleRecord {
    private Circle circle;

    private List<Card> board;

    private int potChips;

    private int playerNum;

    public List<Action> actions = new ArrayList<>();

    public CircleRecord() {}

    public CircleRecord(Circle circle, int potChips, int playerNum) {
        this.circle = circle;
        this.potChips = potChips;
        this.playerNum = playerNum;
    }

}
