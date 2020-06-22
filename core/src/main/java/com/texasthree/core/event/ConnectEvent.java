package com.texasthree.core.event;

import com.texasthree.core.communication.MessageSender.Fast;
import com.texasthree.core.communication.MessageSender.Reliable;

public interface ConnectEvent extends Event
{
	public Reliable getTcpSender();
	public void setTcpSender(Reliable tcpSender);
	public Fast getUdpSender();
	public void setUdpSender(Fast udpSender);
}
