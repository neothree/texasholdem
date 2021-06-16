package com.texasthree.round.texas;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;

class Pot {

    private final int smallBind;

    private final int ante;

    private final int payerNum;

    private int amplitude;

    private Integer lastBetOrRaise;

    private Integer legalRaiseId;

    private Set<Integer> allin = new HashSet<>();

    private Set<Integer> fold = new HashSet<>();

    private Set<Integer> flopRaise = new HashSet<>();

    private List<Circle> circles = new ArrayList<>();

    private Circle going;

    private Action standardAct;

    private Map<Integer, Integer> anteBet = new HashMap<>();

    private List<Divide> divides;

    private Player giveback;

    Pot(int playerNum, int smallBind, int ante) {
        this.smallBind = smallBind;
        this.ante = ante;
        this.payerNum = playerNum;
    }

    void actionAnte(Ring<Player> ring, int ante) {
        if (ante <= 0) {
            return;
        }
        for (var player : ring.iterator()) {
            if (player.getChips() >= ante) {
                this.anteBet.put(player.getId(), ante);
            } else {
                this.anteBet.put(player.getId(), player.getChips());
                this.allin.add(player.getId());
            }
            player.changeChips(-this.anteBet.get(player.getId()));
            ring = ring.getNext();
        }

        // 分池
        this.divides = this.makeDivide(false);
    }

