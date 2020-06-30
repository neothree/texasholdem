package com.texasthree.room;

import com.texasthree.core.app.impl.GameRoomSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Room {
    private static final Logger LOG = LoggerFactory.getLogger(Room.class);

    private static Map<String, Room> roomMap = new HashMap<>();

    private Cmd.RoomData data;

    private Desk desk;

    /**
     * 定时器
     */
    private ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();


    public Room(Cmd.RoomData data, GameRoomSession session) {
        this.data = data;
        this.desk = new Desk(session);
        roomMap.put(data.id, this);

        service.scheduleAtFixedRate(() -> loop(), 1000L, 200L, TimeUnit.MILLISECONDS);
    }

    public void addUser(User user) {
        this.desk.addUser(user);
    }

    public void removeUser(User user) {
        this.desk.removeUser(user);
    }

    public void sitdown(User user, int position) {
        this.desk.sitdown(user, position);
    }

    public void situp(int position) {
        this.desk.situp(position);

    }

    public void start() {
        this.desk.start();
    }

    public static Room getRoom(String id) {
        return roomMap.get(id);
    }


    public String getId() {
        return this.data.id;
    }

    public String getName() {
        return this.data.name;
    }

    @Override
    public String toString() {
        return this.getId() + ":" + this.getName();
    }

    private void loop() {
        this.desk.loop();
    }
}
