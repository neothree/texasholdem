package com.texasthree.appzone.room;

import java.util.Map;

/**
 * @author: neo
 * @create: 2022-09-21 11:24
 */
public class RoundSettlementEntry {
    public final int id;
    /**
     * 牌局利润
     */
    public final int profit;

    /**
     * 保险利润
     */
    public final int insurance;

    public final Map<Integer, Integer> pot;

    public RoundSettlementEntry(int id, int profit, int insurance, Map<Integer, Integer> pot) {
        this.id = id;
        this.insurance = insurance;
        this.profit = profit;
        this.pot = pot;
    }

    @Override
    public String toString() {
        return new StringBuilder()
                .append("id=").append(id)
                .append(", profit=").append(profit)
                .append(", insurance=").append(insurance)
                .append(", pot=").append(pot)
                .toString();
    }
}
