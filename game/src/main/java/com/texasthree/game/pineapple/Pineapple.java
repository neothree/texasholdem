package com.texasthree.game.pineapple;

import com.texasthree.game.texas.*;

import java.util.*;

/**
 * 大菠萝
 *
 * @author: neo
 * @create: 2021-06-19 07:11
 */
public class Pineapple {

    private static final int FANTASY_CARD_NUM = 14;

    public static final String NEXT_OP = "NEXT_OP";

    public static final String CIRCLE_END = "CIRCLE_END";

    public static final String SHOWDOWN = "SHOWDOWN";

    public static final String CONTINUE = "CONTINUE";

    public static class Builder {
        private int playerNum = 2;

        public Builder playerNum(int playerNum) {
            this.playerNum = playerNum;
            return this;
        }

        Pineapple build() {
            throw new IllegalArgumentException();
        }
    }

    private boolean isOver;

    private int circle;

    /**
     * 规则
     */
    private Map<Regulation, Integer> regulations;
    /**
     * 玩家的位置
     */
    private Ring<Player> ring;

    private Map<Integer, Lane> playerMap;

    private Map<Integer, List<Card>> playerCards;

    private Player dealer;

    private Set<Integer> fantasy = new HashSet<>();

    private Set<Integer> continue1 = new HashSet<>();

    Pineapple(Ring<Player> ring,
              Map<Regulation, Integer> regulations,
              Map<Integer, Lane> playerMap,
              Map<Integer, List<Card>> playerCards,
              Player dealer) {
        this.ring = ring;
        this.regulations = regulations;
        this.playerMap = playerMap;
        this.playerCards = playerCards;
        this.dealer = dealer;
    }

    void start() {
        for (var v : ring.iterator()) {
            playerMap.put(v.getId(), new Lane());
        }
        this.circleStart();
    }

    String circleStart() {
        this.circle++;

        // 同时发牌
        if (this.concurrent()) {
            for (var entry : this.playerMap.entrySet()) {
                if (!this.fantasy.contains(entry.getKey())) {
                    this.giveCard(entry.getKey(), this.giveCardNum());
                }
            }
        }
        this.ring = this.ring.move(v -> v.equals(dealer));
        return this.turn(null);
    }

    void circleEnd() {
    }

    void action(Integer id, List<List<Card>> channelList) {
        if (this.isOver
                || this.getPlayerById(id) == null
                || (id != this.opPlayer().getId() && !this.concurrent())) {
            return;
        }

        var waits = new HashSet<>(this.playerMap.get(id).waits);
        if (channelList.stream()
                .flatMap(v -> v.stream())
                .anyMatch(c -> !waits.contains(c))) {
            throw new IllegalArgumentException();
        }

        // 查看牌是否超过channel数量
        if (this.fantasy.contains(id)) {
            // 范特西摆牌
            if (channelList.get(0).size() != 3
                    || channelList.get(1).size() != 5
                    || channelList.get(2).size() != 5) {
                throw new IllegalArgumentException();
            }
        } else {
            if (channelList.stream().mapToLong(v -> v.size()).sum() != this.chooseCardNum(null)
                    || this.getChannelCard(id, 0).size() + channelList.get(0).size() > 3
                    || this.getChannelCard(id, 1).size() + channelList.get(0).size() > 5
                    || this.getChannelCard(id, 2).size() + channelList.get(0).size() > 5) {
                throw new IllegalArgumentException();
            }
        }

        // 记录
        for (var i = 0; i < 3; i++) {
            for (var v : channelList.get(0)) {
                this.playerMap.get(id).hands.add(new LaneCard(v, i, this.opPlayer().getId() != id));
            }
        }
        this.sortLane(this.playerMap.get(id).hands);

        // 弃牌
        channelList.stream()
                .flatMap(v -> v.stream())
                .forEach(v -> this.playerMap.get(id).folds.add(v));
        this.playerMap.get(id).waits.clear();

        this.turn(id);
    }

