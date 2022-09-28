package com.texasthree.appzone;

import com.texasthree.user.UserData;
import com.texasthree.appzone.room.Room;

import java.util.HashMap;
import java.util.Map;

public class User {

    private static Map<String, User> userMap = new HashMap<>();

    public static User getUserById(String id) {
        return userMap.get(id);
    }

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

    public String getId() {
        return data.getId();
    }

    public String getAccountId() {
        return data.getAccountId();
    }

    public boolean isReal() {
        return this.data.isReal();
    }

    public String getAvatar() {
        return this.data.getAvatar();
    }

    public String getClubId() {
        return this.data.getClubId();
    }
}
