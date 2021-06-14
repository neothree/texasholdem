package com.texasthree.round.texas;

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
        for (var i = 0; i < ring.size(); i++) {
            var player = ring.value;
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
            throw new IllegalArgumentException();
        }
        return new Action(player.getId(), action.op, chipsBet, chipsAdd, chipsLeft, this.sumPot());

    }

    void action(Player player, Action act, boolean check) throws Exception {
        // 解析
        act = this.parseAction(player, act);
        if (check) {
            var auth = this.auth(player);
            if (!auth.containsKey(act.op) ||
                    (Optype.Raise.equals(act.op) && act.chipsAdd < auth.get(act.op))) {
                throw new IllegalArgumentException("押注错误");
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
            if (Circle.Preflop.equals(circle)) {
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
        this.records.add(going);
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

        var record = new CircleRecord();
        record.setPotChips(this.sumPot());
        record.setBoard(board);
        record.setPlayerNum(this.notFoldNum());
        this.records.add(record);
    }

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

    List<Divide> divide() {
        return this.divides;
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
    private Map<Integer, Integer> mapWhoChipsCircle(Circle circle) {
        if (circle == null) {
            circle = this.circle;
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

    Map<Integer, Statistic> makeBetStatistic(Set<Integer> winners) {
        return null;
    }

    private CircleRecord getCircleRecord(Circle circle) {
        if (going != null && this.going.getCircle().equals(circle)) {
            return this.going;
        }
        for (var v : this.records) {
            if (v.getCircle().equals(circle)) {
                return v;
            }
        }
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

    void setStandardInfo(int chips, int id) {
        this.standardAct = new Action(id, null, chips, 0, 0, 0);
    }
}

