package com.texasthree.core.app.impl;

import com.texasthree.core.app.Game;
import com.texasthree.core.app.GameRoom;
import com.texasthree.core.app.PlayerSession;
import com.texasthree.core.app.Session;
import com.texasthree.core.app.impl.GameRoomSession.GameRoomSessionBuilder;
import com.texasthree.core.event.Event;
import com.texasthree.core.event.Events;
import com.texasthree.core.event.NetworkEvent;
import com.texasthree.core.event.impl.DefaultSessionEventHandler;
import com.texasthree.core.event.impl.ExecutorEventDispatcher;
import com.texasthree.core.protocols.Protocol;
import com.texasthree.core.protocols.impl.DummyProtocol;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.assertTrue;


public class PlayerSessionWithEventDispatcherTest {
    private static final Protocol DUMMY_PROTOCOL = new DummyProtocol();
    private static final AtomicLong COUNTER = new AtomicLong(0l);
    private static final int NUM_OF_GAME_ROOMS = 1000;
    private static final int SESSIONS_PER_GAME_ROOM = 50;
    private static final int EVENTS_PER_SESSION = 1;
    private static final int LATCH_COUNT = ((NUM_OF_GAME_ROOMS * SESSIONS_PER_GAME_ROOM) * (EVENTS_PER_SESSION * SESSIONS_PER_GAME_ROOM))
            + (EVENTS_PER_SESSION * SESSIONS_PER_GAME_ROOM * NUM_OF_GAME_ROOMS);
    private static final CountDownLatch LATCH = new CountDownLatch(LATCH_COUNT);
    private static Game game;
    private static List<GameRoom> gameRoomList;
    private static List<Session> sessionList;

    @BeforeAll
    public static void setUp() {
        game = new SimpleGame(1, "Test");
        gameRoomList = new ArrayList<GameRoom>(NUM_OF_GAME_ROOMS);
        sessionList = new ArrayList<Session>(NUM_OF_GAME_ROOMS
                * SESSIONS_PER_GAME_ROOM);
        for (int i = 1; i <= NUM_OF_GAME_ROOMS; i++) {
            GameRoomSessionBuilder sessionBuilder = new GameRoomSessionBuilder();
            sessionBuilder.parentGame(game).gameRoomName("Zombie_ROOM_" + i)
                    .protocol(DUMMY_PROTOCOL).eventDispatcher(new ExecutorEventDispatcher());
            Session gameRoomSession = new TestGameRoom(sessionBuilder);
            gameRoomSession.addHandler(new GameRoomSessionHandler(gameRoomSession));
            gameRoomList.add((GameRoom) gameRoomSession);
        }
        for (GameRoom gameRoom : gameRoomList) {
            for (int j = 1; j <= SESSIONS_PER_GAME_ROOM; j++) {
                PlayerSession playerSession = gameRoom.createPlayerSession(null);
                gameRoom.connectSession(playerSession);
                playerSession.addHandler(new SessionHandler(playerSession));
                sessionList.add(playerSession);
            }
        }
    }

    @Test
//	@Category(Performance.class)
    public void eventHandlingPerformance() throws InterruptedException {
        long start = System.nanoTime();
        Thread t = new Thread(new Runnable() {

            @Override
            public void run() {
                for (Session session : sessionList) {
                    for (int i = 1; i <= EVENTS_PER_SESSION; i++) {
                        Event event = Events.event(null, Events.SESSION_MESSAGE);
                        session.onEvent(event);
                    }
                }
            }
        }, "Event-Injector");
        t.start();

        assertTrue(LATCH.await(20, TimeUnit.SECONDS));
        long time = System.nanoTime() - start;
        System.out.printf(
                "Took  %.3f seconds to pass %d messages between sessions\n",
                time / 1e9, COUNTER.get());
        System.out.printf("Message passing rate was %.3f million messages/sec",
                COUNTER.get() / ((time / 1e9) * 1000000));
    }

    private static class TestGameRoom extends GameRoomSession {
        protected TestGameRoom(GameRoomSessionBuilder gameRoomSessionBuilder) {
            super(gameRoomSessionBuilder);
        }

        @Override
        public void onLogin(PlayerSession playerSession) {
            SessionHandler handler = new SessionHandler(playerSession);
            playerSession.addHandler(handler);
        }
    }

    private static class GameRoomSessionHandler extends DefaultSessionEventHandler {
        public GameRoomSessionHandler(Session session) {
            super(session);
        }

        @Override
        public void onNetworkMessage(NetworkEvent event) {
            COUNTER.incrementAndGet();
            LATCH.countDown();
        }
    }

    private static class SessionHandler extends DefaultSessionEventHandler {
        public SessionHandler(Session session) {
            super(session);
        }

        @Override
        public void onNetworkMessage(NetworkEvent event) {
            COUNTER.incrementAndGet();
            LATCH.countDown();
        }
    }
}
