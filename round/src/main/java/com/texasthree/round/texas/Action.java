package com.texasthree.round.texas;

public class Action {

    public int id;

    public Optype op;

    public final int chipsBet;
    public final int chipsAdd;
    public final int chipsLeft;
    public final int sumPot;

    public boolean straddle;

    public Action(int id, Optype op, int chipsBet, int chipsAdd, int chipsLeft, int sumPot) {
        this.id = id;
        this.op = op;
        this.chipsBet = chipsBet;
        this.chipsAdd = chipsAdd;
        this.chipsLeft = chipsLeft;
        this.sumPot = sumPot;
        this.straddle = false;
    }

    public static Action fold() {
        return Action.of(Optype.Fold);
    }

    public static Action raise(int chipsAdd) {
        return new Action(-1, Optype.Raise, 0, chipsAdd, 0, 0);
    }

    public static Action of(Optype op) {
        return new Action(-1, op, 0, 0, 0, 0);
    }

    public static Action straddleBlind(int chipsAdd) {
        var act = new Action(-1, Optype.Raise, chipsAdd, 0, 0, 0);
        act.straddle = true;
        return act;
    }

    public boolean isInpot() {
        if (this.straddle) {
            return false;
        }
        return op == Optype.Raise || op == Optype.Allin || op == Optype.Call;
    }
}
