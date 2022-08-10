package com.texasthree.zone.entity;

import com.texasthree.zone.net.Packet;
import com.texasthree.zone.utility.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class User {

    private static Map<String, User> userMap = new HashMap<>();

    public static User getUser(String id) {
        return userMap.get(id);
    }


    private String id;

    private String name;

    private int chips;

    private Room room;

    private String token;


    public User() {
        this.id = StringUtils.get32UUID();
        this.name = StringUtils.getChineseName();
        this.token = Packet.encode(this.id);
        userMap.put(id, this);
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
}
