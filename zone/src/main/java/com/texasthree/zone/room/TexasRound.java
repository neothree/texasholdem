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
    final static int TIMEOUT_MOVE_ACTION = 500;
    final static int TIMEOUT_MOVE_CIRCLE = 2300;

    private final int id ;

    private final String logpre;

    private Texas game;

    /**
     * 计时器
     */
    private ScheduledEvent opEvent;
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
            players.add(new Player(v.seatId, v.user.getChips()));
        }

        this.game = Texas.builder()
                .smallBlind(1)
                .players(players)
                .regulation(Regulation.Dealer, dealer)
                .regulation(Regulation.SmallBlind, 1)
                .build();
        var move = this.game.start();
        this.printStart();

        this.opEvent = new ScheduledEvent(() -> this.move(move), 2000);
        this.eventHandler.trigger(this, RoundEvent.START_GAME);
        this.updateHand();
    }

    /**
     * 押注
     *
     * @param action 押注信息
     * @return void
     * create at 2020-06-30 11:12
     */
    public void action(Action action) {
        if (operator == null) {
            log.error("{}押注异常，没有操作人", logpre);
            return;
        }
        log.info("{}玩家押注 seatId={} op={}", logpre, operator.seatId, action.op);
        this.lastAction = action;
        this.eventHandler.trigger(this, RoundEvent.ACTION);

        this.opEvent = null;
        this.operator = null;

        var move = this.game.action(action.op, action.chipsAdd);
        if (Optype.Check.equals(action.op)) {
            // Check 没有动画，不需要延时，直接下一个操作
            this.move(move);
        } else if (Optype.Fold.equals(action.op)) {
            this.opEvent = new ScheduledEvent(() -> this.move(move), TIMEOUT_MOVE_FOLD);
        } else {
            this.opEvent = new ScheduledEvent(() -> this.move(move), TIMEOUT_MOVE_ACTION);
        }
    }


    private void move(String move) {
        if (Texas.STATE_NEXT_OP.equals(move)) {
            this.moveNextOp();
        } else if (Texas.STATE_CIRCLE_END.equals(move)) {
            this.moveCircleEnd();
        } else if (Texas.STATE_SHOWDOWN.equals(move)) {
            this.moveShowdown();
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
        this.opEvent = new ScheduledEvent(() -> this.onOpTimeout(), TIMEOUT_ACTION);
        this.eventHandler.trigger(this, RoundEvent.OPERATOR);
        log.info("{}轮到下一位进行押注 uid={} seatId={}", logpre, this.operator.user.getId(), this.operator.seatId);
    }

    /**
     * 一圈结束
     *
     * @return void
     * create at 2020-06-30 18:07
     */
    private void moveCircleEnd() {
        this.printCircle();
        this.eventHandler.trigger(this, RoundEvent.CIRCLE_END);

        this.updateHand();

        int time = this.game.circle().equals(Circle.FLOP) ? TIMEOUT_MOVE_CIRCLE : TIMEOUT_MOVE_CIRCLE - 1000;
        this.opEvent = new ScheduledEvent(() -> this.moveNextOp(), time);
    }

    /**
     * 摊牌
     *
     * @return void
     * create at 2020-06-30 18:06
     */
    private void moveShowdown() {
        this.printShowdown();
        this.opEvent = null;
        this.operator = null;
        this.isOver = true;
        this.eventHandler.trigger(this, RoundEvent.SHOWDOWN);
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
        var action = Action.of(op);
        action.id = this.operator.seatId;
        this.action(action);
    }

    public Hand getPlayerHand(String id) {
        var player = this.playerMap.get(id);
        return this.game.getPlayerById(player.seatId).getHand();
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
        this.eventHandler.trigger(this, RoundEvent.HAND);
    }


    public boolean isLeave(String id) {
        var player = playerMap.get(id);
        var p = this.game.getPlayerById(player.seatId);
        return p.isLeave();
    }

    public Action getPlayerAction(String id) {
        return null;
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

    public Result getResult() {
        return this.game.makeResult();
    }

    public Action getLastAction() {
        return this.lastAction;
    }


    public Map<Optype, Integer> authority() {
        return this.game.authority();
    }

    public int opLeftSec() {
        if (opEvent == null) {
            return 0;
        }
        return (int) ((opEvent.getNextMsec() - System.currentTimeMillis()) / 1000);
    }

    public String circle() {
        return game.circle();
    }

    private UserPlayer getPlayerBySeatId(int seatId) {
        return this.users.stream().filter(v -> v.seatId == seatId).findFirst().get();
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
        if (opEvent != null) {
            this.opEvent.check();
        }
    }

    public void force() {
        if (this.opEvent != null) {
            this.opEvent.force();
        }
    }

    public List<Card> getCommunityCards() {
        return game.getCommunityCards();
    }

    public List<Integer> getPots() {
        return game.getPots();
    }

    public int getId() {
        return id;
    }
}
