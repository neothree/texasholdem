package com.texasthree.game.texas;

import java.util.*;

public class Circle {

    /**
     * 底牌圈 / 前翻牌圈 - 公共牌出现以前的第一轮叫注
     */
    public static final String PREFLOP = "PREFLOP";
    /**
     * 翻牌圈 - 首三张公共牌出现以后的押注圈
     */
    public static final String FLOP = "FLOP";
    /**
     * 转牌圈 - 第四张公共牌出现以后的押注圈
     */
    public static final String TURN = "TURN";
    /**
     * 河牌圈 - 第五张公共牌出现以后,也即是摊牌以前的押注圈
     */
    public static final String RIVER = "RIVER";
    /**
     * 摊牌圈
     */
    public static final String SHOWDOWN = "STATE_SHOWDOWN";

    private String circle;

    private List<Card> board;

    private int potChips;

    private int playerNum;

    private List<Action> actions = new ArrayList<>();

    private Integer lastBetOrRaise;

    private Integer legalRaiseId;

    private Action standardAct;

    private int amplitude;

    private Set<Integer> flopRaise = new HashSet<>();

    Circle(String circle, int potChips, int playerNum) {
        this.circle = circle;
        this.potChips = potChips;
        this.playerNum = playerNum;
    }

    void start(int smallBind, int ante) {
        if (smallBind > 0) {
            this.amplitude = smallBind * 2;
        } else {
            this.amplitude = ante * 2;
        }
    }

    void end(List<Card> board) {
        this.board = board;
        this.lastBetOrRaise = this.getStandardId();
        this.standardAct = null;
    }

    void actionBlind(Player sb, Player bb, int smallBind) {
        if (sb == null || bb == null) {
            throw new IllegalArgumentException();
        }
        // 小盲注
        var chipsBet = sb.getChips() > smallBind ? smallBind : sb.getChips();
        sb.changeChips(-chipsBet);
        var actSb = new Action(sb.getId(), Optype.SmallBlind, chipsBet, chipsBet, sb.getChips(), 0);
        this.actions.add(actSb);

        // 大盲注
        chipsBet = bb.getChips() > smallBind * 2 ? smallBind * 2 : bb.getChips();
        bb.changeChips(-chipsBet);
        var actBb = new Action(bb.getId(), Optype.BigBlind, chipsBet, chipsBet, bb.getChips(), 0);
        this.actions.add(actBb);

        if (actBb.chipsBet > actSb.chipsBet) {
            this.standardAct = actBb;
            this.legalRaiseId = actBb.id;
        } else {
            this.standardAct = actSb;
            this.legalRaiseId = actSb.id;
        }
    }

    void action(Player player, Action act, int smallBlind, boolean check) {
        // 解析
        act = this.parseAction(player, act);
        if (check) {
            var auth = this.auth(player, smallBlind, act.sumPot);
            if (!auth.containsKey(act.op) ||
                    (Optype.Raise.equals(act.op) && act.chipsBet < auth.get(act.op))) {
                throw new IllegalArgumentException("押注权限错误");
            }
        }

        if (act.chipsAdd > 0) {
            player.changeChips(-act.chipsAdd);
            if (player.getChips() <= 0) {
                act.op = Optype.Allin;
            }
        }

        this.actions.add(act);

        // 标准注
        var amplitude = act.chipsBet - this.getStandard();
        if (this.standardAct == null || amplitude > 0) {
            // 每轮刚开始 standardAct 为 nil, 有人押注后直接为 standardAct
            this.standardAct = act;

            // 翻牌前有加注
            if (Circle.PREFLOP.equals(this.circle)) {
                this.flopRaise.add(player.getId());
            }
            // 加注增幅
            if (amplitude >= this.amplitude) {
                this.amplitude = amplitude;
                this.legalRaiseId = player.getId();
            }
        }
    }

    /**
     * 将action中的数据补充完整
     */
    private Action parseAction(Player player, Action action) {
        if (action.op == null) {
            throw new IllegalArgumentException();
        }
        var chipsBetOld = this.chipsThisCircle(player.getId());
        var chipsBet = chipsBetOld;
        var chipsLeft = player.getChips();
        if (Optype.Raise.equals(action.op) || Optype.DealerAnte.equals(action.op)) {
            chipsBet = chipsBetOld + action.chipsAdd;
        } else if (Optype.Allin.equals(action.op)) {
            chipsBet = chipsBetOld + chipsLeft;
        } else if (Optype.Call.equals(action.op)) {
            chipsBet = this.getStandard();
        }
        var chipsAdd = chipsBet - chipsBetOld;
        if (chipsAdd > player.getChips()) {
            throw new IllegalArgumentException("action=" + action + "\n" + "player=" + player);
        }
        var ret = new Action(player.getId(), action.op, chipsBet, chipsAdd, chipsLeft, action.sumPot);
        ret.straddle = action.straddle;
        return ret;

    }

    int chipsThisCircle(int id) {
        var action = this.getAction(id);
        return action != null ? action.chipsBet : 0;
    }

