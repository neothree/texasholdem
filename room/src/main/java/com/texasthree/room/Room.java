package com.texasthree.room;

import com.texasthree.core.app.PlayerSession;
import com.texasthree.core.app.impl.GameRoomSession;
import com.texasthree.core.message.MessageDispatcher;
import io.netty.util.internal.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class Room {
    private static final Logger LOG = LoggerFactory.getLogger(Room.class);

    private static Map<String, Room> roomMap = new HashMap<>();

    private Cmd.RoomData data;

    private Desk desk;


    public Room(Cmd.RoomData data, GameRoomSession session) {
        this.data = data;
        this.desk = new Desk(session);
        roomMap.put(data.id, this);
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
}
