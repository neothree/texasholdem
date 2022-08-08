package com.texasthree.game.texas;

/**
 * 数据统计
 *
 * @author: neo
 * @create: 2021-06-14 15:07
 */
public class Statistic {
    Integer id;
    boolean allin;
    boolean allinWin;
    boolean flopRaise;
    boolean inpot;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public boolean isAllin() {
        return allin;
    }

    public void setAllin(boolean allin) {
        this.allin = allin;
    }

    public boolean isAllinWin() {
        return allinWin;
    }

    public void setAllinWin(boolean allinWin) {
        this.allinWin = allinWin;
    }

    public boolean isFlopRaise() {
        return flopRaise;
    }

    public void setFlopRaise(boolean flopRaise) {
        this.flopRaise = flopRaise;
    }

    public boolean isInpot() {
        return inpot;
    }

    public void setInpot(boolean inpot) {
        this.inpot = inpot;
    }
}
