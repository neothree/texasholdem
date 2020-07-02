package com.texasthree.shell.client.event.impl;

import com.texasthree.shell.client.app.Session;
import com.texasthree.shell.client.event.Events;
import com.texasthree.shell.client.event.SessionEventHandler;

/**
 * A handler to handle "START" event sent by the server. It is after the start
 * event that other changes for e.g. to protocol or sending of data from client
 * should be done.
 *
 * @author Abraham Menacherry
 */
public abstract class StartEventHandler implements SessionEventHandler {

    private final Session session;

    public StartEventHandler(Session session) {
        this.session = session;
    }

    @Override
    public int getEventType() {
        return Events.START;
    }

    @Override
    public Session getSession() {
        return session;
    }

    @Override
    public void setSession(Session session) {
        throw new UnsupportedOperationException("Cannot set session again");
    }

}
