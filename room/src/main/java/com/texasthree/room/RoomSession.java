package com.texasthree.room;

import com.texasthree.core.app.PlayerSession;
import com.texasthree.core.app.impl.GameRoomSession;

/**
 * @author : neo
 * create at:  2020-06-27  22:44
 * @description:
 */
public class RoomSession extends GameRoomSession {

    public RoomSession(GameRoomSessionBuilder sessionBuilder) {
        super(sessionBuilder);
    }

    @Override
    public void onLogin(PlayerSession playerSession) {

    }
}
