package com.texasthree.zone.room;

import com.texasthree.game.texas.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * 德扑游戏
 *
 * @author : neo
 * create at:  2020-06-29  15:26
 */
public class TexasRound {

    private static Logger log = LoggerFactory.getLogger(TexasRound.class);

    final static int TIMEOUT_ACTION = 5000;
    final static int TIMEOUT_MOVE_FOLD = 800;
    final static int TIMEOUT_MOVE_ACTION = 1000;
    final static int TIMEOUT_MOVE_CIRCLE = 3300;

    private final int id;

    private final String logpre;

    private Texas game;

    private ScheduledEventChecker scheduler = new ScheduledEventChecker();
    /**
     * 正在操作的玩家
     */
    private UserPlayer operator;
    /**
     * 是否结束
     */
    private boolean isOver;

    private Map<String, UserPlayer> playerMap = new HashMap<>();

    private List<UserPlayer> users;

    private RoundEventHandler eventHandler;

    private Action lastAction;

    private TexasInsurance insurance;

    public TexasRound(int id, String roomId, List<UserPlayer> users, RoundEventHandler eventHandler) {
        this.id = id;
        this.logpre = "[" + roomId + " - " + id + "]";
        this.users = users;
        this.eventHandler = eventHandler;
        for (var v : users) {
            playerMap.put(v.getId(), v);
        }
    }

    public void start(int dealer) {
        // 位置图谱
        var players = new ArrayList<Player>();
        for (var v : users) {
            players.add(new Player(v.seatId, v.getChips()));
        }

        this.game = Texas.builder()
                .smallBlind(1)
                .players(players)
                .regulation(Regulation.Dealer, dealer)
                .regulation(Regulation.SmallBlind, 1)
                .build();
        this.game.start();
        this.printStart();
        this.scheduler.once(() -> this.move(this.game.state()), 1100);
        this.eventHandler.on(this, RoundEvent.START_GAME);
        this.updateHand();
    }

    /**
     * 押注
     */

    public void action(Optype op, int chipsBet) {
        this.operator.execute();
        this.doAction(op, chipsBet);
    }

    private void doAction(Optype op, int chipsBet) {
        if (operator == null) {
            log.error("{}押注异常，没有操作人", logpre);
            return;
        }

        var bet = this.game.getCircleAction(operator.seatId);
        var old = bet != null ? bet.chipsBet : 0;
        var chipsAdd = Optype.Raise.equals(op) ? chipsBet - old : 0;
        log.info("{}玩家押注 seatId={} op={} chipsAdd={}", logpre, operator.seatId, op, chipsAdd);
        this.game.action(op, chipsAdd);
        this.lastAction = this.game.getAction(this.operator.seatId);
        this.operator = null;
        this.eventHandler.on(this, RoundEvent.ACTION);

        var move = this.game.state();
        if (Optype.Check.equals(op)) {
            // Check 没有动画，不需要延时，直接下一个操作
            this.move(move);
        } else if (Optype.Fold.equals(op)) {
            this.scheduler.once(() -> this.move(move), TIMEOUT_MOVE_FOLD);
        } else {
            this.scheduler.once(() -> this.move(move), TIMEOUT_MOVE_ACTION);
        }
    }


    private void move(Transfer move) {
        if (Transfer.NEXT_OP.equals(move)) {
            this.moveNextOp();
        } else if (Transfer.CIRCLE_END.equals(move)) {
            this.moveCircleEnd();
        } else if (Transfer.SHOWDOWN.equals(move)) {
            if (this.enableInsurance()) {
                this.moveInsurance();
            } else {
                this.moveShowdown();
            }
        }
    }

    /**
     * 下一个操作位
     *
     * @return void
     * create at 2020-06-30 18:07
     */
    private void moveNextOp() {
        this.operator = this.getPlayerBySeatId(game.operator().getId());
        this.operator.gain();
        this.scheduler.once(this::onOpTimeout, TIMEOUT_ACTION);
        this.eventHandler.on(this, RoundEvent.OPERATOR);
        log.info("{}轮到下一位进行押注 uid={} seatId={}", logpre, this.operator.getId(), this.operator.seatId);
    }

    /**
     * 一圈结束
     *
     * @return void
     * create at 2020-06-30 18:07
     */
    private void moveCircleEnd() {
        this.printCircle();
        this.eventHandler.on(this, RoundEvent.CIRCLE_END);

        this.updateHand();

        int time = this.game.circle().equals(Circle.FLOP) ? TIMEOUT_MOVE_CIRCLE : TIMEOUT_MOVE_CIRCLE - 1000;
        this.scheduler.once(this::moveNextOp, time);
    }

    /**
     * 摊牌
     *
     * @return void
     * create at 2020-06-30 18:06
     */
    private void moveShowdown() {
        this.printShowdown();
        this.scheduler.clear();
        this.operator = null;
        this.isOver = true;
        this.scheduler.once(() -> this.eventHandler.on(this, RoundEvent.SHOWDOWN), 2000);
    }

