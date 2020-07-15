package com.texasthree;

import com.texasthree.core.app.Game;
import com.texasthree.core.app.impl.GameRoomSession;
import com.texasthree.core.app.impl.SimpleGame;
import com.texasthree.core.message.MessageDispatcher;
import com.texasthree.core.protocols.Protocol;
import com.texasthree.core.server.ServerManager;
import com.texasthree.core.service.LookupService;
import com.texasthree.core.service.impl.SimpleLookupService;
import com.texasthree.proto.Cmd;
import com.texasthree.room.Room;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;

@SpringBootApplication
public class RoomApplication {
    private static final Logger LOG = LoggerFactory.getLogger(RoomApplication.class);

    @Autowired
    private ServerManager serverManager;

    @Autowired
    private MessageDispatcher dispatcher;

    @Autowired
    protected LookupService lookupService;

    @Autowired
    private Protocol messageBufferProtocol;

    public static void main(String[] args) throws Exception {
        SpringApplication.run(RoomApplication.class);
    }

    @PostConstruct
    private void start() {
        try {
            serverManager.startServers();
            dispatcher.register("com.texasthree.room");
            LOG.info("Room 启动成功");

            this.createRoom();
        } catch (Exception e) {
            e.printStackTrace();
            LOG.error("Unable to start servers cleanly: {}", e.getMessage());
            System.exit(1);
        }
    }

    private void createRoom() {
        String name = "Room";
        Game game = new SimpleGame(1001, "Texasholdm");
        GameRoomSession.GameRoomSessionBuilder sessionBuilder = new GameRoomSession.GameRoomSessionBuilder();
        sessionBuilder.parentGame(game)
                .gameRoomName(name)
                .protocol(messageBufferProtocol);

        Cmd.RoomData data = new Cmd.RoomData();
        data.id = name;
        data.name = name;
        data.creator = name;
        Room room = new Room(sessionBuilder, data, dispatcher);
        ((SimpleLookupService) this.lookupService).getRefKeyGameRoomMap().put(room.getId(), room);
    }
}
