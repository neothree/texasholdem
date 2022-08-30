package com.texasthree.zone.room;

import java.util.List;

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
    }

    public static class Player {
        public String uid;
        public String name;
        public String avator = "";
        public Integer seatId;
        public Integer chips;
    }

    public static class Seat {
        public Integer seatId;
        public Player player;
    }

    public static class Action {
        public String op;
        public Integer seatId;
        public Integer chipsBet;
        public Integer chips;
        public Integer sumPot;
    }

    public static class Operator {
        public Integer seatId;
        public long leftSec;
        public List<Action> actions;
    }

    public static class Hand {
        public List<Integer> cards;
        public String type;
        public List<Integer> best;
        public List<Integer> key;
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
    }

}
