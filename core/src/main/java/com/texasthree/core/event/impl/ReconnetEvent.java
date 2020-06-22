package com.texasthree.core.event.impl;

import com.texasthree.core.communication.MessageSender.Reliable;
import com.texasthree.core.event.Events;

public class ReconnetEvent extends DefaultConnectEvent
{
	private static final long serialVersionUID = 1L;

	public ReconnetEvent(Reliable tcpSender)
	{
		super(tcpSender, null);
	}

	public int getType()
	{
		return Events.RECONNECT;
	}
}
