package com.texasthree.game.texas;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 保险
 *
 * @author: neo
 * @create: 2022-09-11 18:21
 */
public class Insurance {

    private static final BigDecimal[] ODDS = new BigDecimal[21];

    static {
        String[] init = {"0", "31", "16", "10", "8", "6", "5", "4", "3.5", "3", "2.5", "2.3", "2", "1.8", "1.6",
                "1.4", "1.3", "1.2", "1.1", "1", "0.8"};
        for (var i = 0; i < init.length; i++) {
            ODDS[i] = new BigDecimal(init[i]);
        }
    }

    private String circle;
    /**
     * 分池
     */
    private final List<InsurancePot> pots = new ArrayList<>();

    Insurance(List<Player> players, List<Card> communityCards, List<Card> leftCard, String circle, List<Divide> pots) {
        this.circle = circle;

        // 第四张牌
        var cc4 = communityCards.subList(0, 3);
        var lc4 = new ArrayList<>(leftCard);
        lc4.addAll(communityCards.subList(3, 5));
        // 第五张牌
        var cc5 = communityCards.subList(0, 4);
        var lc5 = new ArrayList<>(leftCard);
        lc5.addAll(communityCards.subList(4, 5));

        for (var pot : pots) {
            // 超过3个人不触发保险
            if (pot.size() > 3) {
                continue;
            }

            // 河牌保险
            this.init(pot, players, cc5, lc5, Circle.RIVER);
            if (Circle.TURN.equals(circle)) {
                // 转牌保险
                this.init(pot, players, cc4, lc4, Circle.TURN);
            } else if (!Circle.RIVER.equals(circle)) {
                throw new IllegalArgumentException("circle 错误: " + circle);
            }
        }
    }

    private void init(Divide pot, List<Player> players, List<Card> communityCards, List<Card> leftCard, String circle) {
        // 筛选出池中的玩家
        players = players.stream()
                .filter(v -> pot.contains(v.getId()))
                .collect(Collectors.toList());

        // 更新手牌
        players.forEach(v -> v.getHand().fresh(communityCards));

        // 赢家
        var winners = Texas.winners(players);
        if (winners.size() != 1) {
            return;
        }
        var winner = winners.get(0);
        var others = players.stream()
                .filter(p -> p.getId() != winner.getId())
                .collect(Collectors.toList());

        // 保险池
        var ip = new InsurancePot(pot, circle, winner, others, communityCards, leftCard);
        this.pots.add(ip);
    }

    /**
     * 购买保险
     */
    public Insurance buy(int potId, int amount, List<Card> outs) {
        var pot = this.pots.stream()
                .filter(v -> v.getId() == potId && !v.finished())
                .findFirst().get();
        pot.buy(new BigDecimal(amount), outs);
        return this;
    }

    /**
     * 一圈保险购买结束
     */
    public Insurance end() {
        for (var v : pots) {
            if (!v.finished() && v.circle.equals(circle)) {
                v.buy(BigDecimal.ZERO, v.outs);
            }
        }
        if (Circle.TURN.equals(this.circle)) {
            this.circle = Circle.RIVER;
        }
        return this;
    }

    /**
     * 是否全部购买结束
     */
    public boolean finished() {
        return Circle.TURN.equals(this.circle) && this.pots.stream().allMatch(InsurancePot::finished);
    }

    public static BigDecimal odds(int count) {
        if (count <= 0 || count > 14) {
            return BigDecimal.ZERO;
        }
        return ODDS[count];
    }
}
