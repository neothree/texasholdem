package com.texasthree.shell.client.event;

import com.texasthree.shell.client.app.Session;

/**
 * In addition to handling events this handler will also have a reference to the
 * session.
 *
 * @author Abraham Menacherry.
 */
public interface SessionEventHandler extends EventHandler {
    Session getSession();

    void setSession(Session session);
}
