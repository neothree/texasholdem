package com.texasthree.room;

import java.util.HashMap;
import java.util.Map;

public class User {

    private static Map<String, User> userMap = new HashMap<>();

    public static User getUser(String id) {
        return userMap.get(id);
    }

    private Cmd.UserData data;

    private Room room;

    public User(Cmd.UserData data) {
        this.data = data;
        userMap.put(data.id, this);
    }

    public void enter(Room room) {
        room.addUser(this);
        this.room = room;
    }

    public void leave(Room room) {
        room.removeUser(this);
        this.room = null;
    }

    public Room getRoom() {
        return this.room;
    }

    public String getId() {
        return this.data.id;
    }

    @Override
    public String toString() {
        return this.data.name + ":" + this.data.id;
    }
}
