package com.texasthree.core.app.impl;

import com.texasthree.core.app.GameRoom;
import com.texasthree.core.app.Player;
import com.texasthree.core.app.PlayerSession;
import com.texasthree.core.app.Session;
import com.texasthree.core.app.SessionFactory;
import com.texasthree.core.app.impl.DefaultPlayerSession.PlayerSessionBuilder;
import com.texasthree.core.app.impl.DefaultSession.SessionBuilder;


/**
 * Factory class used to create a {@link PlayerSession} instance. It will
 * create a new instance, initialize it and set the {@link GameRoom} reference
 * if necessary.
 * 
 * @author Abraham Menacherry
 * 
 */
public class Sessions implements SessionFactory
{

	public static final SessionFactory INSTANCE = new Sessions();
	
	@Override
	public Session newSession()
	{
		return new SessionBuilder().build();
	}
	
	@Override
	public PlayerSession newPlayerSession(GameRoom gameRoom, Player player)
	{
		return new PlayerSessionBuilder().parentGameRoom(gameRoom).player(player).build();
	}

}
