package com.texasthree.zone.controller;

import java.util.List;

public class Cmd {

    public static class Command {
        public String name;
        public String data;
    }

    public static class UserData {
        public String id;
        public String name;
        public int chips;
    }

    public static class RoomData {
        public String id;
        public String name;
        public String creator;
    }

    public static class CreateRoom {
        public String name;
    }

    public static class EnterRoom {
        public String roomId;
    }

    public static class LeaveRoom {

    }

    public static class SitDown {
        public int seatId;
    }

    public static class SitUp {
        public int seatId;
    }

    public static class EnableRound {

    }

    public static class StartGame {
        public int sbSeatId;
        public int bbSeatId;
        public int dealer;
        public int smallBlind;
        public int ante;
        public int sumPot;
        public List<Integer> players;
    }

    public static class NewOperator {
        public int seatId;
        public int raiseLine;
        public long leftSec;
        public List<Action> ops;
    }

    public static class Action {
        public int seatId;
        public String op;
        public int chipsAdd;
        public int chipsBet;
        public int chipsLeft;

    }

    public static class PlayerAction {
        public int seatId;
        public String op;
        public int chipsAdd;
        public int chipsBet;
        public int chipsLeft;
        public int sumPot;
    }

    public static class HandUpdate {
        public int seatId;
        public List<Integer> cards;
        public String type;
        public List<Integer> best;
        public List<Integer> key;
    }

    public static class Hand {
        public int seatId;
        public List<Integer> cards;
        public String type;
        public List<Integer> best;
        public List<Integer> key;
    }

    public static class CircleEnd {
        public List<Integer> board;
        public List<Integer> devide;
    }

    public static class RoundResult {

    }

    public static class Heartbeat {
        public long timestamp;
    }

    public static class Dismiss {
    }

    public static class Warning {
        public String message;

        public Warning(String message) {
            this.message = message;
        }
    }
}