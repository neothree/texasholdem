package com.texasthree.round.texas;

import com.texasthree.round.TableCard;

import java.util.*;
import java.util.function.Predicate;

/**
 * 德扑牌局
 */
public class Texas {
    /**
     * 是否结束
     */
    private boolean isOver;
    /**
     * 底池
     */
    private Pot pot;
    /**
     * 玩家人数
     */
    private final int playerNum;
    /**
     * 剩余的牌
     */
    private List<Card> leftCard;
    /**
     * 规则
     */
    private Map<Regulation, Integer> regulations;
    /**
     * 玩家的位置
     */
    private Ring<Player> ring;

    public static Texas.Builder builder() {
        return new Texas.Builder();
    }

    public static Texas.Builder builder(int playerNum) {
        var builder = new Texas.Builder();
        builder.playerNum = playerNum;
        return builder;
    }

    public static class Builder {
        private int playerNum = 2;
        private int smallBlind = 1;
        private int ante = 0;
        private int initChips = 100;
        private Ring<Player> ring;
        private List<Card> leftCard;
        private Map<Regulation, Integer> regulations;
        private List<Player> players;

        public Builder() {

        }

        public Builder(int playerNum) {
            this.playerNum = playerNum;
        }

        public Builder playerNum(int playerNum) {
            this.playerNum = playerNum;
            return this;
        }

        public Builder smallBlind(int smallBlind) {
            this.smallBlind = smallBlind;
            return this;
        }

        public Builder ante(int ante) {
            this.ante = ante;
            return this;
        }

        public Builder initChips(int initChips) {
            this.initChips = initChips;
            return this;
        }

        public Builder ring(Ring<Player> ring) {
            this.ring = ring;
            return this;
        }

        public Builder regulations(Map<Regulation, Integer> regulations) {
            this.regulations = regulations;
            return this;
        }

        public Builder players(List<Player> players) {
            this.players = players;
            return this;
        }

        public Builder players(Player... players) {
            this.players = Arrays.asList(players);
            return this;
        }

        public Builder leftCard(List<Card> leftCard) {
            this.leftCard = leftCard;
            return this;
        }

        public Builder leftCard(Card... leftCard) {
            this.leftCard = Arrays.asList(leftCard);
            return this;
        }

        Texas build() {
            if (ring == null) {
                if (players == null) {
                    players = new ArrayList<>(playerNum);
                    for (int i = 1; i <= playerNum; i++) {
                        players.add(new Player(i, initChips));
                    }
                }
                ring = Ring.create(players.size());
                for (var v : players) {
                    ring.setValue(v);
                    ring = ring.getNext();
                }
            }
            this.playerNum = ring.size();

            if (this.leftCard == null) {
                this.leftCard = TableCard.getInstance().shuffle();
            }
            if (ring.value.getHand() == null) {
                for (int i = 1; i <= playerNum; i++) {
                    ring.value.setHand(new Hand(leftCard.subList(i * 2, i * 2 + 2)));
                    ring = ring.getNext();
                }
                leftCard = leftCard.subList(ring.size(), leftCard.size());
            }

            if (regulations == null) {
                regulations = new HashMap<>();
            }

            if (!regulations.containsKey(Regulation.SmallBlind)) {
                regulations.put(Regulation.SmallBlind, smallBlind);
            }
            if (!regulations.containsKey(Regulation.Ante)) {
                regulations.put(Regulation.Ante, ante);
            }
            if (!regulations.containsKey(Regulation.Dealer)) {
                regulations.put(Regulation.Dealer, 1);
            }

            this.ring = this.ring.move(v -> v.getId() == regulations.get(Regulation.Dealer));

            if (ring.size() == 2) {
                regulations.put(Regulation.SB, this.ring.value.getId());
                regulations.put(Regulation.BB, this.ring.getNext().value.getId());
            } else {
                regulations.put(Regulation.SB, this.ring.getNext().value.getId());
                regulations.put(Regulation.BB, this.ring.getNext().getNext().value.getId());
            }
            return new Texas(regulations, ring, leftCard);
        }
    }


    private Texas(Map<Regulation, Integer> regulations, Ring<Player> ring, List<Card> leftCard) {
        this.regulations = regulations;
        this.ring = ring;
        this.leftCard = leftCard;
        this.playerNum = ring.size();
    }

