package com.texasthree.round.texas;

import com.texasthree.round.TableCard;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * 德扑牌局
 */
public class Texas {
    /**
     * 轮到下一个操作位
     */
    public static final String NEXT_OP = "NEXT_OP";

    public static final String CIRCLE_END = "CIRCLE_END";

    public static final String SHOWDOWN = "SHOWDOWN";
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
     * 桌面
     */
    private List<Card> board;
    /**
     * 规则
     */
    private Map<Regulation, Integer> regulations;
    /**
     * 玩家的位置
     */
    private Ring<Player> ring;

    static Texas.Builder builder() {
        return new Texas.Builder();
    }

    static Texas.Builder builder(int playerNum) {
        var builder = new Texas.Builder();
        builder.playerNum = playerNum;
        return builder;
    }

    public static class Builder {
        private int playerNum = 2;
        private int smallBlind = 1;
        private int ante = 0;
        private int initChips = 100;
        private boolean straddle;
        private Ring<Player> ring;
        private List<Card> board;
        private Map<Regulation, Integer> regulations;
        private List<Player> players;

        public Builder() {

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

        public Builder straddle() {
            this.straddle = true;
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

        public Builder regulation(Regulation regulation, Integer value) {
            if (this.regulations == null) {
                this.regulations = new HashMap<>();
            }
            this.regulations.put(regulation, value);
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

        public Builder board(List<Card> board) {
            this.board = board;
            return this;
        }

        public Builder board(Card... board) {
            this.board = Arrays.asList(board);
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

            this.deal();

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
            if (straddle) {
                regulations.put(Regulation.Straddle, 0);
            }

            this.ring = this.ring.move(v -> v.getId() == regulations.get(Regulation.Dealer));

            if (ring.size() == 2) {
                regulations.put(Regulation.SB, this.ring.value.getId());
                regulations.put(Regulation.BB, this.ring.getNext().value.getId());
            } else {
                regulations.put(Regulation.SB, this.ring.getNext().value.getId());
                regulations.put(Regulation.BB, this.ring.getNext().getNext().value.getId());
            }
            return new Texas(regulations, ring, board);
        }

        private void deal() {
            var leftCard = TableCard.getInstance().shuffle();
            if (board == null || board.size() != 5) {
                board = leftCard.subList(0, 5);
                leftCard = leftCard.subList(5, leftCard.size());
            } else {
                var set = new HashSet<>(board);
                leftCard = leftCard.stream().filter(v -> !set.contains(v)).collect(Collectors.toList());
            }

            if (ring.value.getHand() == null) {
                for (int i = 1; i <= playerNum; i++) {
                    ring.value.setHand(new Hand(leftCard.subList(i * 2, i * 2 + 2)));
                    ring = ring.getNext();
                }
            }
        }
    }


    private Texas(Map<Regulation, Integer> regulations, Ring<Player> ring, List<Card> leftCard) {
        this.regulations = regulations;
        this.ring = ring;
        this.board = leftCard;
        this.playerNum = ring.size();
    }

    /**
     * 开始
     */
    String start() throws Exception {
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
        this.ring = this.ring.move(v -> v == this.bbPlayer());
    }

    /**
     * 强制盲注
     */
    private String actionStraddle() throws Exception {
        var act = Action.straddleBlind(this.straddleBlind());
        return this.action(act);
    }

    /**
     * 庄家前注
     */
    private void actionDealerAnte() throws Exception {
        this.pot.actionDealerAnte(this.dealer(), this.ante());
    }

    public String action(Action action) throws Exception {
        if (this.isOver) {
            return null;
        }

        // 执行下注
        this.pot.action(this.opPlayer(), action, true);

        // 轮转
        var move = this.turn();

        // 如果下一位玩家离开则自动弃牌
        if (Texas.NEXT_OP.equals(move) && this.opPlayer().isLeave()) {
            move = this.action(Action.fold());
        }

        return move;
    }

    /**
     * 操作位轮转
     */
    private String turn() throws Exception {
        var move = Texas.NEXT_OP;
        var leftNum = this.playerNum - this.pot.allinNum() - this.pot.foldNum();
        var opNext = this.nextOpPlayer(this.opPlayer().getId());
        var standard = this.pot.getStandard();
        if ((this.playerNum - this.leaveOrFoldNum() == 1)
                || (leftNum == 1 && standard == this.chipsThisCircle(opNext.getId()))
                || leftNum == 0) {
            move = Texas.SHOWDOWN;
        } else if (opNext != null && opNext.getId() == this.pot.getStandardId()) {
            if (Circle.RIVER.equals(this.pot.circle()) || leftNum <= 1) {
                move = Texas.SHOWDOWN;
            } else if (this.isPreflopOnceAction(standard, opNext)) {
                var player = this.nextOpPlayer(opNext.getId());
                var action = this.pot.getAction(player.getId());
                this.pot.setStandardInfo(action.chipsBet, player.getId());
            } else {
                move = Texas.CIRCLE_END;
            }
        }

        if (Texas.NEXT_OP.equals(move)) {
            this.nextOp();
        } else if (Texas.CIRCLE_END.equals(move)) {
            this.circleEnd();
            this.circleStart();
        } else {
            this.showdown();
        }
        return move;
    }

    private void circleStart() throws Exception {
        this.pot.circleStart();

        this.freshHand();

        if (this.opPlayer().isLeave()) {
            this.action(Action.fold());
        }
    }

    private void circleEnd() {
        this.pot.circleEnd(this.board());

        // 从庄家下一位开始
        Player op = this.nextOpPlayer(this.dealer().getId());
        ring = this.ring.move(v -> v == op);
    }

    /**
     * 摊牌
     */
    private void showdown() {

        this.isOver = true;

        this.pot.showdown(this.board(), this);

        this.freshHand();
    }

    void makeResult() {
        var result = new Result();
        var open = this.showCardStrategy();
        var allBet = this.pot.mapWhoChipsAll();
        for (var v : this.ring.iterator()) {
            var info = new ResultPlayer();
            info.setId(v.getId());
            info.setBetSum(allBet.getOrDefault(v.getId(), 0));
            info.cardShow = open.contains(v.getId());
            result.playersMap.put(v.getId(), info);
        }

        // 单池返回的钱
        var potback = this.pot.giveback();
        if (potback != null) {
            result.playersMap.get(potback.getId()).potback = potback.getChips();
        }

        // 最后从池里的输赢
        var potWin = divideMoney();
        result.playersMap.forEach((k, v) -> v.pot = potWin.getOrDefault(k, new HashMap<>()));


        // 押注统计
        var stats = this.pot.makeBetStatistic(result.getWinners());
        result.playersMap.forEach((k, v) -> v.statistic = stats.getOrDefault(k, new Statistic()));

    }

    private Map<Integer, Map<Integer, Integer>> divideMoney() {
        this.freshHand();

        var divide = this.divides();
        var ret = new HashMap<Integer, Map<Integer, Integer>>();
        var inGameNum = this.ring.iterator().stream().filter(v -> v.inGame()).count();
        // 只剩下一个玩家没有离开,不用经过比牌,全部给他
        if (inGameNum == 1) {
            var give = new HashMap<Integer, Integer>();
            for (var i = 0; i < divide.size(); i++) {
                give.put(i, divide.get(0).getChips());
            }
            var p = this.getPlayer(v -> v.inGame());
            ret.put(p.getId(), give);
        }

        // 有多个玩家,主池肯定有玩家比牌
        Set<Integer> mainPotWinner = null;
        for (var potId = 0; potId < divide.size(); potId++) {
            var pot = divide.get(potId);
            var members = pot.getMembers()
                    .keySet()
                    .stream()
                    .map(key -> this.getPlayerById(key))
                    .filter(Player::inGame)
                    .collect(Collectors.toSet());

            var index = potId;
            if (!members.isEmpty()) {
                // 有玩家比牌, 正常计算输赢
                var winner = winner(members);
                if (potId == 0) {
                    mainPotWinner = winner;
                }
                var singleWin = this.singlePotWinChips(winner, pot.getChips());
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

    private Map<Integer, Integer> singlePotWinChips(Set<Integer> winner, int sum) {
        // 如果只有一个赢家则全给他
        var ret = new HashMap<Integer, Integer>();
        if (winner.size() == 1) {
            ret.put(winner.stream().findFirst().get(), sum);
            return ret;
        }

        // 个池中的赢家都会分有至少averageChips 个筹码，
        // 如果有 oneMoreNum 个筹码余下，则从小盲位开始分，靠后的玩家没有
        var averageChips = (int) Math.floor(sum / winner.size());
        var oneMoreNum = sum % winner.size();
        var r = this.ring.move(v -> v.getId() == this.bbPlayer().getId());
        for (var v : r.iterator()) {
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

    private Set<Integer> winner(Collection<Player> players) {
        var winner = new HashSet<Integer>();
        Player win = null;
        for (var v : players) {
            if (winner.isEmpty()) {
                winner.add(v.getId());
                win = v;
            } else {
                var com = v.getHand().compareTo(win.getHand());
                if (com >= 1) {
                    win = v;
                    winner = new HashSet<>();
                    winner.add(v.getId());
                } else if (com == 0) {
                    winner.add(v.getId());
                }
            }
        }
        return winner;
    }

    /**
     * 展示手牌
     */
    private Set<Player> showCardStrategy() {
        var show = this.forceShow();
        show.addAll(this.willingShow());
        return show;
    }

    /**
     * 系统强制展示手牌
     */
    private Set<Player> forceShow() {
        // 没有进行比牌, 不用亮
        var show = new HashSet<Player>();
        if (!this.isCompareShowdown()) {
            return show;
        }

        // 所有没弃牌的玩家亮牌
        // 1. 非河牌圈, 因为allin结束
        // 2. 没有自动埋牌策略
        if (!this.regulations.containsKey(Regulation.CoverCard) || this.circle() != Circle.RIVER) {
            for (var v : this.ring.iterator()) {
                if (!this.pot.isAllin(v)) {
                    show.add(v);
                }
            }
            return show;
        }

        // 河牌圈
        var playerRing = this.ring.move(v -> v.equals(this.dealer()));
        var stub = this.pot.lastBetOrRaise();
        for (var divide : this.divides()) {
            var pr = playerRing.move(player -> {
                if (!divide.getMembers().containsKey(player.getId())) {
                    return false;
                }
                return stub != null
                        ? player.getId() == stub
                        : !this.pot.isFold(player);
            });
            var last = pr.value;
            show.add(last);
            for (var player : pr.iterator()) {
                // 大于等于必须亮牌
                if (divide.getMembers().containsKey(player.getId())
                        && !this.pot.isFold(player)
                        && player.getHand().compareTo(last.getHand()) > -1) {
                    show.add(player);
                    last = player;
                }
            }
        }
        return show;
    }

    /**
     * 玩家自愿展示手牌
     * <p>
     * TODO 不属于牌局核心逻辑，应该放在外层
     */
    private Set<Player> willingShow() {
        return new HashSet<>();
    }

    /**
     * Preflop多一次押注
     */
    private boolean isPreflopOnceAction(int standard, Player opNext) {
        if (!Circle.PREFLOP.equals(this.pot.circle())) {
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
        while ((this.pot.isFold(r.value) || this.pot.isAllin(r.value))
                && (standardId == null || r.value.getId() != standardId)) {
            r = r.getNext();
        }
        return r.value;
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
        this.ring = this.ring.move(v -> !this.pot.isAllin(v) && !this.pot.isFold(v));
    }

    private int chipsThisCircle(int id) {
        return this.pot.chipsThisCircle(id);
    }

    private int leaveOrFoldNum() {
        return (int) this.ring.iterator()
                .stream()
                .filter(v -> v.isLeave() || this.pot.isFold(v.getId()))
                .count();
    }

    List<Divide> divides() {
        return this.pot.divides();
    }


    /**
     * 玩家离开
     */
    String leave(Integer id) throws Exception {
        var player = this.getPlayerById(id);
        if (player == null || player.isLeave()) {
            return null;
        }

        player.leave();

        if (this.isOver) {
            return null;
        }

        // 如果是正在押注玩家直接弃牌
        if (this.opPlayer().equals(player)) {
            return this.action(Action.fold());
        }

        if (this.playerNum - this.leaveOrFoldNum() == 1) {
            this.showdown();
            return Texas.SHOWDOWN;
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

    private int straddleBlind() {
        return this.smallBlind() * 4;
    }

    private Player straddler() {
        return this.straddleEnable() ? this.nextOpPlayer(this.bbPlayer().getId()) : null;
    }

    /**
     * 牌面
     */
    List<Card> board() {
        if (this.isOver && this.isCompareShowdown()) {
            return this.board.subList(0, 5);
        }
        switch (this.pot.circle()) {
            case Circle.FLOP:
                return this.board.subList(0, 3);
            case Circle.TURN:
                return this.board.subList(0, 4);
            case Circle.RIVER:
                return this.board.subList(0, 5);
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

    public String circle() {
        return this.pot.circle();
    }

    boolean isOver() {
        return this.isOver;
    }

    /**
     * 获取押注操作权限
     */
    Map<Optype, Integer> auth() {
        var auth = this.pot.auth(this.opPlayer());
        if (this.regulations.containsKey(Regulation.AllinOrFold)) {
            var m = new HashMap<Optype, Integer>();
            m.put(Optype.Allin, auth.get(Optype.Allin));
            m.put(Optype.Fold, auth.get(Optype.Fold));
            auth = m;
        }
        return auth;
    }

    int notFoldNum() {
        return this.pot.notFoldNum();
    }
}
