package com.texasthree.zone.round;

import com.texasthree.game.texas.Player;
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

    private Round round;

    public void trigger(RoundEvent event) {
        switch (event) {
            case START_GAME:
                this.onStartGame();
                break;
            case DEAL_CARD:
                this.onDealCard();
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
        this.desk.send(new Cmd.StartGame());
    }

    private void onDealCard() {
        var info = new Cmd.DealCard();
        var round = this.desk.getRound();
        this.desk.getRound().finished();
        info.positions = round.getPlayers().stream().map(Player::getId).collect(Collectors.toList());
        this.desk.send(info);
    }

    private void onUpdateHand() {
        var round = this.desk.getRound();
        for (var v : round.getPlayers()) {
            if (!round.isLeave(v.getId())) {
                continue;
            }
            var update = new Cmd.HandUpdate();
            update.hands = new ArrayList<>();
            update.hands.add(this.getHand(v.getId() + ""));
            round.send(v.getId(), update);
        }
    }

    private void onAction() {
        var round = this.desk.getRound();
        var send = new Cmd.PlayerAction();
        send.sumPot = 0;
        send.action = this.getAction(round.opPlayer().getId() + "");
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
//        info.leftSec = this.opEvent.getNextMsec() - System.currentTimeMillis();
//        info.ops = state.ops.stream().map(v -> {
//            var act = new Cmd.Action();
//            act.op = v.op.name();
//            act.chipsBet = v.chipsBet;
//            return act;
//        }).collect(Collectors.toList());
//        info.position = this.opPlayer.position;
//        info.raiseLine = state.raiseLine;
        this.desk.send(info);
    }

    private Cmd.Action getAction(String id) {
//        int position = this.playerMap.get(id).position;
//        var act = this.state.actions.stream().filter(v -> v.id == position).findFirst().orElse(null);
//        var action = new Cmd.Action();
//        action.op = act.op.name();
//        action.chipsBet = act.chipsBet;
//        return action;
        return null;
    }

    private Cmd.Hand getHand(String id) {
//        if (!this.playerMap.containsKey(id)) {
//            return null;
//        }
//        int position = this.playerMap.get(id).position;
//        var player = this.state.players.stream().filter(v -> v.getId() == position).findFirst().orElse(null);
//        var info = new Cmd.Hand();
//        info.best = cardList(player.getHand().getBest());
//        info.cards = cardList(player.getHand().getHold());
//        info.key = cardList(player.getHand().getKeys());
//        info.type = player.getHand().getType().name();
//        info.position = position;
//        return info;
        return null;
    }
}
