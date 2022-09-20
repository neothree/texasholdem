package com.texasthree.game.insurance;

/**
 * @author: neo
 * @create: 2022-09-20 16:55
 */
public class Claim {

    public final int applicant;

    public final int amount;

    public final int claim;

    Claim(int applicant, int amount, int claim) {
        this.applicant = applicant;
        this.amount = amount;
        this.claim = claim;
    }
}
