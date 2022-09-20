package com.texasthree.game.insurance;

import com.texasthree.game.texas.Card;
import com.texasthree.game.texas.Circle;
import com.texasthree.game.texas.Player;

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
     * 池的筹码总额
     */
    public final int sum;

    public final int id;

    /**
     * 赢家在池中的投注额
     */
    public final int chipsBet;
    /**
     * 押注圈
     */
    public final String circle;

    /**
     * 投保人
     */
    public final int applicant;

    /**
     * 所有的outs
     */
    private final List<Card> outs;
    /**
     * 保单
     */
    public final List<Policy> policies = new ArrayList<>();
    /**
     * 剩余的牌
     */
    private List<Card> leftCard;
    /**
     * 出现平局
     */
    private boolean tie;

    InsurancePot(int id, int sum, int chipsBet, String circle, int applicant, List<Player> players, List<Card> communityCards, List<Card> leftCard) {
        this.applicant = applicant;
        this.id = id;
        this.sum = sum;
        this.chipsBet = chipsBet;
        this.circle = circle;
        this.leftCard = leftCard;
        this.outs = new ArrayList<>();
        for (var v : leftCard) {
            var extra = new ArrayList<>(communityCards);
            extra.add(v);
            for (var p : players) {
                p.getHand().fresh(extra);
            }
            var ws = Player.winners(players);
            if (ws.stream().noneMatch(w -> w.getId() == applicant)) {
                outs.add(v);
            } else if (Circle.RIVER.equals(circle) && ws.size() > 1) {
                this.tie = true;
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
        if (amount.compareTo(BigDecimal.ZERO) < 0 || amount.intValue() > getLimit()) {
            throw new IllegalArgumentException("购买的保险金额错误: amount=" + amount + " limit=" + getLimit());
        }
        if (amount.compareTo(BigDecimal.ZERO) > 0 && buyOuts.isEmpty()) {
            buyOuts = new ArrayList<>(outs);
        }

        // 购买的outs保单
        var hit = buyOuts.stream().anyMatch(v -> v.equals(leftCard.get(0)));
        var policy = new Policy(amount, buyOuts, hit);
        this.policies.add(policy);

        // 对剩余的outs进行背保
        var notBuy = this.diff(buyOuts);
        if (!notBuy.isEmpty()) {
            hit = notBuy.stream().anyMatch(v -> v.equals(leftCard.get(0)));
            var m = amount.divide(policy.getOdds(), RoundingMode.CEILING);
            this.policies.add(new Policy(m, buyOuts, hit));
        }

    }

    /**
     * 计算赔付金额
     */
    int claim() {
        return this.activate()
                ? this.policies.stream().mapToInt(Policy::claimAmount).sum()
                : 0;
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
     * 满池
     */
    public int fullPot() {
        return new BigDecimal(this.sum).divide(getOdds(), RoundingMode.CEILING).intValue();
    }

    /**
     * 保本
     */
    public int breakEven() {
        return new BigDecimal(chipsBet).divide(getOdds(), RoundingMode.CEILING).intValue();
    }

    /**
     * 赔率
     */
    public BigDecimal getOdds() {
        return Insurance.odds(outs.size());
    }

    /**
     * 投保额
     */
    public int getAmount() {
        return this.policies.stream().mapToInt(v -> v.amount.intValue()).sum();
    }

    /**
     * 最高投保额
     */
    public int getLimit() {
        // 转牌圈不能超过底池的0.25
        if (Circle.TURN.equals(circle)) {
            return (int) Math.floor(sum * 0.25);
        } else {
            // 河牌圈不能超过底池的0.5
            // 河牌圈有平分底池的outs, 不能超过玩家的投注额
            return tie ? chipsBet : (int) Math.floor(sum * 0.5);
        }
    }

    public int getChips() {
        return this.sum;
    }

    public List<Card> getOuts() {
        return new ArrayList<>(outs);
    }

    @Override
    public String toString() {
        return new StringBuilder()
                .append("id=").append(id)
                .append(", active=").append(activate())
                .append(", circle=").append(circle)
                .append(", applicant=").append(applicant)
                .append(", limit=").append(getLimit())
                .append(", policies=").append(policies.size())
                .toString();
    }
}
