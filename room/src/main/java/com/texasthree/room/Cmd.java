package com.texasthree.room;

public class Cmd {
    public static class UserData {
        String id;
    }

    public static class RoomData {
        String id;
        String creator;
    }

    public static class Command {
        String name;
        String data;
    }

    public static class CreateRoom {
        public RoomData data;

    }

    public static class EnterRoom {
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
}
