package com.texasthree.zone.round;

import com.texasthree.zone.controller.Cmd;

/**
 * @author: neo
 * @create: 2022-08-09 16:50
 */
public class RoundEventHandler {

    public void trigger(RoundEvent event) {
        switch (event) {
            case START_GAME:
                break;
            case DEAL_CARD:
                break;
            case UPDATE_HAND:
                break;
            case NEW_OPERATOR:
                break;
            case CIRCLE_END:
                break;
            case SHOWDOWN:
                break;
            default:
                throw new IllegalArgumentException(event.name());
        }
    }

    private void onStartGame() {
        this.send(new Cmd.StartGame());
    }

    private void onDealCard() {
        var info = new Cmd.DealCard();
//        info.positions = new ArrayList<>();
//        for (var v : this.playerMap.values()) {
//            info.positions.add(v.position);
//        }
        this.send(info);
    }

    private void onUpdateHand() {
//        for (var v : this.playerMap.values()) {
//            if (!this.isLeave(v.user.getId())) {
//                continue;
//            }
//            var update = new Cmd.HandUpdate();
//            update.hands = new ArrayList<>();
//            update.hands.add(this.getHand(v.user.getId()));
//            v.user.send(update);
//        }
    }

    private void onAction() {
        var send = new Cmd.PlayerAction();
//        send.sumPot = 0;
//        send.action = this.getAction(action.id + "");
        this.send(send);
    }

    private void onCircleEnd() {
        var info = new Cmd.CircleEnd();
//        info.board = this.state.board;
//        info.devide = this.state.divides;
    }

    private void onShowdown() {
    }

    private Cmd.NewOperator onNewOperator() {
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
        return info;
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



    private void send(Object object) {

    }
}
