package com.texasthree.zone.round;

import com.texasthree.game.texas.Action;
import com.texasthree.zone.room.Protocal;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * @author: neo
 * @create: 2022-08-09 16:50
 */
public class TexasEventHandler implements TexasEventListener {

    private final Runnable onShowdown;
    /**
     * 广播
     */
    private final Consumer<Object> broadcast;
    /**
     * 单播
     */
    private final BiConsumer<String, Object> single;

    public TexasEventHandler(Runnable onShowdown,
                      Consumer<Object> broadcast,
                      BiConsumer<String, Object> single) {
        this.onShowdown = onShowdown;
        this.broadcast = broadcast;
        this.single = single;
    }

    @Override
    public void onStartGame(TexasEvent event) {
        var info = new Protocal.Start(event.getRound());
        this.send(info);
    }

    @Override
    public void onUpdateHand(TexasEvent event) {
        var round = event.getRound();
        for (var v : round.getPlayers()) {
            if (round.isLeave(v.getId())) {
                continue;
            }
            var hand = round.getPlayerHand(v.seatId);
            send(v.getId(), new Protocal.Hand(hand));
        }
    }

    @Override
    public void onOperator(TexasEvent event) {
        var round = event.getRound();
        var info = new Protocal.Operator(round);
        this.send(info);
    }

    @Override
    public void onAction(TexasEvent event) {
        var round = event.getRound();
        var action = (Action) event.getValue();
        var info = new Protocal.Action(action, round.sumPot());
        this.send(info);
    }

    @Override
    public void onCircleEnd(TexasEvent event) {
        var round = event.getRound();
        var info = new Protocal.CircleEnd(round);
        this.send(info);
    }

    @Override
    public void onShowdown(TexasEvent event) {
        var round = event.getRound();
        var info = new Protocal.Showdown(round);
        this.send(info);
        this.onShowdown.run();
    }

    @Override
    public void onInsurance(TexasEvent event) {
        var round = event.getRound();
        var info = new Protocal.Insurance(round.getInsurance());
        this.send(info);
    }

    @Override
    public void onBuyer(TexasEvent event) {
        var round = event.getRound();
        var info = new Protocal.Buyer(round.getInsurance());
        this.send(info);
    }

    @Override
    public void onBuyEnd(TexasEvent event) {
        var round = event.getRound();
        var info = new Protocal.BuyEnd(round.getInsurance(), 0, 0);
        this.send(info);
    }

    private void send(Object msg) {
        this.broadcast.accept(msg);
    }

    private void send(String uid, Object msg) {
        this.single.accept(uid, msg);
    }
}
