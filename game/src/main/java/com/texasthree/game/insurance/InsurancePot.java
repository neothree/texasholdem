package com.texasthree.game.insurance;

import com.texasthree.game.texas.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static com.texasthree.game.insurance.Insurance.odds;

/**
 * 保险池
 *
 * @author: neo
 * @create: 2022-09-12 14:59
 */
public class InsurancePot {
    /**
     * 底池数据
     */
    private final Divide pot;

    /**
     * 押注圈
     */
    public final String circle;

    /**
     * 赢家，购买保险的玩家
     */
    public final Player winner;

    /**
     * 所有的outs
     */
    private final List<Card> outs;

    /**
     * 购买金额上限
     */
    private int limit;
    /**
     * 保单
     */
    public final List<policy> policies = new ArrayList<>();

    private List<Card> leftCard;

    InsurancePot(Divide pot, String circle, Player winner, List<Player> others, List<Card> communityCards, List<Card> leftCard) {
        this.pot = pot;
        this.circle = circle;
        this.winner = winner;
        this.leftCard = leftCard;
        // 转牌圈不能超过底池的0.25, 河牌圈不能超过底池的0.5
        this.limit = Circle.RIVER.equals(circle) ? (int) Math.floor(pot.getChips() * 0.5) : (int) Math.floor(pot.getChips() * 0.25);
        this.outs = new ArrayList<>();
        others.add(winner);
        for (var v : leftCard) {
            var extra = new ArrayList<>(communityCards);
            extra.add(v);
            for (var p : others) {
                p.getHand().fresh(extra);
            }
            var ws = Texas.winners(others);
            if (ws.stream().noneMatch(w -> w.getId() == winner.getId())) {
                outs.add(v);
            } else if (Circle.RIVER.equals(circle) && ws.size() > 1) {
                // 河牌圈有平分底池的outs, 购买的总额不能超过玩家的投注额
                this.limit = chipsBet();
            }
        }
    }

    /**
     * 买保险
     *
     * @param amount  金额
     * @param buyOuts 购买的outs
     */
    void buy(BigDecimal amount, List<Card> buyOuts) {
        if (finished()) {
            throw new IllegalArgumentException("保险池已经购买结束");
        }
        if (amount.compareTo(BigDecimal.ZERO) < 0 || amount.intValue() > limit) {
            throw new IllegalArgumentException("购买的保险金额错误: " + amount);
        }

        // 购买的outs保单
        var hit = buyOuts.stream().anyMatch(v -> v.equals(leftCard.get(0)));
        var policy = new policy(amount, buyOuts, hit);
        this.policies.add(policy);

        // 对剩余的outs进行背保
        var notBuy = this.diff(buyOuts);
        if (!notBuy.isEmpty()) {
            hit = notBuy.stream().anyMatch(v -> v.equals(leftCard.get(0)));
            var m = amount.divide(policy.getOdds(), RoundingMode.CEILING);
            this.policies.add(new policy(m, buyOuts, hit));
        }

    }

    /**
     * 找出与不一样的牌
     *
     * @param buyOuts
     * @return
     */
    private List<Card> diff(List<Card> buyOuts) {
        var src = new HashSet<>(this.outs);
        for (var card : buyOuts) {
            if (src.contains(card)) {
                src.remove(card);
            } else {
                throw new IllegalArgumentException("购买的outs异常, 不存在 " + card);
            }
        }
        return new ArrayList<>(src);
    }

    /**
     * 是否激活
     */
    boolean activate() {
        return odds(outs.size()).compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean finished() {
        return !activate() || !this.policies.isEmpty();
    }

    /**
     * 赢家在池中的投注额
     */
    int chipsBet() {
        return this.pot.chipsBet(winner.getId());
    }

    /**
     * 满池
     */
    public int fullPot() {
        return new BigDecimal(this.pot.getChips()).divide(getOdds(), RoundingMode.CEILING).intValue();
    }

    /**
     * 保本
     */
    public int breakEven() {
        return new BigDecimal(chipsBet()).divide(getOdds(), RoundingMode.CEILING).intValue();
    }

    public int getId() {
        return this.pot.id;
    }

    public BigDecimal getOdds() {
        return Insurance.odds(outs.size());
    }

    public int getLimit() {
        return limit;
    }

    public int getChips() {
        return this.pot.getChips();
    }

    public List<Card> getOuts() {
        return new ArrayList<>(outs);
    }

    public int getAmount() {
        return this.policies.stream().mapToInt(v -> v.amount.intValue()).sum();
    }

    @Override
    public String toString() {
        return new StringBuilder()
                .append("id=").append(this.getId())
                .append(", active=").append(activate())
                .append(", circle=").append(circle)
                .append(", winner=").append(winner.getId())
                .append(", limit=").append(limit)
                .append(", policies=").append(policies.size())
                .toString();
    }
}
