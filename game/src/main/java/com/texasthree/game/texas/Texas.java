package com.texasthree.game.texas;

import com.texasthree.game.Deck;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * 德扑牌局
 */
public class Texas {
    public static Texas.Builder builder() {
        return new Texas.Builder();
    }


    public static class Builder {
        private int smallBlind = 1;
        private int ante = 0;
        private boolean straddle;
        Ring<Player> ring;
        private List<Card> board;
        private Map<Regulation, Integer> regulations;
        List<Player> players;

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
            this.players(Arrays.asList(players));
            return this;
        }

        public Builder communityCards(Card... board) {
            this.board = Arrays.asList(board);
            return this;
        }

        Ring<Player> getRing() {
            if (ring == null) {
                ring = Ring.create(players.size());
                for (var v : players) {
                    ring.setValue(v);
                    ring = ring.getNext();
                }
            }
            return ring;
        }

        Map<Regulation, Integer> regulations() {
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
            return regulations;
        }

        public Texas build() {
            var ring = this.getRing();
            var leftCards = this.deal();
            var regulations = this.regulations();
            return new Texas(regulations, ring, leftCards);
        }

        List<Card> deal() {
            var leftCard = Deck.getInstance().shuffle();
            if (board == null || board.size() != 5) {
                board = leftCard.subList(0, 5);
                leftCard = leftCard.subList(5, leftCard.size());
            } else {
                var set = new HashSet<>(board);
                leftCard = leftCard.stream().filter(v -> !set.contains(v)).collect(Collectors.toList());
            }

            if (ring.value.getHand() == null) {
                for (int i = 0; i < ring.size(); i++) {
                    ring.value.setHand(new Hand(leftCard.subList(i * 2, i * 2 + 2)));
                    ring = ring.getNext();
                }
            }
            var list = new ArrayList<Card>();
            list.addAll(board);
            list.addAll(leftCard);
            return list;
        }
    }

    /**
     * 是否结束
     */
    private boolean isOver;

    private boolean isStart;
    /**
     * 底池
     */
    private Pot pot;
    /**
     * 玩家人数
     */
    public final int playerNum;
    /**
     * 桌面
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

    private Transfer STATE;

    Texas(Map<Regulation, Integer> regulations, Ring<Player> ring, List<Card> leftCard) {
        var smallBlind = regulations.getOrDefault(Regulation.SmallBlind, 0);
        if (smallBlind < 0) {
            throw new IllegalArgumentException("小盲错误 " + smallBlind);
        }
        var ante = regulations.getOrDefault(Regulation.Ante, 0);
        if (ante < 0) {
            throw new IllegalArgumentException("前注错误 " + smallBlind);
        }
        for (var v : ring.toList()) {
            if (v.getChips() < smallBlind) {
                throw new IllegalArgumentException("玩家筹码数错误 chips=" + v.getChips() + " smallBlind=" + smallBlind);
            }
        }
        this.regulations = regulations;
        this.ring = ring;
        this.leftCard = leftCard;
        this.playerNum = ring.size();
        this.pot = new Pot(playerNum, this.smallBlind(), this.ante());
    }

    /**
     * 开始
     */
    public Texas start() {
        if (this.isStart) {
            throw new IllegalArgumentException();
        }
        this.isStart = true;

        // 一圈开始
        this.circleStart();

        // 前注
        this.actionAnte();

        // 转到dealer位
        this.ring = this.ring.move(v -> v == this.dealer());
        this.pot.setStandardInfo(this.dealer().getId());

        if (this.smallBlind() > 0) {
            // 盲注
            this.actionBlind();
        } else if (this.ante() > 0 && this.regulations.containsKey(Regulation.DoubleAnte)) {
            // 庄家前注
            this.actionDealerAnte();
        }

        // 轮转
        this.transit();

        // 强制盲注
        if (this.straddleEnable()) {
            this.actionStraddle();
        }
        return this;
    }

    Texas action(Optype op) {
        this.action(op, 0, false);
        return this;
    }

    public Texas action(Optype op, int chipsAdd) {
        this.action(op, chipsAdd, false);
        return this;
    }

    /**
     * 玩家离开
     */
    public Texas leave(Integer id) {
        if (this.isOver) {
            return this;
        }

        var player = this.getPlayerById(id);
        if (player == null) {
            throw new IllegalArgumentException(id + " 不存在");
        }
        if (player.isLeave()) {
            return this;
        }

        player.leave();

        // 如果是正在押注玩家直接弃牌
        if (this.operator().equals(player)) {
            this.action(Optype.Fold, 0, false);
        }

        if (this.remainingNum() == 1) {
            this.transit();
        }
        return this;
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
    private void actionStraddle() {
        this.action(Optype.Raise, this.straddleBlind(), true);
    }

    /**
     * 庄家前注
     */
    private void actionDealerAnte() {
        this.pot.actionDealerAnte(this.dealer(), this.ante());
    }


    private Texas action(Optype op, int chipsAdd, boolean straddle) {
        if (this.isOver) {
            throw new IllegalStateException();
        }

        // 执行下注
        this.pot.action(this.operator(), op, chipsAdd, straddle, true);

        // 轮转
        var move = this.transit();

        // 如果下一位玩家离开则自动弃牌
        if (Transfer.NEXT_OP.equals(move) && this.operator().isLeave()) {
            this.action(Optype.Fold, 0, false);
        }

        return this;
    }

    /**
     * 状态转移
     */
    private Transfer transit() {
        Transfer state;
        var waitActionNum = this.waitActionNum();
        var opNext = this.nextOpPlayer(this.operator().getId());
        var standard = this.pot.getStandard();
        if ((this.remainingNum() == 1)
                // 只能下opNext，并且 opNext 已经到达了 standard
                || (waitActionNum == 1 && standard == this.pot.chipsThisCircle(opNext.getId()))
                // 没有可押注玩家
                || waitActionNum == 0) {
            state = Transfer.SHOWDOWN;
        } else if (opNext.getId() == this.pot.getStandardId()) {
            /********************************************/
            /**  进入这里，说明下一个操作人轮到了最高押注位  **/
            /********************************************/
            if (Circle.RIVER.equals(this.pot.circle())) {
                // 已经是最后一圈，摊牌
                state = Transfer.SHOWDOWN;
            } else if (this.isPreflopOnceAction(standard, opNext)) {
                // 在PREFLOP 多一次押注权限
                var player = this.nextOpPlayer(opNext.getId());
                this.pot.setStandardInfo(player.getId());
                state = Transfer.NEXT_OP;
            } else {
                // 这一圈结束
                state = Transfer.CIRCLE_END;
            }
        } else {
            // 下一个操作位进行押注
            state = Transfer.NEXT_OP;
        }

        if (Transfer.NEXT_OP.equals(state)) {
            this.nextOp();
        } else if (Transfer.CIRCLE_END.equals(state)) {
            this.circleEnd();
            this.circleStart();
        } else {
            this.showdown();
        }
        this.STATE = state;
        return state;
    }

    private void circleStart() {
        this.pot.circleStart();

        this.freshHand();

        if (this.operator().isLeave()) {
            this.action(Optype.Fold, 0, false);
        }
    }

    private void circleEnd() {
        this.pot.circleEnd(this.getCommunityCards());

        // 从庄家下一位开始
        Player op = this.nextOpPlayer(this.dealer().getId());
        ring = this.ring.move(v -> v == op);
    }

    /**
     * 摊牌
     */
    private void showdown() {

        this.isOver = true;

        this.pot.showdown(this.getCommunityCards(), this);

        this.freshHand();
    }

    public Settlement settle() {
        if (!this.isOver) {
            throw new IllegalStateException();
        }
        var open = this.showCardStrategy();
        return this.pot.settle(this.ring, bbPlayer(), open);
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
        if (!this.regulations.containsKey(Regulation.CoverCard) || !this.circle().equals(Circle.RIVER)) {
            this.ring.toList().stream()
                    .filter(v -> !this.pot.isAllin(v))
                    .forEach(p -> show.add(p));
            return show;
        }

        // 河牌圈
        var playerRing = this.ring.move(v -> v.equals(this.dealer()));
        var stub = this.pot.lastBetOrRaise();
        for (var divide : this.pot.divides()) {
            var pr = playerRing.move(player -> {
                if (!divide.contains(player.getId())) {
                    return false;
                }
                return stub != null
                        ? player.getId() == stub
                        : !this.pot.isFold(player);
            });
            var last = pr.value;
            show.add(last);
            for (var player : pr.toList()) {
                // 大于等于必须亮牌
                if (divide.contains(player.getId())
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
        if (this.waitActionNum() == 0) {
            return null;
        }

        var r = this.ring.move(v -> v.getId() == id).getNext();

        var standardId = this.pot.getStandardId();
        while ((this.pot.isFold(r.value) || this.pot.isAllin(r.value))
                && (standardId == null || r.value.getId() != standardId)) {
            r = r.getNext();
        }
        return r.value;
    }

    private void freshHand() {
        var board = this.getCommunityCards();
        for (var v : this.ring.toList()) {
            v.getHand().fresh(board);
        }
    }

    private void nextOp() {
        this.ring = this.ring.move(v -> !this.pot.isAllin(v) && !this.pot.isFold(v));
    }

    /**
     * 剩余玩家数量
     * 即 还没有离开牌局或弃牌的玩家数量
     */
    private int remainingNum() {
        var leaveOrFoldNum = (int) this.ring.toList()
                .stream()
                .filter(v -> v.isLeave() || this.pot.isFold(v.getId()))
                .count();
        return this.playerNum - leaveOrFoldNum;
    }

    /**
     * 等待押注的玩家数量
     * 即 没有fold 或 allin 的玩家，有可能在接下来进行押注的玩家
     */
    private int waitActionNum() {
        return this.playerNum - this.pot.allinAndFoldNum();
    }

    public Player operator() {
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
    public List<Card> getCommunityCards() {
        if (this.isOver && this.isCompareShowdown()) {
            return this.leftCard.subList(0, 5);
        }
        switch (this.pot.circle()) {
            case Circle.FLOP:
                return this.leftCard.subList(0, 3);
            case Circle.TURN:
                return this.leftCard.subList(0, 4);
            case Circle.RIVER:
                return this.leftCard.subList(0, 5);
            default:
                return Collections.emptyList();
        }
    }

    boolean isCompareShowdown() {
        return this.isOver && this.remainingNum() > 1;
    }

    public int sumPot() {
        return this.pot.sumPot();
    }

    public List<Integer> getPots() {
        return this.pot.divides().stream().map(Divide::getChips).collect(Collectors.toList());
    }

    public List<Divide> getDivides() {
        return new ArrayList<>(this.pot.divides());
    }

    public int smallBlind() {
        return this.regulations.getOrDefault(Regulation.SmallBlind, 0);
    }

    public int ante() {
        return this.regulations.getOrDefault(Regulation.Ante, 0);
    }

    public int anteSum() {
        return this.pot.anteSum();
    }

    public Player dealer() {
        var id = this.regulations.get(Regulation.Dealer);
        return this.getPlayer(v -> v.getId() == id);
    }

    public Player sbPlayer() {
        var id = this.regulations.get(Regulation.SB);
        return this.getPlayer(v -> v.getId() == id);
    }

    public Player bbPlayer() {
        var id = this.regulations.get(Regulation.BB);
        return this.getPlayer(v -> v.getId() == id);
    }

    Player getPlayer(Predicate<Player> filter) {
        var r = this.ring.move(filter);
        return r != null ? r.value : null;
    }


    public Player getPlayerById(Integer id) {
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
    public Map<Optype, Integer> authority() {
        var auth = this.pot.auth(this.operator());
        if (this.regulations.containsKey(Regulation.AllinOrFold)) {
            // TODO 押注权限检测在pot中，这个玩法会有漏洞
            var m = new HashMap<Optype, Integer>();
            m.put(Optype.Allin, auth.get(Optype.Allin));
            m.put(Optype.Fold, auth.get(Optype.Fold));
            auth = m;
        }
        return auth;
    }

    public Action getAction(int id) {
        return this.pot.getAction(id);
    }

    public Transfer state() {
        return STATE;
    }

    public static List<Player> winners(List<Player> players) {
        var winners = new ArrayList<Player>();
        winners.add(players.get(0));
        for (var i = 1; i < players.size(); i++) {
            var other = players.get(i);
            var com = other.getHand().compareTo(winners.get(0).getHand());
            if (com == 0) {
                winners.add(other);
            } else if (com > 0) {
                winners.clear();
                winners.add(other);
            }
        }
        return winners;
    }

    public List<Card> getLeftCard() {
        return new ArrayList<>(leftCard);
    }

    public List<Player> players() {
        return this.ring.toList();
    }

    public boolean isFold(int id) {
        return this.pot.isFold(id);
    }

    public int leaveOrderFoldNum() {
        return (int) this.ring.toList().stream()
                .filter(v -> !v.inGame() && this.pot.isFold(v.getId()))
                .count();
    }

    public boolean isAllinShowdown() {
        var notFold = this.pot.notFoldNum();
        return this.isOver && notFold > 1 && notFold - this.pot.allinNum() <= 1;
    }

}
