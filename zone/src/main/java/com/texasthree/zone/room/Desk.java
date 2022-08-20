package com.texasthree.zone.room;

import com.texasthree.zone.round.TexasEventHandler;
import com.texasthree.zone.round.TexasRound;
import com.texasthree.zone.round.UserPlayer;
import com.texasthree.zone.user.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Desk {

    private User[] seats = new User[8];

    private TexasRound round;

    private Map<String, User> audience = new HashMap<>();


    public void addUser(User user) {
        audience.put(user.getId(), user);
    }

    public void removeUser(User user) {
        audience.remove(user.getId());
    }

    /**
     * 玩家坐下
     */
    public void sitDown(User user, int seatId) {
        if (seatId >= seats.length || seats[seatId] != null) {
            throw new IllegalArgumentException();
        }

        seats[seatId] = user;
        this.audience.remove(user.getId());
        this.tryStart();
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

    /**
     * 尝试开局
     */
    private void tryStart() {

        // 已经有牌局
        if (round != null) {
            return;
        }

        // 座位的人数
        if (playerNum() < 2) {
            return;
        }
        this.start();
    }

    /**
     * 牌局开始
     */
    private void start() {
        var users = new ArrayList<UserPlayer>();
        for (var i = 0; i < this.seats.length; i++) {
            var user = this.seats[i];
            if (user != null) {
                var up = new UserPlayer(i, user);
                users.add(up);
            }
        }
        var con = new TexasEventHandler(this);
        this.round = new TexasRound(users, con);
        try {
            this.round.start();
        } catch (Exception e) {
            e.printStackTrace();
            this.round = null;
        }
    }

    public void loop() {
        if (round != null) {
            round.loop();
        }
    }

    public void send(String uid, Object msg) {

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

    public int playerNum() {
        return (int) Arrays.stream(this.seats)
                .filter(v -> v != null)
                .count();
    }
}