    private String turn(Integer id) {
        // 提前摆牌记录
        if (id != null && id != this.opPlayer().getId()) {
            this.continue1.add(id);
            return null;
        }

        String move = null;
        var leftNum = this.playerNum() - this.finishTurnNum();
        // 如果剩余人数到了范特西，那肯定就是一圈结束(leftNum == 0)或者是范特西摆牌
        if (leftNum <= this.fantasy.size()) {
            if (this.playerNum() == this.fantasy.size() || this.circle == 5) {
                if (leftNum > 0) {
                    this.nextOp(true);
                    move = NEXT_OP;
                } else {
                    this.showdown();
                    ;
                    move = SHOWDOWN;
                }
            } else {
                this.circleEnd();
                this.circleStart();
                move = CIRCLE_END;
            }
        } else {
            this.nextOp(false);
            move = NEXT_OP;
        }

        // 触发提前摆牌
        if (this.continue1.contains(id)) {
            move = CONTINUE;
        }
        return move;
    }

    void nextOp(boolean fantasy) {
        if (!fantasy) {
            if (this.playerNum() == 3) {
                this.ring = ring.move(v -> this.fantasy.contains(v.getId()));
            } else if (this.fantasy.isEmpty() || this.fantasy.contains(this.opPlayer().getId())) {
                // 两个有范特西的话，非范特西一直摆牌
                this.ring = ring.getNext();
            }

            // 发牌
            if (this.concurrent()) {
                this.giveCard(this.opPlayer().getId(), this.giveCardNum());

            }
            return;
        }

        this.ring = ring.move(v -> this.fantasy.contains(v.getId()));
        this.giveCard(this.ring.value.getId(), FANTASY_CARD_NUM);
    }

    private String doContinue() {
        var id = this.opPlayer().getId();
        if (!this.continue1.contains(id)) {
            throw new IllegalStateException();
        }
        for (var v : this.playerMap.get(id).hands) {
            v.concurrent = false;
        }
        this.continue1.remove(id);
        return this.turn(id);
    }

    private void giveCard(Integer id, int num) {
        var left = this.playerCards.get(id);
        this.playerMap.get(id).waits = this.sort(left.subList(0, num));
        this.playerCards.put(id, left.subList(num, left.size()));
    }

    void showdown() {
        this.isOver = true;
    }

    void makeResult() {

    }

    void balanceProfit() {

    }

    boolean fantasyEnable() {
        if (this.fantasy.isEmpty()) {
            return false;
        }
        for (var v : this.fantasy) {
            if (!this.playerMap.get(v).hands.isEmpty() || !this.playerMap.get(v).waits.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    List<Card> sort(List<Card> cards) {
        cards.sort((a, b) -> a.compareToWithSuit(b));
        return cards;
    }

    List<LaneCard> sortLane(List<LaneCard> cards) {
        cards.sort((a, b) -> a.card.compareToWithSuit(b.card));
        return cards;
    }

    int giveCardNum() {
        if (this.isOver) {
            return 0;
        }
        return this.circle == 1 ? 5 : 3;
    }

    int playerNum() {
        return this.ring.size();
    }

    /**
     * 已经完成这轮摆牌的玩家数量
     */
    int finishTurnNum() {
        var expect = 0;
        switch (this.circle) {
            case 1:
                expect = 5;
                break;
            case 2:
                expect = 7;
                break;
            case 3:
                expect = 9;
                break;
            case 4:
                expect = 11;
                break;
            case 5:
                expect = 13;
                break;
            default:
                throw new IllegalStateException();
        }
        var ret = 0;
        for (var info : this.playerMap.values()) {
            var num = 0;
            for (var v : info.hands) {
                if (!v.concurrent) {
                    num++;
                }
            }
            if (num >= expect) {
                ret++;
            }
        }
        return ret;
    }

    boolean concurrent() {
        return this.regulations.containsKey(Regulation.Concurrent);
    }

    private List<Card> getChannelCard(int id, int channel) {
        var ret = new ArrayList<Card>();
        if (this.getPlayerById(id) == null) {
            return ret;
        }
        for (var v : this.playerMap.get(id).hands) {
            if (v.channel == channel) {
                ret.add(v.card);
            }
        }
        return ret;
    }

    private int chooseCardNum(Integer id) {
        if (this.isOver) {
            return 0;
        }
        if (id != null && this.fantasy.contains(id)) {
            return FANTASY_CARD_NUM;
        }
        return this.circle == 1 ? 5 : 3;
    }

    Player getPlayerById(Integer id) {
        return this.ring.iterator().stream().filter(v -> v.getId() == id).findFirst().orElseThrow();
    }

    Player opPlayer() {
        return this.ring.value;
    }
}
