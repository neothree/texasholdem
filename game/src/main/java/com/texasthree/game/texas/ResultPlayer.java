package com.texasthree.game.texas;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author: neo
 * @create: 2021-06-14 10:50
 */
@Data
public class ResultPlayer {
    Integer id;
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

    int potback;

    Map<Integer, Integer> pot;

    Statistic statistic;

    /**
     * 赢得筹码
     */
    int getWin() {
        return pot != null ? pot.values().stream().reduce(Integer::sum).orElse(0) : 0;
    }

    int getProfit() {
        return this.getWin() - betSum + potback;
    }
}
