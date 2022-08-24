package com.texasthree.zone.room;

import com.texasthree.zone.net.Server;
import com.texasthree.zone.round.TexasEventHandler;
import com.texasthree.zone.round.TexasRound;
import com.texasthree.zone.round.UserPlayer;
import com.texasthree.zone.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class Desk {

    private static Logger log = LoggerFactory.getLogger(Desk.class);

    private User[] seats = new User[8];

    private TexasRound round;

    private Server server;

    private Map<String, User> audience = new HashMap<>();

    private TexasEventHandler handler = new TexasEventHandler(this);


    public void addUser(User user) {
        audience.put(user.getId(), user);
    }

    public void removeUser(User user) {
        log.info("离开房间");
        audience.remove(user.getId());
    }

    /**
     * 玩家坐下
     */
    public void sitDown(User user, int seatId) {
        if (seatId >= seats.length || seats[seatId] != null) {
            throw new IllegalArgumentException();
        }
        log.info("玩家坐下 id={} name={} seatId={}", user.getId(), user.getName(), seatId);
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
                log.info("玩家站起 id={} name={} seatId={}", user.getId(), user.getName(), i);
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
        if (running()) {
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
        log.info("开始牌局");
        var users = new ArrayList<UserPlayer>();
        for (var i = 0; i < this.seats.length; i++) {
            var user = this.seats[i];
            if (user != null) {
                var up = new UserPlayer(i, user);
                users.add(up);
            }
        }

        this.round = new TexasRound(users, handler);
        try {
            this.round.start(users.get(0).seatId);
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
        if (server == null) {
            return;
        }
        this.server.send(uid, msg);
    }

    public void send(Object msg) {
        if (server == null) {
            return;
        }
        var set = new HashSet<>(this.audience.keySet());
        for (var v : seats) {
            if (v != null) {
                set.add(v.getId());
            }
        }
        server.send(set, msg);
    }

    public int playerNum() {
        return (int) Arrays.stream(this.seats)
                .filter(Objects::nonNull)
                .count();
    }

    public void setServer(Server server) {
        this.server = server;
    }


    /**
     * 牌局是否在进行中
     */
    public boolean running() {
        return round != null;
    }
}
