package com.texasthree.room;

import com.texasthree.core.app.PlayerSession;
import com.texasthree.core.app.impl.GameRoomSession;
import com.texasthree.core.message.MessageDispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Room extends GameRoomSession {
    private static final Logger LOG = LoggerFactory.getLogger(Room.class);

    private static Map<String, Room> roomMap = new HashMap<>();

    private Cmd.RoomData data;

    private Desk desk;

    private MessageDispatcher dispatcher;
    /**
     * 定时器
     */
    private ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();

    public Room(GameRoomSessionBuilder gameRoomSessionBuilder) {
        super(gameRoomSessionBuilder);
    }

    public Room(GameRoomSessionBuilder gameRoomSessionBuilder, Cmd.RoomData data, MessageDispatcher dispatcher) {
        super(gameRoomSessionBuilder);
        this.dispatcher = dispatcher;
        this.data = data;
        this.desk = new Desk(this);
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

    @Override
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

    @Override
    public void onLogin(PlayerSession playerSession) {
        SessionHandler listener = new SessionHandler(playerSession, dispatcher);
        playerSession.addHandler(listener);
        LOG.trace("Added event listener in Zombie Room");
    }

}
