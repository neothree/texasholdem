package com.texasthree.zone.entity;


import com.texasthree.zone.controller.Cmd;

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

    private Cmd.RoomData data;

    private Desk desk;

    /**
     * 定时器
     */
    private ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();


    public Room(Cmd.RoomData data) {
        this.data = data;
        this.desk = new Desk();
        roomMap.put(data.id, this);

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

    public void dismiss(User user) {

    }

    public void enableRound(User user) {

    }

    public void start() {
        this.desk.start();
    }

    public String getName() {
        return this.data.name;
    }

    private void loop() {
        this.desk.loop();
    }

    @Override
    public String toString() {
        return this.getName();
    }

}
