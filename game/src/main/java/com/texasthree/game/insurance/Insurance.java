package com.texasthree.game.insurance;

import com.texasthree.game.texas.Card;
import com.texasthree.game.texas.Circle;
import com.texasthree.game.texas.Divide;
import com.texasthree.game.texas.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 保险
 *
 * @author: neo
 * @create: 2022-09-11 18:21
 */
public class Insurance {
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
        // 赢家
        players.forEach(v -> v.getHand().fresh(communityCards));
        var winners = Player.winners(players);
        if (winners.size() != 1) {
            return;
        }

        // 保险池
        var wid = winners.get(0).getId();
        var ip = new InsurancePot(pot.id, pot.getChips(), pot.chipsBet(wid), circle, wid, players, communityCards, leftCard);
        this.pots.add(ip);
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
            // 自动购买
            if (!v.finished() && v.circle.equals(circle)) {
                v.buy(v.getMin(), v.getOuts());
            }
        }

        if (Circle.TURN.equals(this.circle)) {
            for (var v : pots) {
                // 如果购买了转牌的保险，系统会强制玩家购买购买河牌的少量保险，背回转牌的保险投入。
                if (v.circle.equals(Circle.TURN)
                        && v.getAmount() > 0
                        && !v.hit()) {
                    var river = this.pots.stream()
                            .filter(p -> p.circle.equals(Circle.RIVER) && p.id == v.id)
                            .findFirst().get();
                    river.setDebt(v.getAmount());
                }
            }
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
        return Circle.RIVER.equals(this.circle) && this.circleFinished();
    }

    /**
     * 这一轮是否够购买结束
     */
    public boolean circleFinished() {
        return this.pots.stream()
                .filter(v -> v.circle.equals(this.circle))
                .allMatch(InsurancePot::finished);
    }

    public List<InsurancePot> getPots() {
        return new ArrayList<>(pots);
    }

    public List<Player> getPlayers() {
        return new ArrayList<>(players);
    }

    public List<Card> getCommunityCards() {
        if (finished()) {
            return this.leftCard.subList(0, 5);
        }
        return Circle.TURN.equals(circle)
                ? this.leftCard.subList(0, 3)
                : this.leftCard.subList(0, 4);
    }

    public String getCircle() {
        return circle;
    }
}
