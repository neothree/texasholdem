package com.texasthree.core.event.impl;

import com.texasthree.core.app.Session;
import com.texasthree.core.event.Events;
import com.texasthree.core.event.SessionEventHandler;

/**
 * This abstract helper class can be used to quickly create a listener which
 * listens for SESSION_MESSAGE events. Child classes need to override the
 * onEvent to plugin the logic.
 * 
 * @author Abraham Menacherry
 * 
 */
public abstract class SessionMessageHandler implements SessionEventHandler {

	private final Session session;
	
	public SessionMessageHandler(Session session)
	{
		this.session = session;
	}
	
	@Override
	public int getEventType() {
		return Events.SESSION_MESSAGE;
	}

	@Override
	public Session getSession() {
		return session;
	}

	@Override
	public void setSession(Session session)
			throws UnsupportedOperationException {
		throw new UnsupportedOperationException(
				"Session instance is final and cannot be reset on this handler");
	}

}