    public void buy(String uid, int potId, int amount) {
        var p = this.getPlayerByUid(uid);
        this.insurance.buy(p.seatId, potId, amount, null);
    }

    /**
     * 进入保险
     */
    private void moveInsurance() {
        this.operator = null;
        this.scheduler.clear();
        this.insurance = new TexasInsurance(this, eventHandler, this::moveShowdown);
        this.insurance.start();
    }

    /**
     * 操作超时
     *
     * @return void
     * create at 2020-06-30 17:30
     */
    private void onOpTimeout() {
        if (this.operator == null) {
            log.error("{}押注倒计时超时，但是检测到没有操作人", logpre);
            return;
        }

        log.info("{}压住超时: {}", logpre, this.operator.toString());
        var au = this.game.authority();
        var op = au.containsKey(Optype.Check) ? Optype.Check : Optype.Fold;
        this.doAction(op, 0);
    }

    public Hand getPlayerHand(int seatId) {
        return this.game.getPlayerById(seatId).getHand();
    }

    public boolean finished() {
        return this.isOver;
    }


    public Collection<UserPlayer> getPlayers() {
        return playerMap.values();
    }


    /**
     * 更新手牌
     */
    private void updateHand() {
        this.eventHandler.on(this, RoundEvent.HAND);
    }


    public boolean isLeave(String id) {
        var player = playerMap.get(id);
        var p = this.game.getPlayerById(player.seatId);
        return p.isLeave();
    }

    public int sumPot() {
        return this.game.sumPot();
    }

    public int ante() {
        return this.game.ante();
    }

    public int smallBlind() {
        return this.game.smallBlind();
    }

    public int sbSeatId() {
        return this.game.sbPlayer().getId();
    }

    public int bbSeatId() {
        return this.game.bbPlayer().getId();
    }

    public int dealer() {
        return this.game.dealer().getId();
    }

    public UserPlayer getOperator() {
        return this.operator;
    }

    public RoundSettlement settle() {
        var insClaims = insurance != null ? insurance.claims() : new HashMap<Integer, Integer>();
        return new RoundSettlement(this.game.settle(), insClaims);
    }

    public Action getLastAction() {
        return this.lastAction;
    }


    public Map<Optype, Integer> authority() {
        return this.game.authority();
    }

    public String circle() {
        return game.circle();
    }

    public UserPlayer getPlayerBySeatId(int seatId) {
        return this.users.stream().filter(v -> v.seatId == seatId).findFirst().get();
    }

    public UserPlayer getPlayerByUid(String id) {
        return this.users.stream().filter(v -> v.getId().equals(id)).findFirst().get();
    }

    private void printStart() {
        log.info("{} ============开始牌局==============", logpre);
        log.info("{} 开始牌局 playerNum={} dealer={} sbSeatId={} bbSeatId={} smallBlind={} ante={}", logpre, getPlayers().size(), dealer(), sbSeatId(), bbSeatId(), smallBlind(), ante());
    }

    private void printCircle() {
        log.info("{}============新一轮===================", logpre);
        log.info("{}开始新一轮押注 {}", logpre, circle());
    }

    private void printShowdown() {
        log.info("{}============牌局结束===================", logpre);
    }

    public void loop() {
        this.scheduler.check();
        if (this.insurance != null) {
            this.insurance.loop();
        }
    }

    public List<Card> getCommunityCards() {
        return game.getCommunityCards();
    }

    public List<Integer> getPots() {
        return game.getPots();
    }

    public List<Divide> getDivides() {
        return game.getDivides();
    }

    public int getId() {
        return id;
    }

    public int leftSec() {
        return this.scheduler.leftSec();
    }

    public void force() {
        this.scheduler.force();
    }

    public int getPlayerChips(int seatId) {
        return this.game.getPlayerById(seatId).getChips();
    }

    public boolean isPlayerInGame(int seatId) {
        return this.game.getPlayerById(seatId) != null;
    }

    public boolean isFold(int seatId) {
        return this.game.isFold(seatId);
    }

    public boolean isCompareShowdown() {
        return this.game.isCompareShowdown();
    }

    public Action getAction(int seatId) {
        return this.game.getAction(seatId);
    }

    public TexasInsurance getInsurance() {
        return insurance;
    }

    private boolean enableInsurance() {
        var gameNum = this.game.playerNum - this.game.leaveOrderFoldNum();
        return regulation(Regulation.Insurance)
                && !Circle.RIVER.equals(this.game.circle())
                && this.game.isAllinShowdown()
                && 1 < gameNum && gameNum <= 3;
    }

    private boolean regulation(Regulation regulation) {
        return true;
    }

    public List<Card> getLeftCard() {
        return this.game.getLeftCard();
    }

    public List<Player> players() {
        return this.game.players();
    }
}
