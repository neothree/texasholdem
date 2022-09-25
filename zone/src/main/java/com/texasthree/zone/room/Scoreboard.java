package com.texasthree.zone.room;

import com.texasthree.zone.user.User;

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
    private User user;
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

    Scoreboard(User user) {
        this.user = user;
    }

    void buyin(int value) {
        requireNonSettle();
        this.buyin += value;
    }

    void gameProfit(int value) {
        requireNonSettle();
        this.gameProfit += value;
    }

    void insuranceProfit(int value) {
        requireNonSettle();
        this.insuranceProfit += value;
    }

    void settle() {
        this.settle = true;
    }

    private void requireNonSettle() {
        if (this.settle) {
            throw new IllegalArgumentException("玩家已经结算 " + user);
        }
    }

    public String getUid() {
        return this.user.getId();
    }

    public String getName() {
        return this.user.getName();
    }

    public int getBuyin() {
        return buyin;
    }

    public int getGameProfit() {
        return gameProfit;
    }

    boolean isSettle() {
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
                .append("uid=").append(user.getId())
                .append(", name=").append(user.getName())
                .append(", buyin=").append(buyin)
                .append(", gameProfit=").append(gameProfit)
                .append(", insuranceProfit=").append(insuranceProfit)
                .append(", balance=").append(getBalance())
                .append(", settle=").append(settle)
                .toString();
    }
}
