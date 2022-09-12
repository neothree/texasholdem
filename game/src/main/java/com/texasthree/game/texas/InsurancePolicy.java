package com.texasthree.game.texas;

import java.math.BigDecimal;
import java.util.List;

/**
 * 保险单
 * @author: neo
 * @create: 2022-09-11 22:37
 */
public class InsurancePolicy {
    public final List<Card> outs;

    public final BigDecimal odds;

    public final boolean hit;

    public int amount;

    InsurancePolicy(List<Card> outs, BigDecimal odds, boolean hit) {
        this.outs = outs;
        this.odds = odds;
        this.hit = hit;
    }

    void buy(int amount) {
        this.amount = amount;
    }
}
