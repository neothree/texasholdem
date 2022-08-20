package com.texasthree.zone.room;


import com.texasthree.zone.user.User;
import com.texasthree.zone.utility.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


public class Room {

    private static Logger log = LoggerFactory.getLogger(Room.class);

    private static Map<String, Room> roomMap = new HashMap<>();

    public static Room getRoom(String id) {
        return roomMap.get(id);
    }

    public static Collection<Room> all() {
        return roomMap.values();
    }

    public static Room one() {
        if (roomMap.isEmpty()) {
            var room = new Room();
            roomMap.put(room.id, room);
        }
        return roomMap.values().stream().findFirst().get();
    }

    private Desk desk;

    private String id;

    public Room() {
        this.id = StringUtils.get10UUID();
        this.desk = new Desk();
        roomMap.put(id, this);
        log.info("创建房间 {}", id);
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

    public void loop() {
        this.desk.loop();
    }

    @Override
    public String toString() {
        return this.id;
    }

    public String getId() {
        return id;
    }
}
