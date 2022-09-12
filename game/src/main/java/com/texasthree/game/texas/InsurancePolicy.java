package com.texasthree.game.texas;

import java.math.BigDecimal;
import java.util.List;

/**
 * 保险单
 *
 * @author: neo
 * @create: 2022-09-11 22:37
 */
public class InsurancePolicy {

    /**
     * 购买的outs
     */
    public final List<Card> outs;
    /**
     * 金额
     */
    public final BigDecimal amount;
    /**
     * 是否命中
     */
    public final boolean hit;

    InsurancePolicy(BigDecimal amount, List<Card> outs, boolean hit) {
        this.outs = outs;
        this.hit = hit;
        this.amount = amount;
    }

    public BigDecimal getOdds() {
        return Insurance.odds(outs.size());
    }
}
