package com.texasthree.zone.entity;


import com.texasthree.zone.utility.StringUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


public class Room {

    private static Map<String, Room> roomMap = new HashMap<>();

    public static Room getRoom(String id) {
        return roomMap.get(id);
    }

    public static Collection<Room> all() {
        return roomMap.values();
    }

    private RoomConfig config;

    private Desk desk;

    private User creator;

    private boolean isStart;

    public Room(RoomConfig data, User user) {
        data.setId(StringUtils.get32UUID());
        this.config = data;
        this.desk = new Desk();
        this.creator = user;
        roomMap.put(data.getId(), this);
    }

    public void addUser(User user) {
        this.desk.addUser(user);
    }

    public void removeUser(User user) {
        this.desk.removeUser(user);
    }

    public void sitDown(User user, int position) {
        this.desk.sitDown(user, position);
    }

    public void sitUp(User user) {
        this.desk.sitUp(user);
    }

    public void dismiss() {
        roomMap.remove(this.config.getId());
    }

    public void enableRound() {
        this.isStart = true;
        this.desk.start();
    }

    public void start() {
        this.desk.start();
    }

    public String getName() {
        return this.config.getName();
    }

    public void loop() {
        this.desk.loop();
    }

    @Override
    public String toString() {
        return this.getName();
    }

}
