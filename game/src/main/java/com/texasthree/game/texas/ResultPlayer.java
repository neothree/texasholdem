package com.texasthree.game.texas;


import java.util.List;
import java.util.Map;

/**
 * @author: neo
 * @create: 2021-06-14 10:50
 */
public class ResultPlayer {
    String id;
    /**
     * 总押注
     */
    int betSum;
    /**
     * 是否亮牌
     */
    boolean cardShow;
    /**
     * 手牌
     */
    List<Card> cardList;

    int refund;

    Map<Integer, Integer> pot;

    Statistic statistic;

    /**
     * 赢得筹码
     */
    int getWin() {
        return pot != null ? pot.values().stream().reduce(Integer::sum).orElse(0) : 0;
    }

    int getProfit() {
        return this.getWin() - betSum + refund;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getBetSum() {
        return betSum;
    }

    public void setBetSum(int betSum) {
        this.betSum = betSum;
    }

    public boolean isCardShow() {
        return cardShow;
    }

    public void setCardShow(boolean cardShow) {
        this.cardShow = cardShow;
    }

    public List<Card> getCardList() {
        return cardList;
    }

    public void setCardList(List<Card> cardList) {
        this.cardList = cardList;
    }

    public int getRefund() {
        return refund;
    }

    public void setRefund(int refund) {
        this.refund = refund;
    }

    public Map<Integer, Integer> getPot() {
        return pot;
    }

    public void setPot(Map<Integer, Integer> pot) {
        this.pot = pot;
    }

    public Statistic getStatistic() {
        return statistic;
    }

    public void setStatistic(Statistic statistic) {
        this.statistic = statistic;
    }
}
