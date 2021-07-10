package com.texasthree.game.texas;

import java.util.*;
import java.util.stream.Collectors;

class Pot {

    private final int smallBind;

    private final int ante;

    private final int payerNum;

    private Set<Integer> allin = new HashSet<>();

    private Set<Integer> fold = new HashSet<>();

    private List<Circle> circles = new ArrayList<>();

    private Circle going;

    private Map<Integer, Integer> anteBet = new HashMap<>();

    private List<Divide> divides;

    private Player refundPlayer;

    Pot(int playerNum, int smallBind, int ante) {
        this.smallBind = smallBind;
        this.ante = ante;
        this.payerNum = playerNum;
    }

    void actionAnte(Ring<Player> ring, int ante) {
        if (ante <= 0) {
            return;
        }
        for (var player : ring.toList()) {
            var give = Math.min(player.getChips(), ante);
            this.anteBet.put(player.getId(), give);
            player.changeChips(-give);
            if (player.getChips() == 0) {
                this.allin.add(player.getId());
            }
        }

        // 分池
        this.divides = this.makeDivide(false);
    }

    void actionBlind(Player sb, Player bb) {
        this.going.actionBlind(sb, bb, smallBind);
        if (sb.getChips() == 0) {
            this.allin.add(sb.getId());
        }
        if (bb.getChips() == 0) {
            this.allin.add(bb.getId());
        }
    }

    void actionDealerAnte(Player dealer, int ante) {
        if (dealer.getChips() <= 0) {
            return;
        }
        var chipsBet = Math.min(dealer.getChips(), ante);
        dealer.changeChips(-chipsBet);
        var action = new Action(dealer.getId(), Optype.DealerAnte, chipsBet, chipsBet, 0, 0);
        this.action(dealer, action, false);
    }

    void action(Player player, Action act, boolean check) {
        act.sumPot = this.sumPot();
        this.going.action(player, act, this.smallBind, check);

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

        var circle = this.going != null ? Circle.nextCircle(this.going.getCircle()) : Circle.PREFLOP;
        this.going = new Circle(circle, this.sumPot(), this.notFoldNum());
        this.going.start(this.smallBind, this.ante);
        this.circles.add(going);
    }

    /**
     * 一圈结束
     */
    void circleEnd(List<Card> board) {
        this.going.end(board);
        this.divides = this.makeDivide(false);
    }

    void showdown(List<Card> board, Texas texas) {
        this.circleEnd(board);

        var com = texas.isCompareShowdown();
        this.divides = makeDivide(com);
        var last = this.divides.get(divides.size() - 1);
        if (last.getMembers().size() == 1) {
            var id = last.getMembers().keySet().stream().findFirst().orElseThrow();
            var player = texas.getPlayerById(id);
            if (!player.isLeave() && com) {
                this.refundLastSinglePlayerPot();
            }
        }

        // TODO 记录离开的人数
        var record = new Circle(Circle.SHOWDOWN, this.sumPot(), this.notFoldNum());
        record.end(board);
        this.circles.add(record);
    }

    Map<Optype, Integer> auth(Player op) {
        return this.going.auth(op, smallBind, this.sumPot());
    }

    /**
     * 返回最后的单人单池筹码
     */
    private void refundLastSinglePlayerPot() {
        var putin = this.divides.get(divides.size() - 1).getPutin();
        if (putin.size() == 1) {
            var entry = putin.entrySet().stream().findFirst().get();
            this.refundPlayer = new Player(entry.getKey(), entry.getValue());
            this.divides.remove(divides.size() - 1);
        }
    }

    private List<Divide> makeDivide(boolean com) {
        var divides = new ArrayList<Divide>();
        var mapChips = this.playerBetChips();
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
                putin.putIfAbsent(key, 0);
                putin.compute(key, (k, v) -> v + min);
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
        var preflop = this.circles.stream()
                .filter(v -> v.getCircle().equals(Circle.PREFLOP))
                .findFirst()
                .orElseThrow();
        for (var v : preflop.getActions()) {
            var stat = ret.getOrDefault(v.id, new Statistic());
            if (this.isAllin(v.id)) {
                stat.setAllin(true);
                if (winners.contains(v.id)) {
                    stat.setAllinWin(true);
                }
            }

            // 翻牌前加注
            stat.setFlopRaise(preflop.isFlopRaise(v.id));
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

    Map<Integer, Integer> playerBetChips() {
        var ret = new HashMap<Integer, Integer>();
        var list = circles.stream().map(Circle::playerBetChips).collect(Collectors.toList());
        list.add(anteBet);
        for (var circle : list) {
            for (var entry : circle.entrySet()) {
                ret.putIfAbsent(entry.getKey(), 0);
                ret.computeIfPresent(entry.getKey(), (k, v) -> v + entry.getValue());
            }
        }
        return ret;
    }

    /**
     * 这一圈的押注数
     */
    int chipsThisCircle(int id) {
        return this.going.chipsThisCircle(id);
    }

    List<Divide> divides() {
        return this.divides;
    }

    int allinAndFoldNum() {
        return this.allin.size() + this.fold.size();
    }

    private int notFoldNum() {
        return this.payerNum - this.fold.size();
    }

    boolean isFold(int i) {
        return fold.contains(i);
    }

    boolean isFold(Player player) {
        return fold.contains(player.getId());
    }

    private boolean isAllin(int i) {
        return allin.contains(i);
    }

    boolean isAllin(Player player) {
        return allin.contains(player.getId());
    }

    String circle() {
        return this.going != null ? this.going.getCircle() : null;
    }

    int sumPot() {
        return this.playerBetChips()
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

    int getStandard() {
        return this.going.getStandard();
    }

    Integer getStandardId() {
        return this.going.getStandardId();
    }

    Player refundPlayer() {
        return this.refundPlayer;
    }

    Integer lastBetOrRaise() {
        return this.going.lastBetOrRaise();
    }

    void setStandardInfo(int id) {
        var action = this.going.getAction(id);
        var chips = action != null ? action.chipsBet : 0;
        this.going.setStandardInfo(new Action(id, null, chips, 0, 0, 0));
    }
}

