package com.texasthree.room;

import java.util.List;

public class Cmd {
    public static class UserData {
        String id;
        String name;
        int chips;
    }

    public static class RoomData {
        public String id;
        public String name;
        public String creator;
    }

    public static class Command {
        public String name;
        public String data;
    }

    public static class CreateRoom {
        public RoomData data;

    }

    public static class EnterRoom {
        public String id;

    }

    public static class LeaveRoom {
        public String id;

    }

    public static class Sitdown {
        public int position;
    }

    public static class Situp {
        public int position;
    }

    public static class StartGame {

    }

    public static class NewOperator {
        public int position;
        public int raiseLine;
        public long leftSec;
        public List<Action> ops;
    }

    public static class DealCard {
        public List<Integer> positions;
    }

    public static class Action {
        public int position;
        public String op;
        public int chipsAdd;
        public int chipsBet;
        public int chipsLeft;

    }

    public static class BetAction {
        public Action action;
        public int sumPot;
    }

    public static class HandUpdate {
        public List<Hand> hands;
    }

    public static class Hand {
        public int position;
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
}
