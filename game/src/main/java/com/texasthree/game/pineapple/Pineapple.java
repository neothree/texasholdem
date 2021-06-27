package com.texasthree.game.pineapple;

import com.texasthree.game.TableCard;
import com.texasthree.game.texas.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 大菠萝
 *
 * @author: neo
 * @create: 2021-06-19 07:11
 */
public class Pineapple {

    private static final int FANTASY_CARD_NUM = 14;

    private static final int SUM_CARD_NUM = 17;

    public static final String NEXT_OP = "NEXT_OP";

    public static final String CIRCLE_END = "CIRCLE_END";

    public static final String SHOWDOWN = "SHOWDOWN";

    public static final String CONTINUE = "CONTINUE";

    public static class Builder {
        private int playerNum = 2;

        private Ring<Plate> ring;

        private Integer dealer;

        Map<Regulation, Integer> regulations;

        private Map<Integer, Card[]> playerCards = new HashMap<>();

        public Builder playerNum(int playerNum) {
            this.playerNum = playerNum;
            return this;
        }

        public Builder playerCards(int id, Card... cards) {
            if (cards.length != SUM_CARD_NUM) {
                throw new IllegalArgumentException();
            }
            playerCards.put(id, cards);
            return this;
        }


        Pineapple build() {
            var all = TableCard.getInstance().shuffle();
            if (ring == null) {
                ring = Ring.create(playerNum);
                for (var i = 0; i < playerNum; i++) {
                    var lane = new Plate();
                    lane.id = i;
                    if (this.playerCards.containsKey(i)) {
                        lane.left = Arrays.asList(this.playerCards.get(i));
                        all = removeCard(all, lane.left);
                    } else {
                        lane.left = all.subList(0, SUM_CARD_NUM);
                        all = all.subList(SUM_CARD_NUM, all.size());
                    }
                    ring.value = lane;
                    ring = ring.getNext();
                }
            }
            if (regulations == null) {
                regulations = new HashMap<>();
            }
            if (dealer == null) {
                dealer = 0;
            }
            var pineapple = new Pineapple(ring, regulations, dealer);
            return pineapple;
        }

        private List<Card> removeCard(List<Card> all, List<Card> others) {
            var set = new HashSet<>(others);
            return all.stream().filter(v -> !set.contains(v)).collect(Collectors.toList());
        }
    }

    static Builder builder() {
        return new Builder();
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
    private Ring<Plate> ring;

    private Map<Integer, Plate> playerMap = new HashMap<>();

    private Integer dealer;

    private Set<Integer> fantasy = new HashSet<>();

    private Set<Integer> continue1 = new HashSet<>();

    private Pineapple(Ring<Plate> ring,
                      Map<Regulation, Integer> regulations,
                      Integer dealer) {
        this.ring = ring;
        this.regulations = regulations;
        this.dealer = dealer;
        for (var v : ring.iterator()) {
            if (v.left.size() != SUM_CARD_NUM) {
                throw new IllegalArgumentException();
            }
            playerMap.put(v.getId(), v);
        }
    }

    void start() {
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
        this.ring = this.ring.move(v -> v.id.equals(dealer));
        return this.turn(null);
    }

    void circleEnd() {
    }

    String action(Integer id, List<RowCard> rows) {
        if (this.isOver
                || rows == null
                || this.getPlayerById(id) == null
                || (!id.equals(this.opPlayer()) && !this.concurrent())) {
            throw new IllegalArgumentException();
        }

        var plate = this.playerMap.get(id);
        var waits = new HashSet<>(plate.waits);

        // 查看牌是否超过channel数量
        int num0 = 0, num1 = 0, num2 = 0;
        for (var v : rows) {
            if (v == null || !waits.contains(v.card)) {
                throw new IllegalArgumentException();
            }
            switch (v.row) {
                case 0:
                    num0++;
                    break;
                case 1:
                    num1++;
                    break;
                case 2:
                    num2++;
                    break;
                default:
                    throw new IllegalArgumentException();
            }
        }
        if (this.fantasy.contains(id)) {
            // 范特西摆牌
            if (num0 != 3 || num1 != 5 || num2 != 5) {
                throw new IllegalArgumentException();
            }
        } else {
            if (num0 + num1 + num2 != this.chooseCardNum(null)
                    || this.getChannelCard(id, 0).size() + num0 > 3
                    || this.getChannelCard(id, 1).size() + num1 > 5
                    || this.getChannelCard(id, 2).size() + num2 > 5) {
                throw new IllegalArgumentException();
            }
        }

        // 记录
        var con = !this.opPlayer().equals(id);
        for (var v : rows) {
            plate.layout.add(new RowCard(v.card, v.row, con));
        }
        this.sortLane(plate.layout);

        // 弃牌
        var set = rows.stream().map(v -> v.card).collect(Collectors.toSet());
        plate.waits.stream()
                .filter(v -> !set.contains(v))
                .forEach(v -> plate.folds.add(v));

        this.playerMap.get(id).waits = new ArrayList<>();

        return this.turn(id);
    }

    private String turn(Integer id) {
        // 提前摆牌记录
        if (id != null && !id.equals(this.opPlayer())) {
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
            } else if (this.fantasy.isEmpty() || this.fantasy.contains(this.opPlayer())) {
                // 两个有范特西的话，非范特西一直摆牌
                this.ring = ring.getNext();
            }

            // 发牌
            if (!this.concurrent()) {
                this.giveCard(this.opPlayer(), this.giveCardNum());
            }
            return;
        }

        this.ring = ring.move(v -> this.fantasy.contains(v.getId()));
        this.giveCard(this.ring.value.getId(), FANTASY_CARD_NUM);
    }

    private String doContinue() {
        var id = this.opPlayer();
        if (!this.continue1.contains(id)) {
            throw new IllegalStateException();
        }
        for (var v : this.playerMap.get(id).layout) {
            v.concurrent = false;
        }
        this.continue1.remove(id);
        return this.turn(id);
    }

    private void giveCard(Integer id, int num) {
        var lane = this.playerMap.get(id);
        lane.waits = this.sort(lane.left.subList(0, num));
        lane.left = lane.left.subList(num, lane.left.size());
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
            if (!this.playerMap.get(v).layout.isEmpty() || !this.playerMap.get(v).waits.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    List<Card> sort(List<Card> cards) {
        cards.sort((a, b) -> a.compareToWithSuit(b));
        return cards;
    }

    List<RowCard> sortLane(List<RowCard> cards) {
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
            for (var v : info.layout) {
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
        for (var v : this.playerMap.get(id).layout) {
            if (v.row == channel) {
                ret.add(v.card);
            }
        }
        return ret;
    }

    int chooseCardNum() {
        if (this.isOver) {
            return 0;
        }
        return this.circle == 1 ? 5 : 2;
    }

    int chooseCardNum(Integer id) {
        if (id != null && this.fantasy.contains(id)) {
            return FANTASY_CARD_NUM;
        }
        return chooseCardNum();
    }

    Integer getPlayerById(Integer id) {
        return this.ring.iterator().stream()
                .filter(v -> v.getId().equals(id))
                .findFirst()
                .orElseThrow()
                .getId();
    }

    Integer opPlayer() {
        return this.ring.value.getId();
    }
}
