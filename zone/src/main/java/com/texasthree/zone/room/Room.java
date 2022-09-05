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

    private final Seat[] seats;

    private final Map<String, User> audience = new HashMap<>();

    private Map<String, Integer> userChips = new HashMap<>();

    private final RoundEventHandler handler;

    private TexasRound round;

    private Server server;

    private int roundNum;

    private ScheduledEventChecker scheduler = new ScheduledEventChecker();

    public Room(String id, int capacity) {
        seats = new Seat[capacity];
        for (var i = 0; i < capacity; i++) {
            seats[i] = new Seat(id, i);
        }
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
        if (seatId >= seats.length || seats[seatId].occupied()) {
            throw new IllegalArgumentException();
        }
        var chips = this.getUserChips(user.getId());
        if (chips <= 0) {
            throw new IllegalArgumentException("玩家筹码太少 {}" + user + " chips=" + chips);
        }
        seats[seatId].sitDown(user);
        this.audience.remove(user.getId());

        this.onSeat(seatId);

        this.scheduler.once(this::tryStart, 1);
    }

    public void sitUp(User user) {
        for (var v : seats) {
            if (user.getId().equals(user.getId())) {
                this.audience.put(user.getId(), user);
                v.sitUp();
                this.onSeat(v.id);
            }
        }
    }

    private void onSeat(int seatId) {
        var info = new Protocal.Seat();
        info.seatId = seatId;
        var user = seats[seatId].getUser();
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
        var users = new ArrayList<UserPlayer>();
        for (var v : seats) {
            if (v.occupied()) {
                var up = new UserPlayer(v.id, v.getUser(), this.getUserChips(v.getUid()));
                users.add(up);
            }
        }
        if (users.size() < 2) {
            return;
        }

        this.start(users);
    }

    /**
     * 牌局开始
     */
    private void start(List<UserPlayer> users) {
        this.roundNum++;
        log.info("开始牌局");

        this.round = new TexasRound(roundNum, "333111", users, handler);
        try {
            this.round.start(users.get(0).seatId);
        } catch (Exception e) {
            e.printStackTrace();
            log.info("启动牌局失败");
            this.round = null;
        }
    }

    /**
     * 亮牌
     */
    void onShowdown() {
        if (!this.round.finished()) {
            throw new IllegalStateException();
        }
        log.info("桌子一局结束");
        var execute = this.round.getPlayers().stream()
                .filter(UserPlayer::isExecute)
                .map(v -> v.seatId).collect(Collectors.toSet());
        for (var v : this.round.settle()) {
            var profit = v.getProfit();
            var player = this.round.getPlayerBySeatId(v.getId());
            log.info("玩家结算利润 id={} profit={}", player.getId(), profit);
            this.changeUserChips(player.getId(), profit);

            // 获取到押注权限的玩家要记录未操作次数
            var seat = seats[v.getId()];
            if (seat.occupiedBy(player.getId()) && player.isGain()) {
                seat.execute(execute.contains(v.getId()));
            }
        }

        this.round = null;
        this.scheduler.once(this::tryStart, 5000);
    }

    public void loop() {
        this.scheduler.check();
        if (this.round != null) {
            this.round.loop();
        }
    }

    private void send(String uid, Object msg) {
        if (server == null) {
            return;
        }
        this.server.send(uid, msg);
    }

    private void send(Object msg) {
        if (server == null) {
            return;
        }
        var set = new HashSet<>(this.audience.keySet());
        for (var v : seats) {
            if (v.occupied()) {
                set.add(v.getUid());
            }
        }
        server.send(set, msg);
    }

    public int occupiedNum() {
        return (int) Arrays.stream(this.seats)
                .filter(Seat::occupied)
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


    public TexasRound getRound() {
        return this.round;
    }

    public int getRoundNum() {
        return roundNum;
    }

    public void force() {
        this.scheduler.force();
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
        for (var v : seats) {
            if (v.occupied()) {
                var u = v.getUser();
                var p = new Protocal.Player();
                p.uid = u.getId();
                p.name = u.getName();
                p.chips = u.getChips();
                p.seatId = v.id;
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

    private Integer getSeatId(String uid) {
        for (var v : seats) {
            if (uid.equals(v.getUid())) {
                return v.id;
            }
        }
        return null;
    }

    public int getPlayerChips(String uid) {
        var chips = this.getUserChips(uid);
        if (!this.running()) {
            return chips;
        }

        var seatId = this.getSeatId(uid);
        if (seatId == null) {
            return chips;
        }

        if (!this.round.isPlayerInGame(seatId)) {
            return chips;
        }
        return this.round.getPlayerChips(seatId);
    }

    public int getUserChips(String uid) {
        return this.userChips.getOrDefault(uid, 0);
    }

    private int changeUserChips(String uid, int amount) {
        var old = this.getUserChips(uid);
        this.userChips.put(uid, old + amount);
        log.info("修改玩家筹码数 roomId={} uid={} amount={} old={}", id, uid, amount, old);
        return old;
    }

    public int bring(String uid) {
        log.info("房间带入 roomId={} uid={} amount={}", id, uid, initChips);
        return changeUserChips(uid, initChips);
    }

    public int takeout(String uid) {
        var balance = this.getUserChips(uid);
        log.info("房间带出 roomId={} uid={} amount={}", id, uid, balance);
        return this.changeUserChips(uid, -balance);
    }

    List<Seat> getSeats() {
        return Arrays.asList(seats);
    }
}
