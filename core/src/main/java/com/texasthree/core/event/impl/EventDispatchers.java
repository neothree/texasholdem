package com.texasthree.core.event.impl;

import com.texasthree.core.app.GameRoom;
import com.texasthree.core.concurrent.Fibers;
import com.texasthree.core.concurrent.Lane;
import com.texasthree.core.concurrent.LaneStrategy;
import com.texasthree.core.event.Event;
import com.texasthree.core.event.EventDispatcher;

import java.util.concurrent.ExecutorService;

import org.jetlang.channels.MemoryChannel;
import org.jetlang.fibers.Fiber;

public class EventDispatchers
{
	public static EventDispatcher newJetlangEventDispatcher(GameRoom room,
			LaneStrategy<String, ExecutorService, GameRoom> strategy)
	{
		Fiber fiber = null;
		JetlangEventDispatcher dispatcher = null;
		if (null == room)
		{
			fiber = Fibers.pooledFiber();
			dispatcher = new JetlangEventDispatcher(new MemoryChannel<Event>(),
					fiber, null);
		}
		else
		{
			Lane<String, ExecutorService> lane = strategy.chooseLane(room);
			fiber = Fibers.pooledFiber(lane);
			dispatcher = new JetlangEventDispatcher(new MemoryChannel<Event>(),
					fiber, lane);
		}
		dispatcher.initialize();

		return dispatcher;
	}
}
