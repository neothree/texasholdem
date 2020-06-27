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

    private GameRoomSession session;


    private Desk desk;

    private MessageDispatcher dispatcher;

    private Cmd.RoomData data;

    public Room(Cmd.RoomData data, GameRoomSession session) {
        this.session = session;
        this.data = data;
        this.desk = new Desk();
        roomMap.put(data.id, this);
    }

    public void sitdown(User user, int position) {

    }

    public void situp(int position) {

    }

    public void start() {

    }

//    @Override
//    public void onLogin(final PlayerSession playerSession) {
//        SessionHandler listener = new SessionHandler(playerSession, dispatcher);
//        playerSession.addHandler(listener);
//        LOG.trace("Added event listener in Zombie Room");
//    }

    public static Room getRoom(String id) {
        return roomMap.get(id);
    }
}
