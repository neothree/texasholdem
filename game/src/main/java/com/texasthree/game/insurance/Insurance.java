package com.texasthree.game.insurance;

import com.texasthree.game.texas.Card;
import com.texasthree.game.texas.Circle;
import com.texasthree.game.texas.Divide;
import com.texasthree.game.texas.Player;

import java.math.BigDecimal;
import java.util.*;
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

    /**
     * 保险圈，只有 TURN、RIVER
     */
    private String circle;
    /**
     * 分池
     */
    private final List<InsurancePot> pots = new ArrayList<>();

    private final List<Player> players;

    private final List<Card> leftCard;

    public Insurance(List<Player> players, List<Card> leftCard, String circle, List<Divide> pots) {
        if (!(Circle.TURN.equals(circle) || Circle.RIVER.equals(circle))) {
            throw new IllegalArgumentException("保险圈错误 " + circle);
        }
        this.players = players;
        this.circle = circle;
        this.leftCard = leftCard;

        // 第四张牌
        var cc4 = leftCard.subList(0, 3);
        var lc4 = leftCard.subList(3, leftCard.size());
        // 第五张牌
        var cc5 = leftCard.subList(0, 4);
        var lc5 = new ArrayList<>(leftCard.subList(4, leftCard.size()));

        for (var pot : pots) {
            // 超过3个人不触发保险
            if (pot.size() > 3 || !pot.compare()) {
                continue;
            }

            // 筛选出池中的玩家
            var ps = players.stream()
                    .filter(v -> pot.contains(v.getId()))
                    .collect(Collectors.toList());
            // 河牌保险
            this.init(pot, ps, cc5, lc5, Circle.RIVER);
            if (Circle.TURN.equals(circle)) {
                // 转牌保险
                this.init(pot, ps, cc4, lc4, Circle.TURN);
            }
        }
    }

    private void init(Divide pot, List<Player> players, List<Card> communityCards, List<Card> leftCard, String circle) {
        // 更新手牌
        players.forEach(v -> v.getHand().fresh(communityCards));

        // 赢家
        var winners = Player.winners(players);
        if (winners.size() != 1) {
            return;
        }

        // 保险池
        var wid = winners.get(0).getId();
        var ip = new InsurancePot(pot.id, pot.getChips(), pot.chipsBet(wid), circle, wid, players, communityCards, leftCard);
        this.pots.add(ip);

        System.out.println(ip);
    }

    /**
     * 购买保险
     */
    public Insurance buy(int potId, int amount, List<Card> outs) {
        var pot = this.pots.stream()
                .filter(v -> v.circle.equals(circle) && v.id == potId && !v.finished())
                .findFirst().get();
        pot.buy(amount, outs);
        return this;
    }

    /**
     * 一圈保险购买结束
     */
    public Insurance end() {
        for (var v : pots) {
            // 没有买的池做0购买
            if (!v.finished() && v.circle.equals(circle)) {
                v.buy(0, v.getOuts());
            }
        }

        if (Circle.TURN.equals(this.circle)) {
            this.circle = Circle.RIVER;
        }
        return this;
    }

    /**
     * 赔付
     */
    public List<Claim> claims() {
        return this.pots.stream()
                .map(InsurancePot::claims)
                .flatMap(Collection::stream)
                .filter(v -> v.profit != 0)
                .collect(Collectors.toList());
    }

    /**
     * 是否全部购买结束
     */
    public boolean finished() {
        return Circle.RIVER.equals(this.circle) && this.pots.stream().allMatch(InsurancePot::finished);
    }

    public boolean circleFinished() {
        return this.pots.stream()
                .filter(v -> v.circle.equals(this.circle))
                .allMatch(InsurancePot::finished);
    }

    static BigDecimal odds(int count) {
        if (count <= 0 || count > 14) {
            return BigDecimal.ZERO;
        }
        return ODDS[count];
    }

    public List<InsurancePot> getPots() {
        return new ArrayList<>(pots);
    }

    public List<InsurancePot> getCirclePots() {
        return this.pots.stream().filter(v -> v.circle.equals(this.circle)).collect(Collectors.toList());
    }

    public List<Player> getPlayers() {
        return new ArrayList<>(players);
    }

    public List<Card> getCommunityCards() {
        if (finished()) {
            return this.leftCard.subList(0, 5);
        }
        return Circle.TURN.equals(circle) ? this.leftCard.subList(0, 3) : this.leftCard.subList(0, 4);
    }

    public String getCircle() {
        return circle;
    }
}
