package com.texasthree.zone.room;

import com.texasthree.game.texas.Card;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author: neo
 * @create: 2022-08-09 16:50
 */
public class RoundEventHandler {

    private final Runnable onShowdown;

    private final Consumer<Object> broadcast;

    private final BiConsumer<String, Object> single;

    public RoundEventHandler(Runnable onShowdown,
                             Consumer<Object> broadcast,
                             BiConsumer<String, Object> single) {
        this.onShowdown = onShowdown;
        this.broadcast = broadcast;
        this.single = single;
    }

    public void trigger(TexasRound round, RoundEvent event) {
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
        var info = new Protocal.Start();
        info.ante = round.ante();
        info.sbSeatId = round.sbSeatId();
        info.bbSeatId = round.bbSeatId();
        info.dealer = round.dealer();
        info.smallBlind = round.smallBlind();
        info.sumPot = round.sumPot();
        info.players = round.getPlayers().stream()
                .map(v -> v.seatId)
                .collect(Collectors.toList());
        this.send(info);
    }

    private void onUpdateHand(TexasRound round) {
        for (var v : round.getPlayers()) {
            if (round.isLeave(v.getId())) {
                continue;
            }
            var hand = round.getPlayerHand(v.getId());
            var update = new Protocal.Hand();
            update.cards = toCardIds(hand.getHold());
            update.best = toCardIds(hand.getBest());
            update.key = toCardIds(hand.getKeys());
            update.type = hand.getType().name();
            send(v.getId(), update);
        }
    }

    private List<Integer> toCardIds(List<Card> cards) {
        return cards.stream().map(Card::getId).collect(Collectors.toList());
    }

    private void onAction(TexasRound round) {
        var action = round.getLastAction();
        var send = new Protocal.Action();
        send.op = action.op.name();
        send.seatId = action.id;
        send.chipsBet = action.chipsBet;
        send.chips = action.chipsLeft;
        send.sumPot = round.sumPot();
        this.send(send);
    }

    private void onCircleEnd(TexasRound round) {
        var info = new Protocal.CircleEnd();
        info.communityCards = toCardIds(round.getCommunityCards());
        info.pots = round.getPots();
        this.send(info);
    }

    private void onShowdown(TexasRound round) {
        var result = round.getResult();
        var info = new Protocal.Showdown();
        info.winners = new ArrayList<>(result.getWinners());
        info.hands = new ArrayList<>();
        for (var v : result.playersMap.values()) {
            var h = new Protocal.Hand();
//            h.cards = toCardIds(v.getCardList());
            h.cards = new ArrayList<>();
            h.cards.add(21);
            h.cards.add(121);
            var sh = new Protocal.ShowdownHand();
            sh.seatId = v.getId();
            sh.hand = h;
            info.hands.add(sh);
        }
        this.send(info);
        this.onShowdown.run();
    }

    private void onOperator(TexasRound round) {
        var info = new Protocal.Operator();
        info.leftSec = round.opLeftSec();
        info.seatId = round.getOperator().seatId;
        info.actions = round.authority()
                .entrySet().stream()
                .map(v -> {
                    var act = new Protocal.Action();
                    act.op = v.getKey().name();
                    act.chipsBet = v.getValue();
                    return act;
                }).collect(Collectors.toList());
        this.send(info);
    }

    private void send(Object msg) {
        this.broadcast.accept(msg);
    }
    private void send(String uid, Object msg) {
        this.single.accept(uid, msg);
    }
}