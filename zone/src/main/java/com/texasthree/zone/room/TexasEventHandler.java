package com.texasthree.zone.room;

import com.texasthree.game.texas.Card;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: neo
 * @create: 2022-08-09 16:50
 */
public class TexasEventHandler {

    private Desk desk;

    public TexasEventHandler(Desk desk) {
        this.desk = desk;
    }

    public void trigger(TexasRound round, RoundEvent event) {
        switch (event) {
            case START_GAME:
                this.onStartGame(round);
                break;
            case ACTION:
                this.onAction(round);
                break;
            case UPDATE_HAND:
                this.onUpdateHand(round);
                break;
            case OPERATOR:
                this.onOperator(round);
                break;
            case CIRCLE_END:
                this.onCircleEnd(round);
                break;
            case SHOWDOWN:
                this.onShowdown();
                break;
            default:
                throw new IllegalArgumentException(event.name());
        }
    }

    public void onSeat(int seatId) {
        var info = new Protocal.Seat();
        info.seatId = seatId;
        var user = desk.getSeats()[seatId];
        if (user != null) {
            var p = new Protocal.Player();
            p.uid = user.getId();
            p.name = user.getName();
            p.chips = user.getChips();
            info.player = p;
        }
        this.desk.send(info);
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
        this.desk.send(info);
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
            desk.send(v.getId(), update);
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
        this.desk.send(send);
    }

    private void onCircleEnd(TexasRound round) {
        var info = new Protocal.CircleEnd();
        info.communityCards = toCardIds(round.getCommunityCards());
        info.pots = round.getPots();
        this.desk.send(info);
    }

    private void onShowdown() {
        desk.onShowdown();
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
        this.desk.send(info);
    }
}
