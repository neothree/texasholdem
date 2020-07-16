package com.texasthree.room;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.texasthree.core.app.impl.GameRoomSession;
import com.texasthree.core.communication.DeliveryGuaranty;
import com.texasthree.core.communication.NettyMessageBuffer;
import com.texasthree.core.event.Events;
import com.texasthree.core.event.NetworkEvent;
import com.texasthree.proto.Cmd;
import com.texasthree.room.game.TexasGame;

import java.util.HashMap;
import java.util.Map;

public class Desk {

    static ObjectMapper mapper = new ObjectMapper();

    public User[] seats = new User[8];

    private TexasGame round;

    private GameRoomSession session;

    private Map<String, User> audience = new HashMap<>();

    public Desk(GameRoomSession session) {
        this.session = session;
    }

    public void addUser(User user) {
        audience.put(user.getId(), user);
    }

    public void removeUser(User user) {
        audience.remove(user.getId());
    }

    public void sitdown(User user, int position) {
        if (position >= seats.length || position < 0) {
            return;
        }
        if (seats[position] != null) {
            return;
        }

        seats[position] = user;
        this.audience.remove(user.getId());
    }

    public void situp(int position) {
        User user = seats[position];
        if (user == null) {
            return;
        }

        this.audience.put(user.getId(), user);
        seats[position] = null;
    }

    public void start() {
        this.round = new TexasGame(this.seats, (Object msg) -> this.send(msg));
        try {
            this.round.start();
        } catch (Exception e) {
            e.printStackTrace();
            this.round = null;
        }
    }

    public void loop() {
        this.loop();
    }

    public void send(Object msg) {
        try {
            Cmd.Command cmd = new Cmd.Command();
            cmd.name = msg.getClass().getSimpleName();
            cmd.data = mapper.writeValueAsString(msg);
            String send = mapper.writeValueAsString(cmd);

            NettyMessageBuffer buffer = new NettyMessageBuffer();
            buffer.writeString(send);
            NetworkEvent event = Events.networkEvent(buffer, DeliveryGuaranty.DeliveryGuarantyOptions.RELIABLE);
            session.onEvent(event);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public User[] getUsers() {
        return this.seats;
    }
}
