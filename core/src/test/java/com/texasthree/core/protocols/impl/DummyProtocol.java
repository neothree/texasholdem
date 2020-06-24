package com.texasthree.core.protocols.impl;


import com.texasthree.core.app.PlayerSession;
import com.texasthree.core.protocols.Protocol;

public class DummyProtocol implements Protocol
{
	@Override
	public String getProtocolName()
	{
		return null;
	}

	@Override
	public void applyProtocol(PlayerSession playerSession)
	{

	}

	@Override
	public void applyProtocol(PlayerSession playerSession,
			boolean clearExistingProtocolHandlers) {
		
	}
}
