package com.texasthree.core;

import java.util.*;

public class Pot {

    private Circle circle;

    private final int smallBind;

    private final int ante;

    private final int payerNum;

    private int amplitude;

    private Integer lastBetOrRaise;

    private Integer legalRaiseId;

    private Set<Integer> allin = new HashSet<>();

    private Set<Integer> fold = new HashSet<>();

    private Set<Integer> flopRaise = new HashSet<>();

    private List<CircleRecord> records = new ArrayList<>();

    private CircleRecord going;

    private Action standardAct;

    private Map<Integer, Integer> anteBet = new HashMap<>();

    private List<Integer> devide;

    Pot(int playerNum, int smallBind, int ante) {
        this.smallBind = smallBind;
        this.ante = ante;
        this.payerNum = playerNum;
    }


    /**
     * 一圈开始
     */
    void circleStart() {
        if (Circle.River.equals(circle)) {
            return;
        }

        // 下一圈
        this.circle = nextCircle(circle);

        if (smallBind > 0) {
            this.amplitude = this.smallBind * 2;
        } else {
            this.amplitude = this.ante * 2;
        }

        this.going = new CircleRecord(this.circle, this.sumPot(), this.notFoldNum());
    }

    /**
     * 一圈结束
     */
    void circleEnd(List<Card> board) {
        // TODO 分池
        this.going.board = board;
        this.records.add(this.going);
        this.lastBetOrRaise = this.getStandardId();
        this.standardAct = null;
    }

    void action(Player player, Action action) {
        Action act = this.parseAction(player, action);
        if (act.chipsAdd > 0) {
            player.changeChips(-act.chipsAdd);
            if (player.getChips() <= 0) {
                action.op = Optype.Allin;
            }
        }
        this.going.actions.add(act);

        int ampl = act.chipsBet - this.getStandard();
        if (this.standardAct == null || ampl > 0) {
            // 每轮刚开始 standardAct 为 nil, 有人押注后直接为 standardAct
            this.standardAct = act;
            // 翻牌前有加注
            if (Circle.Preflop.equals(circle)) {
                this.flopRaise.add(player.getId());
            }
            // 加注增幅
            if (ampl >= this.amplitude) {
                this.amplitude = ampl;
                this.legalRaiseId = player.getId();
            }
        }

        // 记录 弃牌 和 allin
        if (Optype.Fold.equals(act.op)) {
            this.fold.add(player.getId());
        } else if (Optype.Allin.equals(act.op)) {
            this.allin.add(player.getId());
        }
    }

    int sumPot() {
        return -1;
    }

    Action getAction(int id) {
        return null;
    }


    int getStandard() {
        return this.standardAct != null ? standardAct.chipsBet : 0;
    }

    Integer getStandardId() {
        return this.standardAct != null ? standardAct.id : 0;
    }

    void setStandardInfo(int chips, int id) {
        this.standardAct = new Action(id, null, chips, 0, 0, 0);
    }


    void showdown() {

    }

    void actionBlind(Player sb, Player bb) {
        // 小盲注
        int chipsBet = sb.getChips() > this.smallBind ? this.smallBind : sb.getChips();
        sb.changeChips(-chipsBet);
        Action actSb = new Action(sb.getId(), Optype.SmallBlind, chipsBet, chipsBet, sb.getChips(), 0);
        this.going.actions.add(actSb);
        if (sb.getChips() == 0) {
            this.allin.add(sb.getId());
        }

        // 大盲注
        chipsBet = bb.getChips() > this.smallBind * 2 ? this.smallBind * 2 : bb.getChips();
        bb.changeChips(-chipsBet);
        Action actBb = new Action(bb.getId(), Optype.BigBlind, chipsBet, chipsBet, bb.getChips(), 0);
        this.going.actions.add(actBb);
        if (bb.getChips() == 0) {
            this.allin.add(bb.getId());
        }

        if (actBb.chipsBet > actSb.chipsBet) {
            this.standardAct = actBb;
            this.legalRaiseId = actBb.id;
        }  else {
            this.standardAct = actSb;
            this.legalRaiseId = actSb.id;
        }
    }

    void actionAnte(Ring<Player> ring, int ante) {
        for (int i = 0; i < ring.size();i ++) {
            Player player = ring.value;
            if (player.getChips() >= ante) {
                this.anteBet.put(player.getId(), ante);
            } else {
                this.anteBet.put(player.getId(), player.getChips());
                this.allin.add(player.getId());
            }
            player.changeChips(this.anteBet.get(player.getId()));
        }

        // 分池
        this.devide = this.makeDevide();
    }

    private List<Integer> makeDevide() {
        return null;
    }

    int allinNum() {
        return this.allin.size();
    }

    int foldNum() {
        return this.fold.size();
    }

    int notFoldNum() {
        return this.payerNum - foldNum();
    }

    boolean isFold(int i) {
        return fold.contains(i);
    }

    boolean isAllin(int i) {
        return allin.contains(i);
    }

    Circle circle() {
        return circle;
    }

    private static Circle nextCircle(Circle c) {
        if (Circle.Preflop.equals(c)) {
            return Circle.Flop;
        } else if (Circle.Flop.equals(c)) {
            return Circle.Turn;
        } else if (Circle.Turn.equals(c)) {
            return Circle.River;
        } else {
            return Circle.Preflop;
        }
    }

    /**
     * 将action中的数据补充完整
     */
    private Action parseAction(Player player, Action action) {
        int chipsBetOld = this.chipsThisCircle(player.getId());
        int chipsBet = chipsBetOld;
        int chipsLeft = player.getChips();
        if (Optype.Raise.equals(action.op) || Optype.DealerAnte.equals(action.op)) {
            chipsBet = chipsBetOld + action.chipsAdd;
        } else if (Optype.Allin.equals(action.op)) {
            chipsBet = chipsBetOld + chipsLeft;
        } else if (Optype.Call.equals(action.op)) {
            chipsBet = this.getStandard();
        }
        int chipsAdd = chipsBet - chipsBetOld;
        if (chipsAdd > player.getChips()) {
            return null;
        }
        return new Action(player.getId(), action.op, chipsBet, chipsAdd, chipsLeft, this.sumPot());

    }

    /**
     * 这一圈的押注数
     */
    int chipsThisCircle(int id) {
        for (int i = this.going.actions.size() - 1; i > 0; i--) {
            Action act = this.going.actions.get(i);
            if (act.id == id) {
                return act.chipsBet;
            }
        }
        return 0;
    }
}

