package com.texasthree.room;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.texasthree.room.game.TexasGame;

import java.util.HashMap;
import java.util.Map;

public class Desk {

    static ObjectMapper mapper = new ObjectMapper();

    public User[] seats = new User[8];

    private TexasGame round;

    private Map<String, User> audience = new HashMap<>();

    public Desk() {
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

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public User[] getUsers() {
        return this.seats;
    }
}
