package com.texasthree.room;

import java.util.HashMap;
import java.util.Map;

public class User {

    private static Map<String, User> userMap = new HashMap<>();

    private Cmd.UserData data;

    private Room room;

    public User(Cmd.UserData data) {
        this.data = data;
        userMap.put(data.id, this);
    }

    public void enter(Room room) {

    }

    public Room getRoom() {
        return this.room;
    }

    public static User getUser(String id) {
        return userMap.get(id);
    }
}
