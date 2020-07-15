package com.texasthree.room.game;

import com.texasthree.proto.Cmd;
import com.texasthree.room.Desk;
import com.texasthree.room.ScheduledEvent;
import com.texasthree.room.User;
import com.texasthree.round.RoundState;
import com.texasthree.round.texas.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author : neo
 * create at:  2020-06-29  15:26
 * @description: 德扑游戏
 */
public class TexasGame {
    private static final Logger LOG = LoggerFactory.getLogger(TexasGame.class);

    /*
     * 庄家位
     */
    private int dealer = 0;

    private Runnable onShowdown;

    private Desk desk;

    ///////////// 一局数据 ///////////////
    private Texas texas;

    private RoundState state;

    private ScheduledEvent opEvent;

    private PlayerInfo opPlayer;

    private boolean isOver;

    private Map<String, PlayerInfo> playerMap = new HashMap<>();

    private int actDuarion = 15000;

    public TexasGame(Desk desk, Runnable onShowdown) {
        this.desk = desk;
        this.onShowdown = onShowdown;
    }


    public void start() {
        // 位置图谱
        List<User> users = new ArrayList<>();
        Map<String, Integer> pos = new HashMap<>();
        for (int i = 0; i < desk.getUsers().length; i++) {
            User v = desk.getUsers()[i];
            if (v != null) {
                users.add(v);
                pos.put(v.getId(), i);
            }
        }
        texas = new TexasBuilder()
                .users(users)
                .position(pos)
                .build();
        try {
            texas.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.state = texas.state();

        this.printStart();

        this.opEvent = new ScheduledEvent(() -> this.move(state.move), 2000);

        this.send(new Cmd.StartGame());

        this.dealCard();
    }

    /**
     * 押注
     *
     * @param action 押注信息
     * @return void
     * create at 2020-06-30 11:12
     */
    public void action(Action action) {

        Cmd.BetAction send = new Cmd.BetAction();
        send.sumPot = 0;
        send.action = this.getAction(action.id + "");
        this.send(send);

        this.opEvent = null;
        this.opPlayer = null;

        try {
            this.texas.action(action);
            state = texas.state();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (Optype.Check.equals(action.op)) {
            this.move(state.move);
        } else if (Optype.Fold.equals(action.op)) {
            this.opEvent = new ScheduledEvent(() -> this.move(state.move), Define.TIMEOUT_MOVE_FOLD);
        } else {
            this.opEvent = new ScheduledEvent(() -> this.move(state.move), Define.TIMEOUT_MOVE_ACTION);
        }

    }

    public void settle() {
//        this.texas.settle();
    }

    /**
     * 发牌
     */
    private void dealCard() {
        Cmd.DealCard info = new Cmd.DealCard();
        info.positions = new ArrayList<>();
        for (PlayerInfo v : this.playerMap.values()) {
            info.positions.add(v.position);
        }
        this.send(info);

        this.updateHand();
    }

    /**
     * 更新手牌
     * create at 2020-06-30 10:26
     */
    private void updateHand() {
        for (PlayerInfo v : this.playerMap.values()) {
            if (!this.isLeave(v.user.getId())) {
                continue;
            }
            Cmd.HandUpdate update = new Cmd.HandUpdate();
            update.hands = new ArrayList<>();
            update.hands.add(this.getHand(v.user.getId()));
            this.send(update, v.user);
        }
    }

    private boolean isLeave(String id) {
        return this.state.players.stream().anyMatch(v -> v.isLeave() && playerMap.get(id).position == v.getId());
    }

    private Cmd.Hand getHand(String id) {
        if (!this.playerMap.containsKey(id)) {
            return null;
        }
        int position = this.playerMap.get(id).position;
        Player player = this.state.players.stream().filter(v -> v.getId() == position).findFirst().orElse(null);
        Cmd.Hand info = new Cmd.Hand();
        info.best = cardList(player.getHand().getBest());
        info.cards = cardList(player.getHand().getList());
        info.key = cardList(player.getHand().getKey());
        info.type = player.getHand().getType().name();
        info.position = position;
        return info;
    }

    private List<Integer> cardList(List<Card> list) {
        return list.stream().map(v -> v.getId()).collect(Collectors.toList());
    }

    private Cmd.Action getAction(String id) {
        int position = this.playerMap.get(id).position;
        Action act = this.state.actions.stream().filter(v -> v.id == position).findFirst().orElse(null);
        Cmd.Action action = new Cmd.Action();
        action.op = act.op.name();
        action.chipsBet = act.chipsBet;
        return action;
    }

    private void move(Move move) {
        if (Move.NextOp.equals(move)) {
            this.moveNextOp();
        } else if (Move.CircleEnd.equals(move)) {
            this.moveCircleEnd();
        } else if (Move.Showdown.equals(move)) {
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
        // TODO
        this.opPlayer = new PlayerInfo();

        this.opEvent = new ScheduledEvent(() -> this.onOpTimeout(), this.actDuarion);
        this.send(this.opInfo());
        LOG.info("轮到下一位进行押注 opId={} position={}", this.opPlayer.user.getId(), this.opPlayer.position);
    }

    /**
     * 一圈结束
     *
     * @return void
     * create at 2020-06-30 18:07
     */
    private void moveCircleEnd() {
        this.printCircle();

        this.send(this.boardInfo());

        this.updateHand();

        int time = this.texas.circle().equals(Circle.Flop) ? 2300 : 1300;
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

        Cmd.RoundResult result = this.roundResult();
        this.send(result);

        printResult(result);

        this.onShowdown.run();
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

        LOG.info("压住超时: {}", this.opPlayer.toString());
        Optype op = state.ops.stream().anyMatch(v -> Optype.Check.equals(v.op)) ? Optype.Check : Optype.Fold;
        Action action = new Action(op);
        action.id = this.opPlayer.position;
        this.action(action);
    }

    private Cmd.NewOperator opInfo() {
        Cmd.NewOperator info = new Cmd.NewOperator();
        info.leftSec = this.opEvent.getNextMsec() - System.currentTimeMillis();
        info.ops = state.ops.stream().map(v -> {
            Cmd.Action act = new Cmd.Action();
            act.op = v.op.name();
            act.chipsBet = v.chipsBet;
            return act;
        }).collect(Collectors.toList());
        info.position = this.opPlayer.position;
        info.raiseLine = state.raiseLine;
        return info;
    }

    private Cmd.CircleEnd boardInfo() {
        Cmd.CircleEnd info = new Cmd.CircleEnd();
        info.board = this.state.board;
        info.devide = this.state.devide;
        return info;
    }

    private Cmd.RoundResult roundResult() {
        return null;
    }

    private void printResult(Cmd.RoundResult result) {

    }


    private void send(Object data) {
        this.desk.send(data);
    }

    private void send(Object data, User user) {
        this.desk.send(data, user);
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
