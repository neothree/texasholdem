package com.texasthree.round.texas;

import com.alibaba.fastjson.JSONObject;
import com.texasthree.round.RoundState;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class Texas {
    /**
     * 是否结束
     */
    private boolean isOver;
    /**
     * 底池
     */
    private Pot pot;

    private final int playerNum;

    private List<Card> leftCard;

    private Map<Law, Integer> laws;

    private Ring<Player> ring;

    public Texas(Map<Law, Integer> laws, Ring<Player> ring, List<Card> leftCard) {
        this.laws = laws;
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

        this.ring = this.ring.move(v -> v == this.dealer());
        this.pot.setStandardInfo(0, this.dealer().getId());

        if (this.smallBlind() > 0) {
            // 盲注
            this.actionBlind();
        } else if (this.ante() > 0 && this.laws.containsKey(Law.DoubleAnte)) {
            // 庄家前注
            this.actionDealerAnte();
        }

        // 轮转
        Move move = this.turn();

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

        Action act = this.pot.parseAction(this.opPlayer(), action);
        Map<Optype, Integer> auth = this.pot.auth(this.opPlayer());
        if (!auth.containsKey(act.op) ||
                (Optype.Raise.equals(act.op) && act.chipsAdd < auth.get(act.op))) {
            System.out.println(JSONObject.toJSONString(auth));
            System.out.println(JSONObject.toJSONString(act));
            throw new Exception("押注错误");
        }


        this.pot.action(this.opPlayer(), act);

        Move move = this.turn();

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
        Move move = Move.NextOp;
        int leftNum = this.playerNum - this.pot.allinNum() - this.pot.foldNum();
        Player opNext = this.nextOpPlayer(this.opPlayer().getId());
        int standard = this.pot.getStandard();
        if ((this.playerNum - this.leaveOrFoldNum() == 1)
                || (leftNum == 1 && standard == this.chipsThisCircle(opNext.getId()))
                || leftNum == 0) {
            move = Move.Showdown;
        } else if (opNext != null && opNext.getId() == this.pot.getStandardId()) {
            if (Circle.River.equals(this.pot.circle()) || leftNum <= 1) {
                move = Move.Showdown;
            } else if (this.preflopOnceAction(standard, opNext)) {
                Player player = this.nextOpPlayer(opNext.getId());
                Action action = this.pot.getAction(player.getId());
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
        if (this.smallBlind() > 0 && standard == this.smallBlind() * 2 && this.bbPlayer() == opNext) {
            return true;
        }

        // 2. 第一圈, 没有小盲, 开始全跟注/或, 轮到庄家, 庄家还有一次说话机会
        if (this.smallBlind() == 0 && this.dealer() == opNext) {
            if (this.laws.containsKey(Law.DoubleAnte) && standard == this.ante()) {
                return true;
            }
            if (!this.laws.containsKey(Law.DoubleAnte) && standard == 0) {
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

        Ring<Player> r = this.ring;
        while (r.getPrev().value.getId() != id) {
            r = r.getNext();
        }
        Integer standardId = this.pot.getStandardId();
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
        Ring<Player> r = this.ring;
        List<Card> board = this.board();
        for (int i = 0; i < playerNum; i++) {
            r.value.getHand().fresh(board);
            r = r.getNext();
        }
    }


    private void nextOp() {
        this.ring = this.ring.move(v -> !this.pot.isAllin(v.getId()) && !this.pot.isFold(v.getId()));
    }

    private void showdown() {

        this.isOver = true;

        this.pot.showdown();

        this.freshHand();
    }

    /**
     * 玩家离开
     */
    public Move playerLeave(String id) throws Exception {
        Player player = this.getPlayer(id);
        if (player.isLeave()) {
            return null;
        }

        player.isLeave();

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

    private boolean straddleEnable() {
        // 必须三个玩家以上触发
        return this.laws.containsKey(Law.Straddle) && this.playerNum > 3;
    }

    public Player opPlayer() {
        return this.ring.value;
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

    private boolean isCompareShowdown() {
        return this.isOver && this.playerNum - this.leaveOrFoldNum() > 1;
    }

    private Player getPlayer(String id) {
        return null;
    }

    public int smallBlind() {
        return this.laws.getOrDefault(Law.SmallBlind, 0);
    }

    public int ante() {
        return this.laws.getOrDefault(Law.Ante, 0);
    }

    public Player dealer() {
        Integer id = this.laws.get(Law.Dealer);
        return this.getPlayer(v -> v.getId() == id);
    }

    public Player sbPlayer() {
        Integer id = this.laws.get(Law.SB);
        return this.getPlayer(v -> v.getId() == id);
    }

    public Player bbPlayer() {
        Integer id = this.laws.get(Law.BB);
        return this.getPlayer(v -> v.getId() == id);
    }

    private Player getPlayer(Predicate<Player> filter) {
        Ring<Player> r = this.ring.move(filter);
        return r != null ? r.value : null;
    }

    public Circle circle() {
        return this.pot.circle();
    }

    public boolean isOver() {
        return this.isOver;
    }

    public Map<Optype, Integer> auth() {
        return this.pot.auth(this.opPlayer());
    }

    public RoundState state() {
        return null;
    }
}
