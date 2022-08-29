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
        public int number = 112;
        public int capacity;
        public int button;
        public int smallBlind;
        public int ante;
        public List<Player> seats;
        public RoundData round;
    }

    public static class RoundData {
        public int dealer;
        public int sbSeatId;
        public int bbSeatId;
        public int sumPot;
        public String circle;
        public List<Integer> pots;
        public List<Integer> communityCards;
        public List<Player> players;
    }

    public static class Player {
        public String uid;
        public String name;
        public String avator = "";
        public int seatId;
        public int chips;
    }

    public static class Seat {
        public int seatId;
        public Player player;
    }


    public static class Action {
        public String op;
        public int seatId;
        public Integer chipsBet;
        public Integer chips;
        public Integer sumPot;
    }

    public static class Operator {
        public int seatId;
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
        public int sbSeatId;
        public int bbSeatId;
        public int dealer;
        public int smallBlind;
        public int ante;
        public int sumPot;
        public List<Integer> players;
    }

    public static class CircleEnd {
        public List<Integer> communityCards;
        public List<Integer> pots;
    }
}
