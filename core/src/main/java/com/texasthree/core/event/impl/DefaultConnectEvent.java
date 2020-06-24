package com.texasthree.core.event.impl;

import com.texasthree.core.app.Session;
import com.texasthree.core.communication.MessageSender;
import com.texasthree.core.communication.MessageSender.Fast;
import com.texasthree.core.communication.MessageSender.Reliable;
import com.texasthree.core.event.ConnectEvent;
import com.texasthree.core.event.Event;
import com.texasthree.core.event.Events;
import com.texasthree.core.handlers.netty.LoginHandler;
import com.texasthree.core.handlers.netty.UDPUpstreamHandler;

/**
 * This is a specific Event class with type {@link Events#CONNECT}. This class
 * is used by {@link LoginHandler} and {@link UDPUpstreamHandler} to create the
 * respective {@link MessageSender} (upd, or tcp), set it as the source of this
 * event and then forward it to the {@link Session}. <b>Note</b> Trying to reset
 * the event type of this class using {@link Event#setType(int)} will result in
 * an {@link UnsupportedOperationException}.
 *
 * @author Abraham Menacherry
 */
public class DefaultConnectEvent extends DefaultEvent implements ConnectEvent {
    private static final long serialVersionUID = 1L;

    protected Reliable tcpSender;
    protected Fast udpSender;

    public DefaultConnectEvent(Reliable tcpSender) {
        this(tcpSender, null);
    }

    public DefaultConnectEvent(Fast udpSender) {
        this(null, udpSender);
    }

    public DefaultConnectEvent(Reliable tcpSender, Fast udpSender) {
        this.tcpSender = tcpSender;
        this.udpSender = udpSender;
    }

    @Override
    public int getType() {
        return Events.CONNECT;
    }

    @Override
    public void setType(int type) {
        throw new UnsupportedOperationException(
                "Type field is final, it cannot be reset");
    }

    @Override
    public MessageSender getSource() {
        return tcpSender;
    }

    @Override
    public void setSource(Object source) {
        this.tcpSender = (Reliable) source;
    }

    public Reliable getTcpSender() {
        return tcpSender;
    }

    public void setTcpSender(Reliable tcpSender) {
        this.tcpSender = tcpSender;
    }

    public Fast getUdpSender() {
        return udpSender;
    }

    public void setUdpSender(Fast udpSender) {
        this.udpSender = udpSender;
    }
}
