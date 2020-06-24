package com.texasthree.room;

import com.texasthree.core.app.PlayerSession;
import com.texasthree.core.app.impl.GameRoomSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Room extends GameRoomSession {
    private static final Logger LOG = LoggerFactory.getLogger(Room.class);
    private Desk desk;

    public Room(GameRoomSessionBuilder builder) {
        super(builder);
    }

    @Override
    public void onLogin(final PlayerSession playerSession) {
        SessionHandler listener = new SessionHandler(playerSession);
        playerSession.addHandler(listener);
        LOG.trace("Added event listener in Zombie Room");
    }
}
