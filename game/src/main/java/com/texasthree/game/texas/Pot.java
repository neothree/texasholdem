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

    private List<Divide> divides = new ArrayList<>();

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
            player.minus(give);
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
        dealer.minus(chipsBet);
        this.action(dealer, Optype.DealerAnte, chipsBet, false, false);
    }

    void action(Player player, Optype op, int chipsAdd, boolean straddle, boolean check) {
        this.going.action(player, op, chipsAdd, straddle, this.sumPot(), this.smallBind, check);

        // 弃牌 和 allin
        if (Optype.Fold.equals(op)) {
            this.fold.add(player.getId());
        } else if (Optype.Allin.equals(op)) {
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
        if (last.size() == 1) {
            var player = texas.getPlayerById(last.members().get(0));
            if (player.inGame() && com) {
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
        var putin = this.divides.get(divides.size() - 1);
        if (putin.size() == 1) {
            this.refundPlayer = new Player(putin.members().get(0), putin.getChips());
            this.divides.remove(divides.size() - 1);
        }
    }

    private List<Divide> makeDivide(boolean com) {
        var divides = new ArrayList<Divide>();
        var mapChips = this.playerBetChips();
        var single = new Divide(0);
        while (!mapChips.isEmpty()) {
            var min = mapChips.values()
                    .stream()
                    .min(Comparator.comparing(Integer::intValue))
                    .get();

            // 以 min 为分池金额标准，把投注金额记录在一个池中
            mapChips.forEach((k, v) -> mapChips.put(k, v - min));
            var putin = mapChips.keySet();
            var member = putin.stream()
                    .filter(v -> !this.fold.contains(v))
                    .collect(Collectors.toSet());
            single.add(putin, min, member);

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
                single = new Divide(single.id + 1);
            }
        }
        if (single.getChips() > 0) {
            divides.add(single);
        }
        return divides;
    }

    Settlement settle(Ring<Player> ring, Player bbPlayer, Set<Player> open) {
        var result = new Settlement();
        var allBet = this.playerBetChips();
        for (var v : ring.toList()) {
            var info = new SettlementItem();
            info.setId(v.getId());
            info.setBetSum(allBet.getOrDefault(v.getId(), 0));
            info.cardShow = open.contains(v);
            result.add(info);
        }

        // 单池返回的钱
        var refundPlayer = this.refundPlayer();
        if (refundPlayer != null) {
            result.getPlayer(refundPlayer.getId()).refund = refundPlayer.getChips();
        }

        // 最后从池里的输赢

        var shares = this.share(ring, bbPlayer);
        result.forEach(v -> v.pot = shares.getOrDefault(v.id, new HashMap<>()));

        // 押注统计
        var stats = this.makeBetStatistic(result.getWinners());
        result.forEach(v -> v.statistic = stats.getOrDefault(v.id, new Statistic()));
        return result;
    }

    /**
     * 计算奖池的分成
     */
    private Map<Integer, Map<Integer, Integer>> share(Ring<Player> ring, Player bbPlayer) {
        var divide = this.divides();
        var ret = new HashMap<Integer, Map<Integer, Integer>>();
        var inGameNum = ring.toList().stream().filter(Player::inGame).count();
        // 只剩下一个玩家没有离开,不用经过比牌,全部给他
        if (inGameNum == 1) {
            var give = new HashMap<Integer, Integer>();
            for (var i = 0; i < divide.size(); i++) {
                give.put(i, divide.get(0).getChips());
            }
            var p = ring.move(Player::inGame).value;
            ret.put(p.getId(), give);
            return ret;
        }

        // 有多个玩家,主池肯定有玩家比牌
        Set<Integer> mainPotWinner = null;
        for (var potId = 0; potId < divide.size(); potId++) {
            var pot = divide.get(potId);
            var members = pot.members()
                    .stream()
                    .map(v -> ring.move(p -> p.getId() == v).value)
                    .filter(Player::inGame)
                    .collect(Collectors.toList());

            var index = potId;
            if (!members.isEmpty()) {
                // 有玩家比牌, 正常计算输赢
                var winner = Player.winners(members).stream().map(Player::getId).collect(Collectors.toSet());
                if (potId == 0) {
                    mainPotWinner = winner;
                }
                var singleWin = shareAmount(ring, winner, bbPlayer, pot.getChips());
                singleWin.forEach((k, v) -> {
                    var give = ret.getOrDefault(k, new HashMap<>());
                    give.put(index, v);
                    ret.put(k, give);
                });
            } else {
                // 没有人了，主池玩家平分
                var average = (int) Math.floor(pot.getChips() / mainPotWinner.size());
                mainPotWinner.forEach(k -> {
                    var give = ret.getOrDefault(k, new HashMap<>());
                    give.put(index, average);
                    ret.put(k, give);
                });
            }
        }
        return ret;
    }


    /**
     * 对amount金额的进行分配
     */
    private Map<Integer, Integer> shareAmount(Ring<Player> ring, Set<Integer> winner, Player bbPlayer, int amount) {
        // 如果只有一个赢家则全给他
        var ret = new HashMap<Integer, Integer>();
        if (winner.size() == 1) {
            ret.put(winner.stream().findFirst().get(), amount);
            return ret;
        }

        // 个池中的赢家都会分有至少averageChips 个筹码，
        // 如果有 oneMoreNum 个筹码余下，则从小盲位开始分，靠后的玩家没有
        var averageChips = (int) Math.floor(amount / winner.size());
        var oneMoreNum = amount % winner.size();
        var r = ring.move(v -> v.getId() == bbPlayer.getId());
        for (var v : r.toList()) {
            if (winner.contains(v.getId())) {
                if (oneMoreNum > 0) {
                    ret.put(v.getId(), averageChips + 1);
                    oneMoreNum--;
                } else {
                    ret.put(v.getId(), averageChips);
                }
            }
        }
        return ret;
    }

    /**
     * 押注统计信息
     */
    private Map<Integer, Statistic> makeBetStatistic(Set<Integer> winners) {
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

    private Map<Integer, Integer> playerBetChips() {
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

    int allinNum() {
        return this.allin.size();
    }

    int notFoldNum() {
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

    Action getAction(int id) {
        var cs = new ArrayList<>(circles);
        Collections.reverse(cs);
        for (var v : cs) {
            var act = v.getAction(id);
            if (act != null) {
                return act;
            }
        }
        return null;
    }
}

