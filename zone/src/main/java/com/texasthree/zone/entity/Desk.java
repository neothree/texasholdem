package com.texasthree.zone.entity;

import com.texasthree.zone.round.Round;

import java.util.HashMap;
import java.util.Map;

public class Desk {

    public User[] seats = new User[8];

    private Round round;

    private Map<String, User> audience = new HashMap<>();

    public Desk() {
    }

    /**
     * 添加玩家
     */
    public void addUser(User user) {
        audience.put(user.getId(), user);
    }

    /**
     * 移除玩家
     */
    public void removeUser(User user) {
        audience.remove(user.getId());
    }

    /**
     * 玩家坐下
     */
    public void sitDown(User user, int position) {
        if (position >= seats.length || seats[position] != null) {
            throw new IllegalArgumentException();
        }

        seats[position] = user;
        this.audience.remove(user.getId());
    }

    /**
     * 玩家站起
     */
    public void sitUp(User user) {
        for (var i = 0; i < seats.length; i++) {
            var v = seats[i];
            if (v != null && v.getId().equals(user.getId())) {
                this.audience.put(user.getId(), user);
                seats[i] = null;
            }
        }
    }

    public void start() {
        this.round = Round.texas(this.seats, (Object msg) -> this.send(msg));
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
        var set = this.audience.keySet();
        for (var v : seats) {
            if (v != null) {
                set.add(v.getId());
            }
        }
        User.send(msg, set);
    }

    public User[] getUsers() {
        return this.seats;
    }
}
