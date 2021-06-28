package com.texasthree.room.game;

import com.texasthree.room.Cmd;
import com.texasthree.room.ScheduledEvent;
import com.texasthree.room.User;
import com.texasthree.game.GameState;
import com.texasthree.game.texas.*;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author : neo
 * create at:  2020-06-29  15:26
 * @description: 德扑游戏
 */
@Slf4j
public class TexasGame {
    public static int TIMEOUT_ACTION = 15000;
    public static int TIMEOUT_MOVE_FOLD = 800;
    public static int TIMEOUT_MOVE_ACTION = 500;
    public static int TIMEOUT_MOVE_CIRCLE = 2000;
    /*
     * 庄家位
     */
    private int dealer = 0;

    private Texas texas;

    private GameState state;

    /**
     * 计时器
     */
    private ScheduledEvent opEvent;

    private PlayerInfo opPlayer;

    private boolean isOver;

    private Map<String, PlayerInfo> playerMap = new HashMap<>();

    private int actDuarion = 15000;

    private Consumer<Object> send;

    private User[] users;

    public TexasGame(User[] users, Consumer send) {
        this.users = users;
        this.send = send;
    }


    public void start() throws Exception {
        // 位置图谱
        var players = new ArrayList<User>();
        var pos = new HashMap<String, Integer>();
        for (int i = 0; i < this.users.length; i++) {
            var v = this.users[i];
            if (v != null) {
                players.add(v);
                pos.put(v.getId(), i);
            }
        }
        // 玩法
        Map<Regulation, Integer> laws = new HashMap<>();
        laws.put(Regulation.SmallBlind, 1);
        laws.put(Regulation.Dealer, 0);
        if (players.size() == 2) {
            laws.put(Regulation.SB, 0);
            laws.put(Regulation.BB, 1);
        } else {
            laws.put(Regulation.SB, 1);
            laws.put(Regulation.BB, 2);
        }

        texas = null;

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
//            state = texas.state();
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
            v.user.send(update);
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
        info.cards = cardList(player.getHand().getHold());
        info.key = cardList(player.getHand().getKeys());
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
        // TODO
        this.opPlayer = new PlayerInfo();

        this.opEvent = new ScheduledEvent(() -> this.onOpTimeout(), this.actDuarion);
        this.send(this.opInfo());
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

        this.send(this.boardInfo());

        this.updateHand();

        int time = this.texas.circle().equals(Circle.FLOP) ? 2300 : 1300;
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
        Optype op = state.ops.stream().anyMatch(v -> Optype.Check.equals(v.op)) ? Optype.Check : Optype.Fold;
        Action action = Action.of(op);
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
        info.devide = this.state.divides;
        return info;
    }

    private Cmd.RoundResult roundResult() {
        return null;
    }

    private void printResult(Cmd.RoundResult result) {

    }


    private void send(Object data) {
        if (this.send != null) {
            this.send.accept(data);
        }
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
