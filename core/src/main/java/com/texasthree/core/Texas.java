package com.texasthree.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Texas {


    private String id;
    /**
     * 是否结束
     */
    private boolean isOver;
    /**
     * 底池
     */
    private Pot pot;

    private final int playerNum = 1;

    private List<Card> leftCard;

    private Circle circle;

    private Map<String, Integer> playing = new HashMap<>();

    private Map<Law, Integer> laws;

    private Ring<Player> ring;

    public Texas(Map<Law, Integer> laws, Ring<Player> playerList, List<Card> leftCard) {
        this.laws = laws;
        this.ring = playerList;
        this.leftCard = leftCard;
    }

    /**
     * 开始
     */
    public void start() {
        this.pot = new Pot();

        // 一圈开始
        this.circleStart();

        // 前注
        this.actionAnte();

        // 移动到操作位
        this.nextOp();

        if (this.getSmallBlind() > 0) {
            this.actionBlind();
        } else if (this.getAnte() > 0 && this.playing.containsKey(Law.DoubleAnte.name())) {
            this.actionDealerAnte();
        }

        // 轮转
        this.turn();

        // 强制盲注
        if (this.straddleEnable()) {
            this.actionStraddle();
        }
    }

    private Move action(Action action) {
        if (this.isOver) {
            return null;
        }

        this.pot.action();

        Move move = this.turn();

        // 如果下一位玩家离开则自动弃牌
        if (Move.NextOp.equals(move) && this.getOpPlayer().getLeave()) {
            move = this.action(new Action(Optype.Fold));
        }

        return move;
    }

    /**
     * 操作位轮转
     */
    private Move turn() {
        Move move = Move.NextOp;
        int leftNum = this.playerNum - this.pot.allinNum() - this.pot.foldNum();
        Player opNext = this.nextOpPlayer(this.getOpPlayer().getId());
        int standard = this.pot.getStandard();
        if ((this.playerNum - this.leaveOrFoldNum() == 1)
                || (leftNum == 1 && standard == this.chipsThisCircle(opNext.getId()))
                || leftNum == 0) {
            move = Move.Showdown;
        } else {
            if (opNext != null && opNext.getId() == this.pot.getStandardId()) {
                if (Circle.River.equals(this.circle) || leftNum <= 1) {
                    move = Move.Showdown;
                }
            } else if (this.preflopOnceAction(standard, opNext)) {
                Player player = this.nextOpPlayer(opNext.getId());
                Action action = this.pot.getAction(player.getId());
                this.pot.setStandardInfo(action.getChipsBet(), player.getId());
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
        if (!Circle.Preflop.equals(this.getCircle())) {
            return false;
        }

        // 1. 第一圈, 有小盲, 开始全跟注, 轮到大盲, 大盲还有一次说话机会
        if (this.getSmallBlind() > 0 && standard == this.getSmallBlind() * 2 && this.getBbPlayer() == opNext) {
            return true;
        }

        // 2. 第一圈, 没有小盲, 开始全跟注/或, 轮到庄家, 庄家还有一次说话机会
        if (this.getSmallBlind() == 0 && this.getDealer() == opNext) {
            if (this.playing.containsKey(Law.DoubleAnte.name()) && standard == this.getAnte()) {
                return true;
            }
            if (!this.playing.containsKey(Law.DoubleAnte.name()) && standard == 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * 找到紧挨 id 的下一个没有allin 和 fold 的座位
     */
    private Player nextOpPlayer(int id) {
        if (this.pot.notFoldNum() == this.pot.allinNum()) {
            return null;
        }
        Ring<Player> r = new Ring<>();
        while (r.getPrev().getValue().getId() != id) {
            r = r.getNext();
        }
        int standardId = this.pot.getStandardId();
        while ((this.pot.isFold(r.getValue().getId()) || this.pot.isAllin(r.getValue().getId()))
                && r.getValue().getId() != standardId) {
            r = r.getNext();
        }
        return r.getValue();
    }

    private int chipsThisCircle(int id) {
        return 0;
    }

    private int leaveOrFoldNum() {
        return 1;
    }

    /**
     * 前注
     */
    private void actionAnte() {

    }

    /**
     * 盲注
     */
    private void actionBlind() {

    }

    private void actionStraddle() {

    }

    /**
     * 庄家前注
     */
    private void actionDealerAnte() {

    }

    private void circleStart() {
        this.pot.circleStart();

        this.freshHand();

        if (this.getOpPlayer().getLeave()) {
            this.action(new Action(Optype.Fold));
        }
    }

    private void freshHand() {
//        local board = self:Board()
//        for _, player in self:PlayerRing():Iterator() do
//            player:Handhold():Fresh(board)
//        end
    }


    private void nextOp() {

    }

    private void circleEnd() {
        this.pot.circleEnd(this.getBoard());

//        -- 从庄家下一位开始
//        local opPlayer = self:NextOpPlayer(self:Dealer():Id())
//        if opPlayer ~= nil then
//           self.playerRing = self.playerRing:MoveTo(function(v) return v == opPlayer end)
//        end
    }

    private void showdown() {

        this.isOver = true;

        this.pot.showdown();

        this.freshHand();
    }

    /**
     * 玩家离开
     */
    public Move playerLeave(String id) {
        Player player = this.getPlayer(id);
        if (player.getLeave()) {
            return null;
        }

        player.getLeave();

        if (this.isOver) {
            return null;
        }

        // 如果是正在押注玩家直接弃牌
        if (this.getOpPlayer() == player) {
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
        return this.playing.containsKey(Law.Straddle.name()) && this.playerNum > 3;
    }

    private Player getOpPlayer() {
        return null;
    }

    private Circle getCircle() {
        return null;
    }

    /**
     * 牌面
     */
    private List<Card> getBoard() {
        if (this.isOver && this.isCompareShowdown()) {
            return this.leftCard.subList(0, 5);
        }
        switch (this.getCircle()) {
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
        return true;
    }

    private Player getPlayer(String id) {
        return null;
    }

    private int getSmallBlind() {
        return this.laws.getOrDefault(Law.SmallBlind, 0);
    }

    private int getAnte() {
        return this.laws.getOrDefault(Law.Ante, 0);
    }

    public Player getDealer() {
        return null;
    }

    public Player getSbPlayer() {
        return null;
    }

    public Player getBbPlayer() {
        return null;
    }
}
