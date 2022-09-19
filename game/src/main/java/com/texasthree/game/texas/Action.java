package com.texasthree.game.texas;

public class Action {

    public final int id;

    public Optype op;

    /**
     * 押注后一共的筹码
     */
    public final int chipsBet;
    /**
     * 这一注增加的筹码
     */
    public final int chipsAdd;
    /**
     * 剩余的筹码
     */
    public final int chipsLeft;
    /**
     * 底池的总筹码
     */
    public int sumPot;
    /**
     * 强制
     */
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

    public boolean isInpot() {
        if (this.straddle) {
            return false;
        }
        return op == Optype.Raise || op == Optype.Allin || op == Optype.Call;
    }

    @Override
    public String toString() {
        return new StringBuilder()
                .append("[id=").append(id)
                .append(", op=").append(op)
                .append(", chipsBet=").append(chipsBet)
                .append(", chipsAdd=").append(chipsAdd)
                .append(", chipsLeft=").append(chipsLeft)
                .append(", sumPot=").append(sumPot)
                .append(", straddle=").append(straddle)
                .append("]")
                .toString();
    }
}
