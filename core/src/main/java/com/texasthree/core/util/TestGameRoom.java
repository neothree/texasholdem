package com.texasthree.core.util;

import com.texasthree.core.app.PlayerSession;
import com.texasthree.core.app.impl.GameRoomSession;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;



public class TestGameRoom extends GameRoomSession {

	private final AtomicLong counter;
	private final CountDownLatch latch;

	public TestGameRoom(GameRoomSessionBuilder gameRoomSessionBuilder,
			AtomicLong counter, CountDownLatch latch) {
		super(gameRoomSessionBuilder);
		this.counter = counter;
		this.latch = latch;
	}

	@Override
	public void onLogin(PlayerSession playerSession) {
		SessionHandlerLatchCounter handler = new SessionHandlerLatchCounter(
				playerSession, counter, latch);
		playerSession.addHandler(handler);
	}

}
