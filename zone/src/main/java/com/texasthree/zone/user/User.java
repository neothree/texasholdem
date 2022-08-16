package com.texasthree.zone.user;

import com.texasthree.zone.entity.Room;
import com.texasthree.zone.utility.StringUtils;

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

    private String id;

    private String name;

    private String username;

    private int chips;

    private Room room;

    private String token;


    public User(String username) {
        this.id = StringUtils.get32UUID();
        this.username = username;
        this.name = StringUtils.getChineseName();
        userMap.put(id, this);
        usernameMap.put(username, this);
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
        return this.name + ":" + this.id;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
