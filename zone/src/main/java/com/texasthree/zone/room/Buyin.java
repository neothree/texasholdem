package com.texasthree.zone.room;

import com.texasthree.zone.user.User;

/**
 * 房间买入
 *
 * @author: neo
 * @create: 2022-09-22 11:00
 */
public class Buyin {
    /**
     * 玩家
     */
    private User user;
    /**
     * 总买入
     */
    private int sum;
    /**
     * 利润
     */
    private int profit;
    /**
     * 是否结算
     */
    private boolean settle;

    public Buyin(User user) {
        this.user = user;
    }

    public void buyin(int value) {
        if (this.settle) {
            throw new IllegalArgumentException("玩家已经结算，不能买入");
        }
        this.sum += value;
    }

    public void changeProfit(int value) {
        if (this.settle) {
            throw new IllegalArgumentException("玩家已经结算，不能修改利润");
        }
        this.profit += value;
    }

    public String getUid() {
        return this.user.getId();
    }

    public String getName() {
        return this.user.getName();
    }

    public int getSum() {
        return sum;
    }

    public int getProfit() {
        return profit;
    }

    public boolean isSettle() {
        return settle;
    }

    public void settle() {
        this.settle = true;
    }

    public int getBalance() {
        return this.settle ? 0 : this.sum + profit;
    }

    @Override
    public String toString() {
        return new StringBuilder()
                .append("uid=").append(user.getId())
                .append(", name=").append(user.getName())
                .append(", sum=").append(sum)
                .append(", profit=").append(profit)
                .append(", balance=").append(getBalance())
                .append(", settle=").append(settle)
                .toString();
    }
}