    /**
     * 开始
     */
    public Move start() throws Exception {
        this.pot = new Pot(playerNum, this.smallBlind(), this.ante());

        // 一圈开始
        this.circleStart();

        // 前注
        this.actionAnte();

        // 转到dealer位
        this.ring = this.ring.move(v -> v == this.dealer());
        this.pot.setStandardInfo(0, this.dealer().getId());

        if (this.smallBlind() > 0) {
            // 盲注
            this.actionBlind();
        } else if (this.ante() > 0 && this.regulations.containsKey(Regulation.DoubleAnte)) {
            // 庄家前注
            this.actionDealerAnte();
        }

        // 轮转
        var move = this.turn();

        // 强制盲注
        if (this.straddleEnable()) {
            move = this.actionStraddle();
        }
        return move;
    }

    public Move action(Action action) throws Exception {
        if (this.isOver) {
            return null;
        }

        var act = this.pot.parseAction(this.opPlayer(), action);
        var auth = this.pot.auth(this.opPlayer());
        if (!auth.containsKey(act.op) ||
                (Optype.Raise.equals(act.op) && act.chipsAdd < auth.get(act.op))) {
            throw new IllegalArgumentException("押注错误");
        }

        // 执行下注
        this.pot.action(this.opPlayer(), act);

        // 轮转
        var move = this.turn();

        // 如果下一位玩家离开则自动弃牌
        if (Move.NextOp.equals(move) && this.opPlayer().isLeave()) {
            move = this.action(new Action(Optype.Fold));
        }

        return move;
    }

    /**
     * 操作位轮转
     */
    private Move turn() throws Exception {
        var move = Move.NextOp;
        var leftNum = this.playerNum - this.pot.allinNum() - this.pot.foldNum();
        var opNext = this.nextOpPlayer(this.opPlayer().getId());
        var standard = this.pot.getStandard();
        if ((this.playerNum - this.leaveOrFoldNum() == 1)
                || (leftNum == 1 && standard == this.chipsThisCircle(opNext.getId()))
                || leftNum == 0) {
            move = Move.Showdown;
        } else if (opNext != null && opNext.getId() == this.pot.getStandardId()) {
            if (Circle.River.equals(this.pot.circle()) || leftNum <= 1) {
                move = Move.Showdown;
            } else if (this.preflopOnceAction(standard, opNext)) {
                var player = this.nextOpPlayer(opNext.getId());
                var action = this.pot.getAction(player.getId());
                this.pot.setStandardInfo(action.chipsBet, player.getId());
            } else {
                move = Move.CircleEnd;
            }
        }

        if (Move.NextOp.equals(move)) {
            this.nextOp();
        } else if (Move.CircleEnd.equals(move)) {
            this.circleEnd();
            this.circleStart();
        } else {
            this.showdown();
        }

        return move;
    }

