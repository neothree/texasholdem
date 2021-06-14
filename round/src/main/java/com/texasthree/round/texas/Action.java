package com.texasthree.round.texas;

public class Action {

    public int id;

    public Optype op;

    public final int chipsBet;
    public final int chipsAdd;
    public final int chipsLeft;
    public final int sumPot;

    public Action(Optype op) {
        this(-1, op, 0, 0, 0, 0);
    }

    public Action(Optype op, int chipsAdd) {
        this(-1, op, 0, chipsAdd, 0, 0);
    }

    public Action(int id, Optype op, int chipsBet, int chipsAdd, int chipsLeft, int sumPot) {
        this.id = id;
        this.op = op;
        this.chipsBet = chipsBet;
        this.chipsAdd = chipsAdd;
        this.chipsLeft = chipsLeft;
        this.sumPot = sumPot;
    }

    public static Action Fold() {
        return new Action(Optype.Fold);
    }

}
