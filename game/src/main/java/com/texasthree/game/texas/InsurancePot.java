package com.texasthree.game.texas;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static com.texasthree.game.texas.Insurance.odds;

/**
 * @author: neo
 * @create: 2022-09-12 14:59
 */
public class InsurancePot {

    private final Divide pot;

    public final String circle;

    public final Player winner;

    public final List<Card> outs;

    public final boolean hit;

    public List<InsurancePolicy> policies = new ArrayList<>();

    private boolean activate = false;


    InsurancePot(Divide pot, String circle, Player winner, List<Player> others, List<Card> communityCards, List<Card> leftCard) {
        this.pot = pot;
        this.circle = circle;
        this.winner = winner;
        this.outs = new ArrayList<>();
        for (var v : leftCard) {
            var extra = new ArrayList<>(communityCards);
            extra.add(v);
            for (var p : others) {
                p.getHand().fresh(extra);
            }
            var ws = Texas.winners(others);
            if (ws.size() == 1 && ws.get(0).getId() != winner.getId()) {
                outs.add(v);
            }
        }
        this.hit = outs.stream().anyMatch(v -> v.equals(leftCard.get(0)));
    }

    boolean activate() {
        return pot.getMembers().size() > 0
                && pot.getMembers().size() <= 3
                && odds(outs.size()).compareTo(BigDecimal.ZERO) > 0;
    }

    /**
     * 满池
     */
    int fullPot() {
        return 0;
    }

    /**
     * 保本
     */
    int breakEven() {
        return 0;
    }
}
