package com.texasthree.core.event.impl;

import com.texasthree.core.app.GameRoom;
import com.texasthree.core.app.Session;
import com.texasthree.core.event.Event;
import com.texasthree.core.event.Events;
import com.texasthree.core.event.NetworkEvent;
import com.texasthree.core.event.SessionEventHandler;

/**
 * A listener class which will be used by {@link GameRoom} to send
 * {@link NetworkEvent}s to the connected sessions. When the game room
 * publishes such events to its channel, this listener will pick it up and
 * transmit it to the session which in turn will transmit it to the remote
 * machine/vm.
 *
 * @author Abraham Menacherry
 */
public class NetworkEventListener implements SessionEventHandler {

    private static final int EVENT_TYPE = Events.NETWORK_MESSAGE;
    private final Session session;

    public NetworkEventListener(Session session) {
        this.session = session;
    }

    @Override
    public void onEvent(Event event) {
        session.onEvent(event);
    }

    @Override
    public int getEventType() {
        return EVENT_TYPE;
    }

    @Override
    public Session getSession() {
        return session;
    }

    @Override
    public void setSession(Session session) {
        throw new UnsupportedOperationException(
                "Session is a final field in this class. "
                        + "It cannot be reset");
    }

}
