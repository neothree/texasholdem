package com.texasthree.room;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.texasthree.room.game.TexasGame;

import java.util.HashMap;
import java.util.Map;

public class Desk {

    static ObjectMapper mapper = new ObjectMapper();

    public User[] seats = new User[8];

    private TexasGame game;

    private Map<String, User> audience = new HashMap<>();

    public Desk() {
    }

    public void addUser(User user) {
        audience.put(user.getId(), user);
    }

    public void removeUser(User user) {
        audience.remove(user.getId());
    }

    public void sitDown(User user, int position) {
        if (position >= seats.length || seats[position] != null) {
            throw new IllegalArgumentException();
        }

        seats[position] = user;
        this.audience.remove(user.getId());
    }

    public void sitUp(int position) {
        var user = seats[position];
        if (user == null) {
            return;
        }

        this.audience.put(user.getId(), user);
        seats[position] = null;
    }

    public void start() {
        this.game = new TexasGame(this.seats, (Object msg) -> this.send(msg));
        try {
            this.game.start();
        } catch (Exception e) {
            e.printStackTrace();
            this.game = null;
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
