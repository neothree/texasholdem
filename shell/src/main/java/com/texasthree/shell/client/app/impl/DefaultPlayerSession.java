package com.texasthree.shell.client.app.impl;

import com.texasthree.shell.client.app.Player;
import com.texasthree.shell.client.app.PlayerSession;
import com.texasthree.shell.client.event.Event;

/**
 * This implementation of the {@link PlayerSession} interface is used to both
 * receive and send messages to a particular player using the
 * {@link Event}{@link #onEvent(Event)}.
 *
 * @author Abraham Menacherry
 */
public class DefaultPlayerSession extends DefaultSession implements PlayerSession {

    /**
     * Each player session belongs to a Player. This variable holds the
     * reference.
     */
    final protected Player player;

    protected DefaultPlayerSession(SessionBuilder sessionBuilder, Player player) {
        super(sessionBuilder);
        this.player = player;
    }

    @Override
    public Player getPlayer() {
        return player;
    }

}
