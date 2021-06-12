package com.texasthree.room;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.texasthree.core.app.PlayerSession;
import com.texasthree.core.communication.DeliveryGuaranty;
import com.texasthree.core.communication.NettyMessageBuffer;
import com.texasthree.core.event.Events;
import com.texasthree.core.event.NetworkEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class User {

    static ObjectMapper mapper = new ObjectMapper();

    private static Map<String, User> userMap = new HashMap<>();

    public static User getUser(String id) {
        return userMap.get(id);
    }

    private Cmd.UserData data;

    private PlayerSession session;

    private Room room;

    public User(Cmd.UserData data, PlayerSession session) {
        this.data = data;
        userMap.put(data.id, this);
    }

    public void enter(Room room) {
        room.addUser(this);
        this.room = room;
    }

    public void leave(Room room) {
        room.removeUser(this);
        this.room = null;
    }

    public Room getRoom() {
        return this.room;
    }

    public String getId() {
        return this.data.id;
    }

    public int getChips() {
        return this.data.chips;
    }

    @Override
    public String toString() {
        return this.data.name + ":" + this.data.id;
    }

    public void send(Object msg) {
        if (session != null) {
            send(this.session, msg);
        }
    }

    public static void send(PlayerSession ps, Object msg) {
        try {
            Cmd.Command cmd = new Cmd.Command();
            cmd.name = msg.getClass().getSimpleName();
            cmd.data = mapper.writeValueAsString(msg);
            String send = mapper.writeValueAsString(cmd);

            NettyMessageBuffer buffer = new NettyMessageBuffer();
            buffer.writeString(send);
            NetworkEvent event = Events.networkEvent(buffer, DeliveryGuaranty.DeliveryGuarantyOptions.RELIABLE);
            ps.onEvent(event);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
