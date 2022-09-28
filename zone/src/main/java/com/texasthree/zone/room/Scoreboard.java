package com.texasthree.zone.room;

import com.texasthree.zone.User;
import com.texasthree.user.UserData;

/**
 * 记分牌
 *
 * @author: neo
 * @create: 2022-09-22 11:00
 */
public class Scoreboard {
    /**
     * 玩家
     */
    private String uid;

    private String name;
    /**
     * 总买入
     */
    private int buyin;
    /**
     * 牌局利润
     */
    private int gameProfit;
    /**
     * 保险利润
     */
    private int insuranceProfit;
    /**
     * 是否结算
     */
    private boolean settle;

    public Scoreboard(User user) {
        this.uid = user.getId();
        this.name = user.getName();
    }

    public Scoreboard(UserData user) {
        this.uid = user.getId();
        this.name = user.getName();
    }

    public void buyin(int value) {
        requireNonSettle();
        this.buyin += value;
    }

    public void gameProfit(int value) {
        requireNonSettle();
        this.gameProfit += value;
    }

    public void insuranceProfit(int value) {
        requireNonSettle();
        this.insuranceProfit += value;
    }

    void settle() {
        this.settle = true;
    }

    private void requireNonSettle() {
        if (this.settle) {
            throw new IllegalArgumentException("玩家已经结算 " +  name);
        }
    }

    public String getUid() {
        return this.uid;
    }

    public String getName() {
        return this.name;
    }

    public int getBuyin() {
        return buyin;
    }

    public int getGameProfit() {
        return gameProfit;
    }

    public int getProfit() {
        return gameProfit + insuranceProfit;
    }

    public boolean isSettle() {
        return settle;
    }

    public int getBalance() {
        return this.settle ? 0 : this.buyin + gameProfit + insuranceProfit;
    }

    public int getInsuranceProfit() {
        return insuranceProfit;
    }

    @Override
    public String toString() {
        return new StringBuilder()
                .append("uid=").append(uid)
                .append(", name=").append(name)
                .append(", buyin=").append(buyin)
                .append(", gameProfit=").append(gameProfit)
                .append(", insuranceProfit=").append(insuranceProfit)
                .append(", balance=").append(getBalance())
                .append(", settle=").append(settle)
                .toString();
    }
}