    /**
     * 计算操作位可以进行的押注动作
     * <p>
     * 1. call 显示数字为需要增加的筹码数
     * 2. 剩下的为 加到的筹码数
     */
    Map<Optype, Integer> auth(Player op, int smallBind, int potSum) {
        var chipsLeft = op.getChips();
        var opBetChips = this.chipsThisCircle(op.getId());
        var maxBetChips = chipsLeft + opBetChips;
        var ret = new HashMap<Optype, Integer>();
        ret.put(Optype.Fold, 0);
        ret.put(Optype.Allin, chipsLeft);

        if (opBetChips == this.getStandard()) {
            ret.put(Optype.Check, 0);
        } else if (maxBetChips > this.getStandard()) {
            ret.put(Optype.Call, this.getStandard() - opBetChips);
        }

        var raiseLine = this.raiseLine();
        if (chipsLeft + opBetChips > raiseLine) {
            ret.put(Optype.Raise, raiseLine);
        }


        // 先屏蔽
        // 如果有玩家allin没有达到有效raise, 但是这个玩家已经对这个加注call过
        // 此后如果还没有有效加注,再次轮到这个玩家则不能加注
        var onlyCall = false;
        Ring<Player> ring = null;
        var act = this.getAction(op.getId());
        if (ring != null && standardAct.chipsBet != opBetChips && act != null && act.op.equals(Optype.Call)) {
            ring.move(v -> v.getId() == this.legalRaiseId);
            while (ring != null && ring.value.getId() != standardAct.id) {
                if (ring.value.getId() == op.getId()) {
                    onlyCall = true;
                }
                ring = ring.getNext();
            }
        }

        if (onlyCall) {
            // 只能call, 筹码不够call的线则选择allin
            ret.remove(Optype.Allin);
        } else if (maxBetChips > raiseLine) {
            ret.put(Optype.Raise, raiseLine);
        }

        // 快捷加注
        if (ret.containsKey(Optype.Raise)) {
            ret.putAll(this.authShortcut(op, smallBind, potSum));
        }
        return ret;
    }

    private Map<Optype, Integer> authShortcut(Player op, int smallBind, int potSum) {
        var chipsLeft = op.getChips();
        var bb = smallBind * 2;
        var ret = new HashMap<Optype, Integer>();
        var oldBetChips = this.chipsThisCircle(op.getId());

        // 第一次押注位, 有盲注计算
        if (potSum <= smallBind * 3) {
            // 2倍大盲
            var bb2 = bb * 2;
            if (chipsLeft < bb2) {
                return ret;
            }
            ret.put(Optype.BBlind2, bb2 + oldBetChips);

            // 3倍大盲
            var bb3 = bb * 3;
            if (chipsLeft < bb3) {
                return ret;
            }
            ret.put(Optype.BBlind3, bb3 + oldBetChips);

            // 4倍大盲
            var bb4 = bb * 4;
            if (chipsLeft < bb4) {
                return ret;
            }
            ret.put(Optype.BBlind4, bb4 + oldBetChips);
            return ret;
        }

        // 底池的计算值是call平后的总值
        var addBase = this.getStandard() - oldBetChips;
        potSum = potSum + addBase;

        // 1/2 底池
        var add1_2 = addBase + Math.ceil(potSum / 2);
        if (chipsLeft < add1_2) {
            return ret;
        }
        ret.put(Optype.Pot1_2, (int) add1_2 + oldBetChips);

        // 2/3 底池
        var add2_3 = addBase + Math.ceil(potSum * 2 / 3);
        if (chipsLeft < add2_3) {
            return ret;
        }
        ret.put(Optype.Pot2_3, (int) add2_3 + oldBetChips);

        // 1倍底池
        var add1_1 = addBase + potSum;
        if (chipsLeft < add1_1) {
            return ret;
        }
        ret.put(Optype.Pot1_1, add1_1 + oldBetChips);
        return ret;
    }

    Action getAction(int id) {
        for (var i = this.actions.size() - 1; i >= 0; i--) {
            var action = this.actions.get(i);
            if (action.id == id) {
                return action;
            }
        }
        return null;
    }

    /**
     * 所有人的押注数
     */
    Map<Integer, Integer> playerBetChips() {
        var ret = new HashMap<Integer, Integer>();
        var actions = new ArrayList<>(this.actions);
        Collections.reverse(actions);
        actions.forEach(v -> ret.putIfAbsent(v.id, v.chipsBet));
        return ret;
    }

    boolean isFlopRaise(Integer id) {
        return this.flopRaise.contains(id);
    }

    static String nextCircle(String c) {
        if (Circle.PREFLOP.equals(c)) {
            return Circle.FLOP;
        } else if (Circle.FLOP.equals(c)) {
            return Circle.TURN;
        } else if (Circle.TURN.equals(c)) {
            return Circle.RIVER;
        } else {
            return Circle.PREFLOP;
        }
    }

    private int raiseLine() {
        return this.getStandard() + this.amplitude;
    }

    Integer lastBetOrRaise() {
        return lastBetOrRaise;
    }

    int getStandard() {
        return this.standardAct != null ? standardAct.chipsBet : 0;
    }

    Integer getStandardId() {
        return this.standardAct != null ? standardAct.id : null;
    }

    void setStandardInfo(Action action) {
        this.standardAct = action;
    }

    String getCircle() {
        return circle;
    }

    List<Action> getActions() {
        return actions;
    }
}
