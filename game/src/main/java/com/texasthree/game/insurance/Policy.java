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
class Policy {

    /**
     * 购买的outs
     */
    final List<Card> outs;
    /**
     * 金额
     */
    final int amount;
    /**
     * 是否命中
     */
    final boolean hit;

    final int applicant;

    Policy(int applicant, int amount, List<Card> outs, boolean hit) {
        this.applicant = applicant;
        this.outs = outs;
        this.hit = hit;
        this.amount = amount;
    }

    BigDecimal getOdds() {
        return Insurance.odds(outs.size());
    }

    Claim claim() {
        var give = hit ? BigDecimal.valueOf(amount).multiply(getOdds()).intValue() : 0;
        return new Claim(applicant, amount, give);
    }
}
