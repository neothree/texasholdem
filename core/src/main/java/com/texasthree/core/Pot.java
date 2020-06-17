package com.texasthree.core;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Pot {

    private Circle circle;

    private final int smallBind;

    private final int ante ;

    private final int payerNum ;

    private int amplitude;

    private Set<Integer> allin = new HashSet();

    private Set<Integer> fold = new HashSet();

    public Pot(int playerNum, int smallBind, int ante) {
        this.smallBind = smallBind;
        this.ante = ante;
        this.payerNum = playerNum;
    }


    /**
     * 一圈开始
     */
    public void circleStart() {
        if (Circle.River.equals(circle)) {
            return;
        }

        // 下一圈
        this.circle = nextCircle(circle);

        if (smallBind > 0) {
            this.amplitude = this.smallBind*2;
        } else {
            this.amplitude = this.ante*2;
        }



//        -- 记录一圈刚开始时的数据
//        local info = {
//                circle = self.circle,
//                sumPot = self:SumChips(),
//                board = nil,
//                num = self:NotFoldNum(),
//                actList = {},
//    }
//        self.record[self.circle] = info

    }

    public void circleEnd(List<Card> board) {

    }

    public void action() {

    }

    public Action getAction(int id) {
        return null;
    }


    public int getStandard() {
        return 0;
    }

    public int getStandardId() {
        return -1;
    }

    public void setStandardInfo(int chips, int id) {
    }


    public void showdown() {

    }


    public int allinNum() {
        return this.allin.size();
    }

    public int foldNum() {
        return this.fold.size();
    }

    public int notFoldNum() {
        return this.payerNum - foldNum();
    }

    public boolean isFold(int i) {
        return fold.contains(i);
    }

    public boolean isAllin(int i) {
        return allin.contains(i);
    }

    public Circle circle() {
        return circle;
    }

    private static Circle nextCircle(Circle c) {
        if (Circle.Preflop.equals(c)) {
            return Circle.Flop;
        } else if (Circle.Flop.equals(c)) {
            return Circle.Turn;
        } else if (Circle.Turn.equals(c)) {
            return Circle.River;
        } else {
            return Circle.Preflop;

        }
    }
}

