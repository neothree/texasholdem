package com.texasthree.room;

import com.texasthree.core.app.impl.GameRoomSession;

import java.util.HashMap;
import java.util.Map;

public class Desk {

    public User[] seats = new User[8];

    private RoundClient round;

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
        this.round.start();
    }

    public void loop() {
        this.loop();
    }

    public void send(Object data) {
    }

    public void send(Object data, User user) {

    }
    public User[] getUsers() {
        return this.seats;
    }
}
