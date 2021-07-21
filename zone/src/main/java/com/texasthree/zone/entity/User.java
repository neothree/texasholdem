package com.texasthree.zone.entity;

import com.texasthree.core.Server;
import com.texasthree.zone.controller.Cmd;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class User {

    public static Server server;

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

    public String getId() {
        return this.data.id;
    }

    public int getChips() {
        return this.data.chips;
    }

    @Override
    public String toString() {
        return this.data.name + ":" + this.data.id;
    }

    public void send(Object obj) {
        send(obj, this.getId());
    }

    public static void send(Object obj, String uid) {
        server.send(uid, obj);

    }

    public static void send(Object obj, Set<String> uids) {
        server.send(uids, obj);
    }
}
