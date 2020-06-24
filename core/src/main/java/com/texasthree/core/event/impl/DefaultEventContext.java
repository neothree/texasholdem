package com.texasthree.core.event.impl;

import com.texasthree.core.app.Session;
import com.texasthree.core.event.EventContext;

public class DefaultEventContext implements EventContext {

    private Object attachement;
    private Session session;

    public DefaultEventContext() {

    }

    public DefaultEventContext(Session session, Object attachement) {
        this.session = session;
        this.attachement = attachement;
    }

    @Override
    public Object getAttachment() {
        return attachement;
    }

    @Override
    public Session getSession() {
        return session;
    }

    @Override
    public void setAttachment(Object attachement) {
        this.attachement = attachement;
    }

    @Override
    public void setSession(Session session) {
        this.session = session;
    }

}
