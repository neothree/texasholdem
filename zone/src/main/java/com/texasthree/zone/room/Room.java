package com.texasthree.zone.room;


import com.texasthree.game.texas.Card;
import com.texasthree.zone.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;


public class Room {

    private static Logger log = LoggerFactory.getLogger(Room.class);

    private static Map<String, Room> roomMap = new HashMap<>();

    public static Room getRoom(String id) {
        return roomMap.get(id);
    }

    public static Collection<Room> all() {
        return roomMap.values();
    }

    private Desk desk;

    private final String id;

    private final int capacity;

    public Room(String id, int capacity) {
        this.id = id;
        this.capacity = capacity;
        this.desk = new Desk(capacity);
        roomMap.put(id, this);
        log.info("创建房间 {}", id);
    }

    public void addUser(User user) {
        this.desk.addUser(user);
    }

    public void removeUser(User user) {
        this.desk.removeUser(user);
    }

    public void sitDown(User user, int seatId) {
        this.desk.sitDown(user, seatId);
    }

    public void sitUp(User user) {
        this.desk.sitUp(user);
    }

    public void loop() {
        this.desk.loop();
    }

    @Override
    public String toString() {
        return this.id;
    }

    public String getId() {
        return id;
    }

    public Desk getDesk() {
        return desk;
    }

    public int getCapacity() {
        return capacity;
    }

    public Protocal.RoomData data() {
        var info = new Protocal.RoomData();
        info.id = id;
        info.name = "test";
        info.ante = 1;
        info.smallBlind = 1;
        info.button = 1;
        info.capacity = capacity;
        info.seats = new ArrayList<>();

        // 座位
        var seats = desk.getSeats();
        for (var i = 0; i < capacity; i++) {
            var v = seats[i];
            if (v != null) {
                var p = new Protocal.Player();
                p.uid = v.getId();
                p.name = v.getName();
                p.chips = v.getChips();
                p.seatId = i;
                info.seats.add(p);
            }
        }

        // 牌局
        var round = desk.getRound();
        if (round != null) {

            var rd = new Protocal.RoundData();
            rd.dealer = round.dealer();
            rd.sbSeatId = round.sbSeatId();
            rd.bbSeatId = round.bbSeatId();
            rd.sumPot = round.sumPot();
            rd.circle = round.circle();
            rd.pots = round.getPots();
            rd.communityCards = round.getCommunityCards().stream().map(Card::getId).collect(Collectors.toList());
            rd.players = new ArrayList<>();
            for (var v : round.getPlayers()) {
                var p = new Protocal.Player();
                p.seatId = v.seatId;
                p.uid = v.getId();
            }
            info.round = rd;
        }

        return info;
    }
}