    /**
     * Preflop多一次押注
     */
    private boolean preflopOnceAction(int standard, Player opNext) {
        if (!Circle.Preflop.equals(this.pot.circle())) {
            return false;
        }

        // 1. 第一圈, 有小盲, 开始全跟注, 轮到大盲, 大盲还有一次说话机会
        if (this.smallBlind() > 0
                && standard == this.smallBlind() * 2
                && this.bbPlayer() == opNext) {
            return true;
        }

        // 2. 第一圈, 没有小盲, 开始全跟注/或, 轮到庄家, 庄家还有一次说话机会
        if (this.smallBlind() == 0 && this.dealer() == opNext) {
            if (this.regulations.containsKey(Regulation.DoubleAnte) && standard == this.ante()) {
                return true;
            }
            if (!this.regulations.containsKey(Regulation.DoubleAnte) && standard == 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * 找到紧挨 id 的下一个没有allin 和 fold 的座位
     */
    private Player nextOpPlayer(int id) {
        if (this.playerNum == this.pot.allinNum() + this.pot.foldNum()) {
            return null;
        }

        var r = this.ring;
        while (r.getPrev().value.getId() != id) {
            r = r.getNext();
        }
        var standardId = this.pot.getStandardId();
        while ((this.pot.isFold(r.value.getId()) || this.pot.isAllin(r.value.getId()))
                && (standardId == null || r.value.getId() != standardId)) {
            r = r.getNext();
        }
        return r.value;
    }

    private int chipsThisCircle(int id) {
        return this.pot.chipsThisCircle(id);
    }

    private int leaveOrFoldNum() {
        // TODO 加上leave数量
        return this.pot.foldNum();
    }

    /**
     * 前注
     */
    private void actionAnte() {
        this.pot.actionAnte(this.ring, this.ante());
    }

    /**
     * 盲注
     */
    private void actionBlind() {
        this.pot.actionBlind(this.sbPlayer(), this.bbPlayer());
        // 模拟操作位是大盲
        this.ring = this.ring.move(v -> v == this.bbPlayer());
    }

    List<Divide> divide() {
        return this.pot.divide();
    }

    private Move actionStraddle() {
        return null;
    }

    /**
     * 庄家前注
     */
    private void actionDealerAnte() throws Exception {
        this.pot.actionDealerAnte(this.dealer(), this.ante());
    }

    private void circleStart() throws Exception {
        this.pot.circleStart();

        this.freshHand();

        if (this.opPlayer().isLeave()) {
            this.action(new Action(Optype.Fold));
        }
    }


    private void circleEnd() {
        this.pot.circleEnd(this.board());

        // 从庄家下一位开始
        Player op = this.nextOpPlayer(this.dealer().getId());
        ring = this.ring.move(v -> v == op);
    }

    private void freshHand() {
        var r = this.ring;
        var board = this.board();
        for (int i = 0; i < playerNum; i++) {
            r.value.getHand().fresh(board);
            r = r.getNext();
        }
    }

    private void nextOp() {
        this.ring = this.ring.move(v -> !this.pot.isAllin(v.getId()) && !this.pot.isFold(v.getId()));
    }

    /**
     * 摊牌
     */
    private void showdown() {

        this.isOver = true;

        this.pot.showdown(this.board(), this);

        this.freshHand();
    }

    /**
     * 玩家离开
     */
    public Move leave(Integer id) throws Exception {
        var player = this.getPlayerById(id);
        if (player == null || player.isLeave()) {
            return null;
        }

        player.leave();

        if (this.isOver) {
            return null;
        }

        // 如果是正在押注玩家直接弃牌
        if (this.opPlayer() == player) {
            return this.action(new Action(Optype.Fold));
        }

        if (this.playerNum - this.leaveOrFoldNum() == 1) {
            this.showdown();
            return Move.Showdown;
        }
        return null;
    }

    Player opPlayer() {
        return this.ring.value;
    }

    private boolean straddleEnable() {
        // 必须三个玩家以上触发
        return this.regulations.containsKey(Regulation.Straddle) && this.playerNum > 3;
    }

    /**
     * 牌面
     */
    private List<Card> board() {
        if (this.isOver && this.isCompareShowdown()) {
            return this.leftCard.subList(0, 5);
        }
        switch (this.pot.circle()) {
            case Flop:
                return this.leftCard.subList(0, 3);
            case Turn:
                return this.leftCard.subList(0, 4);
            case River:
                return this.leftCard.subList(0, 5);
            default:
                return new ArrayList<>();
        }
    }

    boolean isCompareShowdown() {
        return this.isOver && this.playerNum - this.leaveOrFoldNum() > 1;
    }

    int sumPot() {
        return this.pot.sumPot();
    }

    int smallBlind() {
        return this.regulations.getOrDefault(Regulation.SmallBlind, 0);
    }

    int ante() {
        return this.regulations.getOrDefault(Regulation.Ante, 0);
    }

    int anteSum() {
        return this.pot.anteSum();
    }

    Player dealer() {
        var id = this.regulations.get(Regulation.Dealer);
        return this.getPlayer(v -> v.getId() == id);
    }

    Player sbPlayer() {
        var id = this.regulations.get(Regulation.SB);
        return this.getPlayer(v -> v.getId() == id);
    }

    Player bbPlayer() {
        var id = this.regulations.get(Regulation.BB);
        return this.getPlayer(v -> v.getId() == id);
    }

    private Player getPlayer(Predicate<Player> filter) {
        var r = this.ring.move(filter);
        return r != null ? r.value : null;
    }


    Player getPlayerById(Integer id) {
        return this.getPlayer(v -> v.getId() == id);
    }

    Circle circle() {
        return this.pot.circle();
    }

    boolean isOver() {
        return this.isOver;
    }

    Map<Optype, Integer> auth() {
        return this.pot.auth(this.opPlayer());
    }

    int notFoldNum() {
        return this.pot.notFoldNum();
    }
}
