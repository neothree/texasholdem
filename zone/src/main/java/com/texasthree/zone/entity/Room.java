package com.texasthree.zone.entity;


import com.texasthree.zone.utility.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class Room {

    private static Map<String, Room> roomMap = new HashMap<>();

    public static Room getRoom(String id) {
        return roomMap.get(id);
    }

    private RoomConfig config;

    private Desk desk;

    private User creator;

    private boolean isStart;

    /**
     * 定时器
     */
    private ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();


    public Room(RoomConfig data, User user) {
        data.setId(StringUtils.get32UUID());
        this.config = data;
        this.desk = new Desk();
        this.creator = user;
        roomMap.put(data.getId(), this);

        service.scheduleAtFixedRate(() -> loop(), 1000L, 200L, TimeUnit.MILLISECONDS);
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

    private void loop() {
        this.desk.loop();
    }

    @Override
    public String toString() {
        return this.getName();
    }

}
