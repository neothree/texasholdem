package com.texasthree.core;

public class Action {
    private int chipsBet;

    private Optype op;

    public Action(Optype op) {
        this.op = op;
    }

    public int getChipsBet() {
        return chipsBet;
    }
}
