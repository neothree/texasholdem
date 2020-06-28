package com.texasthree.room;

import com.texasthree.core.app.PlayerSession;
import com.texasthree.core.app.impl.GameRoomSession;
import com.texasthree.core.message.MessageDispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author : neo
 * create at:  2020-06-27  22:44
 * @description:
 */
public class RoomSession extends GameRoomSession {

    private MessageDispatcher dispatcher;

    private static final Logger LOG = LoggerFactory.getLogger(Room.class);
    public RoomSession(GameRoomSessionBuilder sessionBuilder, MessageDispatcher dispatcher) {
        super(sessionBuilder);
        this.dispatcher = dispatcher;
    }

    @Override
    public void onLogin(PlayerSession playerSession) {
        SessionHandler listener = new SessionHandler(playerSession, dispatcher);
        playerSession.addHandler(listener);
        LOG.trace("Added event listener in Zombie Room");
    }
}
