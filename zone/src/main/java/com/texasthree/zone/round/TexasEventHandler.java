package com.texasthree.zone.round;

import com.texasthree.game.texas.Card;
import com.texasthree.zone.controller.Cmd;
import com.texasthree.zone.room.Desk;

import java.util.ArrayList;
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
            case NEW_OPERATOR:
                this.onNewOperator(round);
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

    private void onStartGame(TexasRound round) {
        var info = new Cmd.StartGame();
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
            var update = new Cmd.HandUpdate();
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
        var id = "11";
        var action = round.getPlayerAction(id);
        var send = new Cmd.PlayerAction();
        send.op = action.op.name();
        send.chipsAdd = action.chipsAdd;
        send.chipsBet = action.chipsBet;
        send.chipsLeft = action.chipsLeft;
        send.sumPot = round.sumPot();
        this.desk.send(send);
    }

    private void onCircleEnd(TexasRound round) {
        var info = new Cmd.CircleEnd();
        info.board = new ArrayList<>();
        info.devide = new ArrayList<>();
        this.desk.send(info);
    }

    private void onShowdown() {
    }

    private void onNewOperator(TexasRound round) {
        var info = new Cmd.NewOperator();
        info.leftSec = round.opLeftSec();
        info.seatId = round.getOpPlayer().seatId;
        info.ops = round.authority()
                .entrySet().stream()
                .map(v -> {
                    var act = new Cmd.Action();
                    act.op = v.getKey().name();
                    act.chipsBet = v.getValue();
                    return act;
                }).collect(Collectors.toList());
        this.desk.send(info);
    }
}
