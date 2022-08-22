package com.texasthree.zone.round;

import com.texasthree.game.texas.*;
import com.texasthree.zone.room.ScheduledEvent;
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

    final static int TIMEOUT_ACTION = 15000;
    final static int TIMEOUT_MOVE_FOLD = 800;
    final static int TIMEOUT_MOVE_ACTION = 500;
    final static int TIMEOUT_MOVE_CIRCLE = 2000;


    private Texas game;

    /**
     * 计时器
     */
    private ScheduledEvent opEvent;
    /**
     * 正在操作的玩家
     */
    private UserPlayer opPlayer;
    /**
     * 是否结束
     */
    private boolean isOver;

    private Map<String, UserPlayer> playerMap = new HashMap<>();

    private List<UserPlayer> users;

    private int actDuration = 15000;

    private TexasEventHandler eventHandler;

    public TexasRound(List<UserPlayer> users, TexasEventHandler eventHandler) {
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
        this.eventHandler.trigger(this, RoundEvent.ACTION);

        this.opEvent = null;
        this.opPlayer = null;

        var move = this.game.action(action);
        if (Optype.Check.equals(action.op)) {
            this.move(move);
        } else if (Optype.Fold.equals(action.op)) {
            this.opEvent = new ScheduledEvent(() -> this.move(move), TIMEOUT_MOVE_FOLD);
        } else {
            this.opEvent = new ScheduledEvent(() -> this.move(move), TIMEOUT_MOVE_ACTION);
        }
    }

    public boolean finished() {
        return this.isOver;
    }


    public Collection<UserPlayer> getPlayers() {
        return playerMap.values();
    }

    public UserPlayer getOpPlayer() {

        return this.opPlayer;
    }


    /**
     * 更新手牌
     */
    private void updateHand() {
        this.eventHandler.trigger(this, RoundEvent.UPDATE_HAND);
    }


    public boolean isLeave(String id) {
        var player = playerMap.get(id);
        var p = this.game.getPlayerById(player.seatId);
        return p.isLeave();
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
        this.opPlayer = new UserPlayer(1, null);
        this.opEvent = new ScheduledEvent(() -> this.onOpTimeout(), this.actDuration);
        this.eventHandler.trigger(this, RoundEvent.NEW_OPERATOR);
        log.info("轮到下一位进行押注 opId={} seatId={}", this.opPlayer.user.getId(), this.opPlayer.seatId);
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

        int time = this.game.circle().equals(Circle.FLOP) ? 2300 : 1300;
        this.opEvent = new ScheduledEvent(() -> this.moveNextOp(), time);
    }

    /**
     * 摊牌
     *
     * @return void
     * create at 2020-06-30 18:06
     */
    private void moveShowdown() {
        this.opEvent = null;
        this.opPlayer = null;
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
        if (this.opPlayer == null) {
            return;
        }

        log.info("压住超时: {}", this.opPlayer.toString());
        var au = this.game.authority();
        var op = au.containsKey(Optype.Check) ? Optype.Check : Optype.Fold;
        var action = Action.of(op);
        action.id = this.opPlayer.seatId;
        this.action(action);
    }

    public Hand getPlayerHand(String id) {
        var player = this.playerMap.get(id);
        return this.game.getPlayerById(player.seatId).getHand();
    }


    public UserPlayer opPlayer() {
        return null;
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

    public Map<Optype, Integer> authority() {
        return this.game.authority();
    }

    public int opLeftSec() {
        return 15;
    }

    private void printStart() {

    }

    private void printCircle() {

    }

    public void loop() {
        if (opEvent != null) {
            this.opEvent.check();
        }
    }
}
