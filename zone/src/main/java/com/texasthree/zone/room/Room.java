package com.texasthree.zone.room;


import com.texasthree.game.texas.Card;
import com.texasthree.zone.net.Server;
import com.texasthree.zone.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
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

    public static final int initChips = 2000;

    private final String id;

    private final int capacity;

    private final User[] seats;

    private final Map<String, User> audience = new HashMap<>();

    private Map<String, Integer> userChips = new HashMap<>();

    private final RoundEventHandler handler;

    private TexasRound round;

    private Server server;

    private int roundNum;

    private ScheduledEventChecker checker = new ScheduledEventChecker();

    public Room(String id, int capacity) {
        seats = new User[capacity];
        handler = new RoundEventHandler(this::onShowdown, this::send, this::send);
        this.id = id;
        this.capacity = capacity;
        roomMap.put(id, this);
        log.info("创建房间 {}", id);
    }

    public void addUser(User user) {
        if (!userChips.containsKey(user.getId())) {
            this.bring(user.getId());
        }
        audience.put(user.getId(), user);
    }

    public void removeUser(User user) {
        log.info("离开房间");
        audience.remove(user.getId());
    }

    public void sitDown(User user, int seatId) {

        if (seatId >= seats.length || seats[seatId] != null) {
            throw new IllegalArgumentException();
        }
        log.info("玩家坐下 id={} name={} seatId={}", user.getId(), user.getName(), seatId);
        seats[seatId] = user;
        this.audience.remove(user.getId());

        this.onSeat(seatId);

        this.tryStart();
    }

    public void sitUp(User user) {
        for (var i = 0; i < seats.length; i++) {
            var v = seats[i];
            if (v != null && v.getId().equals(user.getId())) {
                log.info("玩家站起 id={} name={} seatId={}", user.getId(), user.getName(), i);
                this.audience.put(user.getId(), user);
                seats[i] = null;
                this.onSeat(i);
            }
        }
    }

    private void onSeat(int seatId) {
        var info = new Protocal.Seat();
        info.seatId = seatId;
        var user = this.getSeats()[seatId];
        if (user != null) {
            var p = new Protocal.Player();
            p.uid = user.getId();
            p.name = user.getName();
            p.chips = user.getChips();
            info.player = p;
        }
        this.send(info);
    }


    /**
     * 尝试开局
     */
    private void tryStart() {
        if (roundNum >= 10) {
            log.info("已经进行了 10 局比赛，不再开启");
            return;
        }

        // 已经有牌局
        if (running()) {
            return;
        }

        // 座位的人数
        if (playerNum() < 2) {
            return;
        }

        // 准备开始
        this.checker.once(this::start, 2000);
    }

    /**
     * 牌局开始
     */
    private void start() {
        this.roundNum++;
        log.info("开始牌局");
        var users = new ArrayList<UserPlayer>();
        for (var i = 0; i < this.seats.length; i++) {
            var user = this.seats[i];
            if (user != null) {
                var up = new UserPlayer(i, user);
                users.add(up);
            }
        }

        this.round = new TexasRound(roundNum, "333111", users, handler);
        try {
            this.round.start(users.get(0).seatId);
        } catch (Exception e) {
            e.printStackTrace();
            log.info("启动牌局失败");
            this.round = null;
        }
    }


    public void onShowdown() {
        log.info("桌子一局结束");
        this.round = null;
        this.checker.once(this::tryStart, 3000);
    }

    public void loop() {
        this.checker.check();
        if (this.round != null) {
            this.round.loop();
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


    public User[] getSeats() {
        return Arrays.copyOf(seats, seats.length);
    }

    public TexasRound getRound() {
        return this.round;
    }

    public int getRoundNum() {
        return roundNum;
    }

    public void force() {
        this.checker.force();
    }

    @Override
    public String toString() {
        return this.id;
    }

    public String getId() {
        return id;
    }

    public int getCapacity() {
        return capacity;
    }

    public void dispose() {
        log.info("房间解散");
        roomMap.remove(id);
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
        var seats = this.getSeats();
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
        var round = this.getRound();
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

    public int getUserChips(String uid) {
        return this.userChips.getOrDefault(uid, 0);
    }

    public int bring(String uid) {
        var now = this.getUserChips(uid) + initChips;
        this.userChips.put(uid, now);
        return now;
    }

    public int takeout(String uid) {
        var balance = this.getUserChips(uid);
        this.userChips.remove(uid);
        return balance;
    }

}
