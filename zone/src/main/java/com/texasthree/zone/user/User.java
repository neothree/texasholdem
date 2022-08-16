package com.texasthree.zone.user;

import com.texasthree.zone.entity.Room;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class User {

    private static Map<String, User> userMap = new HashMap<>();

    private static Map<String, User> usernameMap = new HashMap<>();

    public static User getUserById(String id) {
        return userMap.get(id);
    }

    public static User getUserByUsername(String username) {
        return usernameMap.get(username);
    }


    private int chips;

    private Room room;

    private UserData data;

    public User(UserData data) {
        this.data = data;
    }

    /**
     * 进入房间
     */
    public void enter(Room room) {
        room.addUser(this);
        this.room = room;
    }

    /**
     * 离开房间
     */
    public void leave() {
        if (this.room == null) {
            return;
        }
        this.room.removeUser(this);
        this.room = null;
    }

    public Room getRoom() {
        return this.room;
    }


    @Override
    public String toString() {
        return data.toString();
    }


    public String getName() {
        return data.getName();
    }

    public int getChips() {
        return chips;
    }

    public void setChips(int chips) {
        this.chips = chips;
    }

    public void send(Object obj) {
        send(obj, this.getId());
    }

    public static void send(Object obj, String uid) {

    }

    public static void send(Object obj, Set<String> uids) {
    }

    public String getId() {
        return data.getId().toString();
    }
}
