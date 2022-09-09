package com.texasthree.zone.room;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * @author: neo
 * @create: 2022-08-09 16:50
 */
class RoundEventHandler {

    private final Runnable onShowdown;

    /**
     * 广播
     */
    private final Consumer<Object> broadcast;
    /**
     * 单播
     */
    private final BiConsumer<String, Object> single;

    RoundEventHandler(Runnable onShowdown,
                      Consumer<Object> broadcast,
                      BiConsumer<String, Object> single) {
        this.onShowdown = onShowdown;
        this.broadcast = broadcast;
        this.single = single;
    }

    void on(TexasRound round, RoundEvent event) {
        switch (event) {
            case START_GAME:
                this.onStartGame(round);
                break;
            case ACTION:
                this.onAction(round);
                break;
            case HAND:
                this.onUpdateHand(round);
                break;
            case OPERATOR:
                this.onOperator(round);
                break;
            case CIRCLE_END:
                this.onCircleEnd(round);
                break;
            case SHOWDOWN:
                this.onShowdown(round);
                break;
            default:
                throw new IllegalArgumentException(event.name());
        }
    }

    private void onStartGame(TexasRound round) {
        var info = new Protocal.Start(round);
        this.send(info);
    }

    private void onUpdateHand(TexasRound round) {
        for (var v : round.getPlayers()) {
            if (round.isLeave(v.getId())) {
                continue;
            }
            var hand = round.getPlayerHand(v.seatId);
            send(v.getId(), new Protocal.Hand(hand));
        }
    }

    private void onAction(TexasRound round) {
        var action = round.getLastAction();
        var info = new Protocal.Action(action, round.sumPot());
        this.send(info);
    }

    private void onCircleEnd(TexasRound round) {
        var info = new Protocal.CircleEnd(round);
        this.send(info);
    }

    private void onShowdown(TexasRound round) {
        var info = new Protocal.Showdown(round);
        this.send(info);
        this.onShowdown.run();
    }

    private void onOperator(TexasRound round) {
        var info = new Protocal.Operator(round);
        this.send(info);
    }

    private void send(Object msg) {
        this.broadcast.accept(msg);
    }

    private void send(String uid, Object msg) {
        this.single.accept(uid, msg);
    }
}
