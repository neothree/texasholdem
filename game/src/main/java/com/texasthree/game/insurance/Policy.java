package com.texasthree.game.insurance;

import com.texasthree.game.texas.Card;

import java.math.BigDecimal;
import java.util.List;

/**
 * 保险单
 *
 * @author: neo
 * @create: 2022-09-11 22:37
 */
public class Policy {

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

    Policy(BigDecimal amount, List<Card> outs, boolean hit) {
        this.outs = outs;
        this.hit = hit;
        this.amount = amount;
    }

    BigDecimal getOdds() {
        return Insurance.odds(outs.size());
    }

    int claimAmount() {
        return hit ? amount.multiply(getOdds()).intValue() : 0;
    }
}
