package com.texasthree.zone.round;

import com.texasthree.game.texas.Card;
import com.texasthree.zone.controller.Cmd;
import com.texasthree.zone.entity.Desk;

import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * @author: neo
 * @create: 2022-08-09 16:50
 */
public class TexasEventHandler {

    private Desk desk;

    private TexasRound round;

    public void trigger(RoundEvent event) {
        switch (event) {
            case START_GAME:
                this.onStartGame();
                break;
            case DEAL_CARD:
                this.onDealCard();
                break;
            case ACTION:
                this.onAction();
                break;
            case UPDATE_HAND:
                this.onUpdateHand();
                break;
            case NEW_OPERATOR:
                this.onNewOperator();
                break;
            case CIRCLE_END:
                this.onCircleEnd();
                break;
            case SHOWDOWN:
                this.onShowdown();
                break;
            default:
                throw new IllegalArgumentException(event.name());
        }
    }

    private void onStartGame() {
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

    private void onDealCard() {
        var info = new Cmd.DealCard();
        info.seatIds = round.getPlayers().stream().map(v -> v.seatId).collect(Collectors.toList());
        this.desk.send(info);
    }

    private void onUpdateHand() {
        for (var v : round.getPlayers()) {
            if (!round.isLeave(v.getId())) {
                continue;
            }
            var hand = round.getPlayerHand(v.getId());
            var update = new Cmd.HandUpdate();
            update.cards = hand.getHold().stream().map(Card::getId).collect(Collectors.toList());
            update.best = hand.getBest().stream().map(Card::getId).collect(Collectors.toList());
            update.key = hand.getKeys().stream().map(Card::getId).collect(Collectors.toList());
            update.type = hand.getType().name();
            round.send(v.getId(), update);
        }
    }

    private void onAction() {
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

    private void onCircleEnd() {
        var info = new Cmd.CircleEnd();
        info.board = new ArrayList<>();
        info.devide = new ArrayList<>();
        this.desk.send(info);
    }

    private void onShowdown() {
    }

    private void onNewOperator() {
        var info = new Cmd.NewOperator();
        info.leftSec = this.round.opLeftSec();
        info.seatId = this.round.getOpPlayer().seatId;
        info.ops = this.round.authority()
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