    void actionBlind(Player sb, Player bb) {
        if (sb == null || bb == null) {
            throw new IllegalArgumentException();
        }
        // 小盲注
        var chipsBet = sb.getChips() > this.smallBind ? this.smallBind : sb.getChips();
        sb.changeChips(-chipsBet);
        var actSb = new Action(sb.getId(), Optype.SmallBlind, chipsBet, chipsBet, sb.getChips(), 0);
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
        } else {
            this.standardAct = actSb;
            this.legalRaiseId = actSb.id;
        }
    }

    void actionDealerAnte(Player dealer, int ante) throws Exception {
        if (dealer.getChips() <= 0) {
            return;
        }
        var chipsBet = dealer.getChips() >= ante ? ante : dealer.getChips();
        dealer.changeChips(-chipsBet);
        var action = new Action(dealer.getId(), Optype.DealerAnte, chipsBet, chipsBet, 0, 0);
        this.action(dealer, action, false);
        this.legalRaiseId = action.id;
    }

    /**
     * 将action中的数据补充完整
     */
    private Action parseAction(Player player, Action action) throws Exception {
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
        var ret = new Action(player.getId(), action.op, chipsBet, chipsAdd, chipsLeft, this.sumPot());
        ret.straddle = action.straddle;
        return ret;

    }

    void action(Player player, Action act, boolean check) throws Exception {
        // 解析
        act = this.parseAction(player, act);
        if (check) {
            var auth = this.auth(player);
            if (!auth.containsKey(act.op) ||
                    (Optype.Raise.equals(act.op) && act.chipsBet < auth.get(act.op))) {
                var str = "auth = " + new ObjectMapper().writeValueAsString(auth) + "\n";
                str = str + "action = " + act;
                throw new IllegalArgumentException("押注错误 " + str);
            }
        }

        if (act.chipsAdd > 0) {
            player.changeChips(-act.chipsAdd);
            if (player.getChips() <= 0) {
                act.op = Optype.Allin;
            }
        }

        // 记录
        this.going.actions.add(act);

        // 标准注
        var amplitude = act.chipsBet - this.getStandard();
        if (this.standardAct == null || amplitude > 0) {
            // 每轮刚开始 standardAct 为 nil, 有人押注后直接为 standardAct
            this.standardAct = act;

            // 翻牌前有加注
            if (Circle.PREFLOP.equals(this.circle())) {
                this.flopRaise.add(player.getId());
            }
            // 加注增幅
            if (amplitude >= this.amplitude) {
                this.amplitude = amplitude;
                this.legalRaiseId = player.getId();
            }
        }

        // 弃牌 和 allin
        if (Optype.Fold.equals(act.op)) {
            this.fold.add(player.getId());
        } else if (Optype.Allin.equals(act.op)) {
            this.allin.add(player.getId());
        }
    }


    /**
     * 一圈开始
     */
    void circleStart() {
        if (Circle.RIVER.equals(circle())) {
            return;
        }

        if (smallBind > 0) {
            this.amplitude = this.smallBind * 2;
        } else {
            this.amplitude = this.ante * 2;
        }

        // 下一圈
        var circle = this.going != null ? nextCircle(this.going.getCircle()) : Circle.PREFLOP;
        this.going = new Circle(circle, this.sumPot(), this.notFoldNum());
        this.circles.add(going);
    }

    /**
     * 一圈结束
     */
    void circleEnd(List<Card> board) {
        this.going.setBoard(board);
        this.divides = this.makeDivide(false);
        this.lastBetOrRaise = this.getStandardId();
        this.standardAct = null;
    }


    void showdown(List<Card> board, Texas texas) {
        this.circleEnd(board);

        var com = texas.isCompareShowdown();
        this.divides = makeDivide(com);
        var last = this.divides.get(divides.size() - 1);
        if (last.getMembers().size() == 1) {
            var id = last.getMembers().keySet().stream().findFirst().get();
            var player = texas.getPlayerById(id);
            if (!player.isLeave() && com) {
                this.givebackLastSinglePlayerPot();
            }
        }

        var record = new Circle();
        record.setPotChips(this.sumPot());
        record.setBoard(board);
        record.setPlayerNum(this.notFoldNum());
        this.circles.add(record);
    }

    /**
     * 计算操作位可以进行的押注动作
     * <p>
     * 1. call 显示数字为需要增加的筹码数
     * 2. 剩下的为 加到的筹码数
     */
    Map<Optype, Integer> auth(Player op) {
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

        if (chipsLeft + opBetChips > this.raiseLine()) {
            ret.put(Optype.Raise, this.raiseLine());
        }


        // 如果有玩家allin没有达到有效raise, 但是这个玩家已经对这个加注call过
        // 此后如果还没有有效加注,再次轮到这个玩家则不能加注
        var onlyCall = false;

        if (onlyCall) {
            // 只能call, 筹码不够call的线则选择allin
            ret.remove(Optype.Allin);
        } else if (maxBetChips > this.raiseLine()) {
            ret.put(Optype.Raise, this.raiseLine());
        }

        // 快捷加注
        if (ret.containsKey(Optype.Raise)) {
            ret.putAll(this.authShortcut(op));
        }
        return ret;
    }

    private Map<Optype, Integer> authShortcut(Player op) {
        var chipsLeft = op.getChips();
        var potSum = this.sumPot();
        var bb = this.smallBind * 2;
        var ret = new HashMap<Optype, Integer>();
        var oldBetChips = this.chipsThisCircle(op.getId());

        // 第一次押注位, 有盲注计算
        if (potSum <= this.smallBind * 3) {
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

    /**
     * 返回最后的单人单池筹码
     */
    private void givebackLastSinglePlayerPot() {
        var putin = this.divides.get(divides.size() - 1).getPutin();
        if (putin.size() == 1) {
            var entry = putin.entrySet().stream().findFirst().get();
            this.giveback = new Player(entry.getKey(), entry.getValue());
        }
    }

    private List<Divide> makeDivide(boolean com) {
        var divides = new ArrayList<Divide>();
        var mapChips = this.mapWhoChipsAll();
        var single = new Divide();
        while (!mapChips.isEmpty()) {
            var min = mapChips.values()
                    .stream()
                    .min(Comparator.comparing(Integer::intValue))
                    .get();

            // 以 min 为分池标准
            for (var key : mapChips.keySet()) {
                mapChips.put(key, mapChips.get(key) - min);

                var putin = single.getPutin();
                putin.put(key, putin.getOrDefault(key, 0) + min);
                if (!this.fold.contains(key)) {
                    single.getMembers().put(key, putin.get(key));
                }
            }
            single.setChips(single.getChips() + mapChips.size() * min);

            // 是否需要添加一个分池
            var append = false;
            for (var v : mapChips.keySet().toArray()) {
                if (mapChips.get(v) == 0) {
                    mapChips.remove(v);
                    // 如果需要比牌, 最后一个单人单池筹码返回
                    if (this.allin.contains(v)
                            || (com && mapChips.size() <= 1)) {
                        append = true;
                    }
                }
            }
            if (append) {
                divides.add(single);
                single = new Divide();
            }
        }
        if (single.getChips() > 0) {
            divides.add(single);
        }
        return divides;
    }

    /**
     * 押注统计信息
     */
    Map<Integer, Statistic> makeBetStatistic(Set<Integer> winners) {
        var ret = new HashMap<Integer, Statistic>();
        var preflop = this.getCircleRecord(Circle.PREFLOP);
        for (var v : preflop.getActions()) {
            var stat = ret.getOrDefault(v.id, new Statistic());
            if (this.isAllin(v.id)) {
                stat.setAllin(true);
                if (winners.contains(v.id)) {
                    stat.setAllinWin(true);
                }
            }

            // 翻牌前加注
            stat.setFlopRaise(flopRaise.contains(v.id));
            ret.put(v.id, stat);
        }

        this.circles.stream()
                .map(Circle::getActions)
                .flatMap(v -> v.stream())
                .forEach(act -> {
                    if (act.isInpot()) {
                        ret.get(act.id).setInpot(true);
                    }
                });
        return ret;
    }

    Action getAction(int id) {
        if (this.going == null) {
            return null;
        }
        for (var i = this.going.actions.size() - 1; i >= 0; i--) {
            Action action = this.going.actions.get(i);
            if (action.id == id) {
                return action;
            }
        }
        return null;
    }

    Map<Integer, Integer> mapWhoChipsAll() {
        var ret = new HashMap<Integer, Integer>();
        for (var circle : Circle.values()) {
            var m = this.mapWhoChipsCircle(circle);
            for (var v : m.entrySet()) {
                ret.put(v.getKey(), ret.getOrDefault(v.getKey(), 0) + v.getValue());
            }
        }

        for (var v : anteBet.entrySet()) {
            ret.put(v.getKey(), ret.getOrDefault(v.getKey(), 0) + v.getValue());
        }

        return ret;
    }

    /**
     * 在 circle 圈所有人的押注数
     */
    private Map<Integer, Integer> mapWhoChipsCircle(String circle) {
        if (circle == null) {
            circle = this.circle();
        }

        var ret = new HashMap<Integer, Integer>();
        var record = this.getCircleRecord(circle);
        if (record == null) {
            return ret;
        }
        for (var i = record.actions.size() - 1; i >= 0; i--) {
            var action = record.actions.get(i);
            if (!ret.containsKey(action.id)) {
                ret.put(action.id, action.chipsBet);
            }
        }
        return ret;
    }

    private Circle getCircleRecord(String circle) {
        if (going != null && this.going.getCircle().equals(circle)) {
            return this.going;
        }
        for (var v : this.circles) {
            if (v.getCircle().equals(circle)) {
                return v;
            }
        }
        return null;
    }

    /**
     * 这一圈的押注数
     */
    int chipsThisCircle(int id) {
        for (var i = this.going.actions.size() - 1; i >= 0; i--) {
            Action act = this.going.actions.get(i);
            if (act.id == id) {
                return act.chipsBet;
            }
        }
        return 0;
    }

    List<Divide> divides() {
        return this.divides;
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

    boolean isFold(Player player) {
        return fold.contains(player.getId());
    }

    boolean isAllin(int i) {
        return allin.contains(i);
    }

    boolean isAllin(Player player) {
        return allin.contains(player.getId());
    }

    String circle() {
        return this.going != null ? this.going.getCircle() : null;
    }

    private static String nextCircle(String c) {
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

    int sumPot() {
        return this.mapWhoChipsAll()
                .values()
                .stream()
                .reduce(Integer::sum)
                .orElse(0);
    }

    int anteSum() {
        return this.anteBet.values()
                .stream()
                .reduce(Integer::sum)
                .orElse(0);
    }

    private int raiseLine() {
        return this.getStandard() + this.amplitude;
    }

    int getStandard() {
        return this.standardAct != null ? standardAct.chipsBet : 0;
    }

    Integer getStandardId() {
        return this.standardAct != null ? standardAct.id : null;
    }

    Player giveback() {
        return this.giveback;
    }

    Integer lastBetOrRaise() {
        return this.legalRaiseId;
    }

    void setStandardInfo(int chips, int id) {
        this.standardAct = new Action(id, null, chips, 0, 0, 0);
    }
}

