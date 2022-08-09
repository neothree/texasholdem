package com.texasthree.zone.round;

import com.texasthree.game.GameState;
import com.texasthree.game.texas.*;
import com.texasthree.zone.entity.ScheduledEvent;
import com.texasthree.zone.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 德扑游戏
 *
 * @author : neo
 * create at:  2020-06-29  15:26
 */
class TexasRound implements Round {

    private static Logger log = LoggerFactory.getLogger(TexasRound.class);

    final static int TIMEOUT_ACTION = 15000;
    final static int TIMEOUT_MOVE_FOLD = 800;
    final static int TIMEOUT_MOVE_ACTION = 500;
    final static int TIMEOUT_MOVE_CIRCLE = 2000;
    /*
     * 庄家位
     */
    private int dealer = 0;

    private Texas game;

    private GameState state;

    /**
     * 计时器
     */
    private ScheduledEvent opEvent;

    private RoundEventHandler eventHandler;

    private PlayerInfo opPlayer;

    private boolean isOver;

    private Map<String, PlayerInfo> playerMap = new HashMap<>();

    private int actDuration = 15000;

    private User[] users;


    TexasRound(User[] users) {
        this.users = users;
    }

    @Override
    public void start() {
        // 位置图谱
        var players = new ArrayList<Player>();
        for (int i = 0; i < this.users.length; i++) {
            var v = this.users[i];
            if (v != null) {
                players.add(new Player(i, v.getChips()));
            }
        }

        this.game = Texas.builder()
                .smallBlind(1)
                .players(players)
                .regulation(Regulation.Dealer, 0)
                .regulation(Regulation.SmallBlind, 1)
                .build();
        this.game.start();

        this.printStart();

        try {
            this.opEvent = new ScheduledEvent(() -> this.move(state.move), 2000);
        } catch (Exception e) {
            throw new IllegalStateException();
        }

        this.eventHandler.trigger(RoundEvent.START_GAME);


        this.dealCard();
    }

    /**
     * 押注
     *
     * @param action 押注信息
     * @return void
     * create at 2020-06-30 11:12
     */
    @Override
    public void action(Action action) {
        this.eventHandler.trigger(RoundEvent.ACTION);

        this.opEvent = null;
        this.opPlayer = null;

        try {
            this.game.action(action);
//            state = game.state();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (Optype.Check.equals(action.op)) {
            this.move(state.move);
        } else if (Optype.Fold.equals(action.op)) {
            this.opEvent = new ScheduledEvent(() -> this.move(state.move), TIMEOUT_MOVE_FOLD);
        } else {
            this.opEvent = new ScheduledEvent(() -> this.move(state.move), TIMEOUT_MOVE_ACTION);
        }
    }

    @Override
    public boolean finished() {
        return this.isOver;
    }

    /**
     * 发牌
     */
    private void dealCard() {
        this.eventHandler.trigger(RoundEvent.DEAL_CARD);
        this.updateHand();
    }

    /**
     * 更新手牌
     * create at 2020-06-30 10:26
     */
    private void updateHand() {
        this.eventHandler.trigger(RoundEvent.UPDATE_HAND);
    }

    private boolean isLeave(String id) {
        return this.state.players.stream().anyMatch(v -> v.isLeave() && playerMap.get(id).position == v.getId());
    }

    private List<Integer> cardList(List<Card> list) {
        return list.stream().map(v -> v.getId()).collect(Collectors.toList());
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
        this.opPlayer = new PlayerInfo();
        this.opEvent = new ScheduledEvent(() -> this.onOpTimeout(), this.actDuration);
        this.eventHandler.trigger(RoundEvent.NEW_OPERATOR);
        log.info("轮到下一位进行押注 opId={} position={}", this.opPlayer.user.getId(), this.opPlayer.position);
    }

    /**
     * 一圈结束
     *
     * @return void
     * create at 2020-06-30 18:07
     */
    private void moveCircleEnd() {
        this.printCircle();
        this.eventHandler.trigger(RoundEvent.CIRCLE_END);

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
        this.eventHandler.trigger(RoundEvent.SHOWDOWN);
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
        var op = state.ops.stream().anyMatch(v -> Optype.Check.equals(v.op)) ? Optype.Check : Optype.Fold;
        var action = Action.of(op);
        action.id = this.opPlayer.position;
        this.action(action);
    }

    private void printStart() {

    }

    private void printCircle() {

    }

    @Override
    public void loop() {
        if (opEvent != null) {
            this.opEvent.check();
        }
    }
}
