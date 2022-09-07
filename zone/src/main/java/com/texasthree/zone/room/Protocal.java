package com.texasthree.zone.room;

import com.texasthree.game.texas.Card;
import com.texasthree.game.texas.Optype;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: neo
 * @create: 2022-08-28 00:07
 */
public class Protocal {

    public static class RoomData {
        public String id;
        public String name;
        public Integer number = 112;
        public Integer capacity;
        public Integer button;
        public Integer smallBlind;
        public Integer ante;
        public List<Player> seats;
        public RoundData round;

        public RoomData(Room room, String uid) {
            this.id = room.getId();
            this.name = "test";
            this.ante = 1;
            this.smallBlind = 1;
            this.button = 1;
            this.capacity = room.getCapacity();
            this.seats = room.getSeats().stream()
                    .filter(com.texasthree.zone.room.Seat::occupied)
                    .map(Protocal.Player::new)
                    .collect(Collectors.toList());
            // 牌局
            var round = room.getRound();
            if (round != null) {
                this.round = new Protocal.RoundData(round, uid) ;
            }
        }
    }

    public static class RoundData {
        public Integer dealer;
        public Integer sbSeatId;
        public Integer bbSeatId;
        public Integer sumPot;
        public String circle;
        public List<Integer> pots;
        public List<Integer> communityCards;
        public List<Player> players;
        public Operator operator;

        public RoundData(TexasRound round, String uid) {
            this.dealer = round.dealer();
            this.sbSeatId = round.sbSeatId();
            this.bbSeatId = round.bbSeatId();
            this.sumPot = round.sumPot();
            this.circle = round.circle();
            this.pots = round.getPots();
            this.communityCards = toCardIds(round.getCommunityCards());
            this.players = new ArrayList<>();
            for (var v : round.getPlayers()) {
                var p = round.getPlayerById(v.seatId);
                var info = new Protocal.Player();
                info.seatId = v.seatId;
                info.chips = p.getChips();

                // 押注
                var action = round.getAction(v.seatId);
                if (action != null) {
                    info.betChips = action.chipsBet;
                    info.op = action.op;
                }

                // 主角手牌
                if (v.getId().equals(uid)) {
                    info.hand = new Protocal.Hand(p.getHand());
                }
                this.players.add(info);
            }
            if (round.getOperator() != null) {
                this.operator = new Protocal.Operator(round);
            }
        }
    }

    public static class Player {
        public String uid;
        public String name;
        public String avator = "";
        public Integer seatId;
        public Integer betChips;
        public Integer chips;
        public Optype op;
        public Hand hand;

        public Player() {
        }

        public Player(com.texasthree.zone.room.Seat seat) {
            var u = seat.getUser();
            this.uid = u.getId();
            this.name = u.getName();
            this.chips = u.getChips();
            this.seatId = seat.id;
        }
    }

    public static class Seat {
        public Integer seatId;
        public Player player;
    }

    public static class Action {
        public Optype op;
        public Integer seatId;
        public Integer chipsBet;
        public Integer chips;
        public Integer sumPot;

        Action() {
        }

        Action(Optype op, Integer chipsBet) {
            this.op = op;
            this.chipsBet = chipsBet;
        }
    }

    public static class Operator {
        public Integer seatId;
        public long leftSec;
        public List<Action> actions;

        Operator(TexasRound round) {
            this.leftSec = round.leftSec();
            this.seatId = round.getOperator().seatId;
            this.actions = round.authority()
                    .entrySet().stream()
                    .map(v -> new Protocal.Action(v.getKey(), v.getValue()))
                    .collect(Collectors.toList());
        }

    }

    public static class Hand {
        public List<Integer> cards;
        public String type;
        public List<Integer> best;
        public List<Integer> keys;

        public Hand() {

        }

        public Hand(com.texasthree.game.texas.Hand h) {
            this.cards = toCardIds(h.getHold());
            this.best = toCardIds(h.getBest());
            this.keys = toCardIds(h.getKeys());
            this.type = h.getType().name();
        }
    }

    public static class Start {
        public Integer sbSeatId;
        public Integer bbSeatId;
        public Integer dealer;
        public Integer smallBlind;
        public Integer ante;
        public Integer sumPot;
        public List<Integer> players;
    }

    public static class CircleEnd {
        public List<Integer> communityCards;
        public List<Integer> pots;
    }

    public static class Showdown {
        public List<Integer> winners;
        public List<ShowdownHand> hands;
    }

    public static class ShowdownHand {
        public Integer seatId;
        public Hand hand;
        public List<PotProfit> profits;
    }

    public static class PotProfit {
        public int potId;
        public int profit;

        PotProfit(int potId, int profit) {
            this.potId = potId;
            this.profit = profit;
        }
    }

    private static List<Integer> toCardIds(List<Card> cards) {
        return cards.stream().map(Card::getId).collect(Collectors.toList());
    }

}
