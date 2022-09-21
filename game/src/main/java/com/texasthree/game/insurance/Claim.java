package com.texasthree.game.insurance;

/**
 * @author: neo
 * @create: 2022-09-20 16:55
 */
public class Claim {

    public final int applicant;

    public final int profit;

    Claim(int applicant, int profit) {
        this.applicant = applicant;
        this.profit = profit;
    }

    public int getApplicant() {
        return applicant;
    }

    public int getProfit() {
        return profit;
    }

    @Override
    public String toString() {
        return new StringBuilder()
                .append("applicant=").append(applicant)
                .append(", profit=").append(profit)
                .toString();
    }
}
