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

    public static final int FANTASY_CARD_NUM = 14;

    public static final int SUM_CARD_NUM = 17;

    public static final String STATE_NEXT_OP = "STATE_NEXT_OP";

    public static final String STATE_CIRCLE_END = "STATE_CIRCLE_END";

    public static final String STATE_SHOWDOWN = "STATE_SHOWDOWN";

    public static final String STATE_OPEN = "STATE_OPEN";

    public static class Builder {
        private int playerNum = 2;

        private Ring<Plate> ring;

        private Integer dealer;

        private Map<Regulation, Integer> regulations = new HashMap<>();

        private Map<Integer, Card[]> playerCards = new HashMap<>();

        private Set<Integer> fantasy = new HashSet<>();

        public Builder dealer(Integer dealer) {
            this.dealer = dealer;
            return this;
        }

        public Builder fantasy(Integer id) {
            fantasy.add(id);
            return this;
        }

        public Builder beforehand() {
            regulations.put(Regulation.Beforehand, 1);
            return this;
        }

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
                    List<Card> left;
                    if (this.playerCards.containsKey(i)) {
                        left = Arrays.asList(this.playerCards.get(i));
                        all = removeCard(all, left);
                    } else {
                        left = all.subList(0, SUM_CARD_NUM);
                        all = all.subList(SUM_CARD_NUM, all.size());
                    }
                    ring.value = new Plate(i, left);
                    ring = ring.getNext();
                }
            }
            if (regulations == null) {
                regulations = new HashMap<>();
            }
            if (dealer == null) {
                dealer = 0;
            }
            var pineapple = new Pineapple(ring, regulations, fantasy, dealer);
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

    private Integer dealer;

    private Set<Integer> fantasy;

    private Set<Integer> beforehand = new HashSet<>();

    private Pineapple(Ring<Plate> ring,
                      Map<Regulation, Integer> regulations,
                      Set<Integer> fantasy,
                      Integer dealer) {
        this.ring = ring;
        this.regulations = regulations;
        this.fantasy = fantasy;
        this.dealer = dealer;
        if (ring.toList().stream().anyMatch(v -> v.getLeft().size() != SUM_CARD_NUM)) {
            throw new IllegalArgumentException();
        }
    }

    void start() {
        this.circleStart();
    }

    String circleStart() {
        this.circle++;

        // 同时发牌
        if (this.beforehand()) {
            var num = this.giveCardNum();
            this.ring.toList().stream()
                    .filter(v -> !fantasy.contains(v.getId()))
                    .forEach(v -> v.deal(num));
        }
        this.ring = this.ring.move(v -> v.getId().equals(dealer));
        return this.transit();
    }

    void circleEnd() {
    }

    String action(Integer id, List<RowCard> rows) {
        if (this.isOver
                || rows == null
                || this.getPlayerById(id) == null
                || (!id.equals(this.opPlayer()) && !this.beforehand())) {
            throw new IllegalArgumentException();
        }

        // 记录
        var before = !this.opPlayer().equals(id);
        var plate = this.getPlateById(id);
        plate.put(rows, before, this.fantasy.contains(id), this.chooseCardNum());

        // 提前摆牌记录
        if (before) {
            this.beforehand.add(id);
            return null;
        }

        return this.transit();
    }

    String action() {
        var id = this.opPlayer();
        if (!this.beforehand.contains(id)) {
            throw new IllegalStateException();
        }
        this.getPlateById(id).open();
        this.beforehand.remove(id);
        return this.transit();
    }

    private String transit() {
        String state = null;
        var leftNum = this.playerNum() - this.finishTurnNum();
        // 如果剩余人数到了范特西，那肯定就是一圈结束(leftNum == 0)或者是范特西摆牌
        if (leftNum <= this.fantasy.size()) {
            if (this.playerNum() == this.fantasy.size() || this.circle == 5) {
                if (leftNum > 0) {
                    this.nextOp(true);
                    state = STATE_NEXT_OP;
                } else {
                    this.showdown();
                    state = STATE_SHOWDOWN;
                }
            } else {
                this.circleEnd();
                this.circleStart();
                state = STATE_CIRCLE_END;
            }
        } else {
            this.nextOp(false);
            state = STATE_NEXT_OP;
        }

        // 触发提前摆牌
        if (this.beforehand.contains(this.opPlayer())) {
            state = STATE_OPEN;
        }
        return state;
    }

    void nextOp(boolean fantasy) {
        if (!fantasy) {
            if (this.playerNum() == 3) {
                this.ring = ring.move(v -> !this.fantasy.contains(v.getId()));
            } else if (this.fantasy.isEmpty() || this.fantasy.contains(this.opPlayer())) {
                // 两个有范特西的话，非范特西一直摆牌
                this.ring = ring.getNext();
            }

            // 发牌
            if (!this.beforehand()) {
                var plate = this.getPlateById(this.opPlayer());
                plate.deal(this.giveCardNum());
            }
            return;
        }

        this.ring = ring.move(v -> this.fantasy.contains(v.getId()));
        this.ring.value.deal(FANTASY_CARD_NUM);
    }

    void showdown() {
        this.isOver = true;
    }

    void makeResult() {

    }

    void balanceProfit() {

    }

    boolean fantasyEnable() {
        return this.fantasy.stream()
                .map(v -> this.getPlateById(v))
                .noneMatch(Plate::isStart);
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
        var com = expect;
        return (int) this.ring.toList()
                .stream()
                .filter(v -> v.notConcurrentNum() >= com)
                .count();
    }

    boolean beforehand() {
        return this.regulations.containsKey(Regulation.Beforehand);
    }

    List<Card> getRowCards(int id, int channel) {
        if (this.getPlayerById(id) == null) {
            throw new IllegalArgumentException();
        }
        return this.getPlateById(id).getRowCards(channel);
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
        return this.getPlateById(id).getId();
    }

    Plate getPlateById(Integer id) {
        return this.ring.toList().stream()
                .filter(v -> v.getId().equals(id))
                .findFirst()
                .orElseThrow();
    }

    Integer opPlayer() {
        return this.ring.value.getId();
    }

    List<Card> getFolds(Integer id) {
        return this.getPlateById(id).getFolds();
    }

    List<Card> getWaits(Integer id) {
        return this.getPlateById(id).getWaits();
    }

    Set<Integer> getFantasy() {
        return this.fantasy;
    }

    /**
     * 备忘录
     * <p>
     * 已经看到的牌
     */
    List<Card> getMemo(Integer id) {
        var see = new ArrayList<Card>();
        for (var plate : this.ring.toList()) {
            if (plate.getId().equals(id)) {
                see.addAll(plate.getFolds());
                see.addAll(plate.getWaits());
                see.addAll(plate.getLayout().stream().map(v -> v.card).collect(Collectors.toList()));
            } else {
                see.addAll(plate.getLayout().stream()
                        .filter(v -> !v.beforehand)
                        .map(v -> v.card)
                        .collect(Collectors.toList()));
            }
        }

        var all = TableCard.getInstance().getAll();
        return Card.removeList(all, see);
    }
}
