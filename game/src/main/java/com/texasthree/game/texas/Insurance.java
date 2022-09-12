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

    static final BigDecimal[] ODDS = new BigDecimal[21];

    static {
        String[] init = {"0", "31", "16", "10", "8", "6", "5", "4", "3.5", "3", "2.5", "2.3", "2", "1.8", "1.6",
                "1.4", "1.3", "1.2", "1.1", "1", "0.8"};
        for (var i = 0; i < init.length; i++) {
            ODDS[i] = new BigDecimal(init[i]);
        }
    }

    /**
     * 正在购买保险的圈
     */
    private String circle;
    /**
     * 公共牌
     */
    private final List<Card> communityCards;
    /**
     * 剩余的牌
     */
    private final List<Card> leftCard;
    /**
     * 参与的玩家
     */
    private final List<Player> players;
    /**
     * 分池
     */
    private final List<InsurancePot> pots = new ArrayList<>();

    Insurance(List<Player> players, List<Card> communityCards, List<Card> leftCard, String circle, List<Divide> pots) {
        this.players = players;
        this.circle = circle;
        this.communityCards = communityCards;
        this.leftCard = leftCard;

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
            if (pot.getMembers().size() > 3) {
                continue;
            }

            // 河牌保险
            this.init(pot, players, cc5, lc5, Circle.RIVER);
            if (Circle.FLOP.equals(circle)) {
                // 转牌保险
                this.init(pot, players, cc4, lc4, Circle.TURN);
            } else if (!Circle.TURN.equals(circle)) {
                throw new IllegalArgumentException("circle 错误: " + circle);
            }
        }
    }

    private void init(Divide pot, List<Player> players, List<Card> communityCards, List<Card> leftCard, String circle) {
        players = players.stream()
                .filter(v -> pot.getMembers().containsKey(v.getId()))
                .collect(Collectors.toList());
        players.forEach(v -> v.getHand().fresh(communityCards));
        var winners = Texas.winners(players);
        if (winners.size() != 1) {
            return;
        }
        var winner = winners.get(0);
        var others = players.stream()
                .filter(p -> p.getId() != winner.getId())
                .collect(Collectors.toList());
        var ip = new InsurancePot(pot, circle, winner, others, communityCards, leftCard);
        this.pots.add(ip);

    }

    public static BigDecimal odds(int count) {
        if (count <= 0 || count > 14) {
            return BigDecimal.ZERO;
        }
        return ODDS[count];
    }

    private int buyIndex() {
        if (Circle.PREFLOP.equals(this.circle) || Circle.FLOP.equals(this.circle)) {
            return 4;
        } else if (Circle.TURN.equals(this.circle)) {
            return 5;
        } else {
            throw new IllegalStateException();
        }
    }
}
