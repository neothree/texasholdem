package com.texasthree.zone.room;


import com.texasthree.utility.packet.Packet;
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

    /**
     * 座位
     */
    private final Seat[] seats;

    /**
     * 观众
     */
    private final Map<String, User> audience = new HashMap<>();

    /**
     * 玩家带入的筹码数
     */
    private Map<String, Integer> userChips = new HashMap<>();

    private final RoundEventHandler handler;

    private TexasRound round;

    private Server server;

    private int roundNum;

    private int smallBlind = 1;

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

    /**
     * 玩家进入房间
     *
     * @param user 玩家
     */
    public void addUser(User user) {
        if (!userChips.containsKey(user.getId())) {
            this.bring(user.getId(), initChips);
        }
        audience.put(user.getId(), user);
    }

    /**
     * 玩家离开房间
     *
     * @param user 玩家
     */
    public void removeUser(User user) {
        log.info("离开房间");
        audience.remove(user.getId());
    }


    /**
     * 购买记分牌
     *
     * @param uid    玩家id
     * @param amount 金额
     * @return
     */
    public int bring(String uid, Integer amount) {
        log.info("房间带入 roomId={} uid={} amount={}", id, uid, amount);
        return changeUserChips(uid, amount);
    }

    /**
     * 提前结算
     *
     * @param uid 玩家id
     * @return
     */
    public int takeout(String uid) {
        var balance = this.getUserChips(uid);
        log.info("房间带出 roomId={} uid={} amount={}", id, uid, balance);
        return this.changeUserChips(uid, -balance);
    }

    /**
     * 玩家坐下
     *
     * @param user   玩家
     * @param seatId 座位号
     */
    public void sitDown(User user, int seatId) {
        Objects.requireNonNull(user);
        if (seatId >= seats.length || seats[seatId].occupied()) {
            throw new IllegalArgumentException();
        }
        if (this.getUserSeat(user.getId()) != null) {
            throw new IllegalArgumentException("玩家已经在座位上");
        }
        var chips = this.getUserChips(user.getId());
        if (chips <= 0) {
            throw new IllegalArgumentException("玩家筹码太少 {}" + user + " chips=" + chips);
        }
        seats[seatId].occupy(user);
        this.audience.remove(user.getId());

        this.scheduler.once(this::tryStart, 2 * 1000);
    }

    /**
     * 玩家站起
     *
     * @param user 玩家
     */
    public void sitUp(User user) {
        for (var v : seats) {
            if (v.occupiedBy(user.getId())) {
                this.audience.put(user.getId(), user);
                v.occupyEnd();
            }
        }
    }

    /**
     * 玩家留座离桌
     *
     * @param user 玩家
     */
    public void pending(User user) {
        var seat = this.getUserSeat(user.getId());
        if (seat != null) {
            seat.pending();
        }
    }

    /**
     * 玩家留座离桌结束
     *
     * @param user 玩家
     */
    public void pendingCancel(User user) {
        var seat = this.getUserSeat(user.getId());
        if (seat != null) {
            seat.pendingCancel();
        }
    }

    /**
     * 尝试开局
     */
    private void tryStart() {
        // 清理一直没有操作的玩家
        for (var v : seats) {
            if (v.occupied() && v.getUser().isReal() && v.getNoExecute() >= 3) {
                log.info("玩家多次没有操作，强制站起 seatId{} user={} noExecute={}", v.id, v.getUser(), v.getNoExecute());
                this.sitUp(v.getUser());
            }
        }

        // 已经有牌局
        if (running()) {
            return;
        }

        // 可以进入牌局的玩家
        var users = Arrays.stream(seats)
                .filter(Seat::occupied)
                .filter(v -> this.getUserChips(v.getUid()) > 0)
                .map(v -> new UserPlayer(v.id, v.getUser(), this.getUserChips(v.getUid())))
                .collect(Collectors.toList());
        if (users.size() < 2) {
            return;
        }

        this.start(users);
    }

    /**
     * 牌局开始
     *
     * @param users 参与牌局的玩家
     */
    private void start(List<UserPlayer> users) {
        this.roundNum++;
        log.info("开始牌局");

        var round = new TexasRound(roundNum, "333111", users, handler);
        round.start(users.get(0).seatId);
        this.round = round;
    }

    /**
     * 亮牌回调
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


    /**
     * 牌局是否在进行中
     */
    public boolean running() {
        return round != null;
    }

    /**
     * 玩家是否在牌局中
     *
     * @param uid 玩家id
     * @return
     */
    public boolean running(String uid) {
        var seat = this.getUserSeat(uid);
        if (seat == null) {
            return false;
        }
        return round != null && round.isPlayerInGame(seat.id);
    }

    public void dispose() {
        log.info("房间解散");
        roomMap.remove(id);
    }

    /**
     * 玩家{@code uid}在房间的筹码数
     * <p>
     * 如果玩家没有在牌局中，则是房间内总带入的数量
     * 如果玩家在牌局中，则是牌局中剩余的筹码数
     *
     * @param uid 玩家id
     * @return
     */
    public int getPlayerChips(String uid) {
        var chips = this.getUserChips(uid);
        if (!this.running()) {
            return chips;
        }

        var seat = this.getUserSeat(uid);
        if (seat == null) {
            return chips;
        }

        if (!this.round.isPlayerInGame(seat.id)) {
            return chips;
        }
        return this.round.getPlayerChips(seat.id);
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

    public List<Seat> getSeats() {
        return Arrays.asList(seats);
    }

    Seat getSeat(int seatId) {
        return seats[seatId];
    }

    public Seat getUserSeat(String uid) {
        for (var v : seats) {
            if (uid.equals(v.getUid())) {
                return v;
            }
        }
        return null;
    }

    public boolean contains(String uid) {
        if (audience.containsKey(uid)) {
            return true;
        }
        return this.getUserSeat(uid) != null;
    }

    int occupiedNum() {
        return (int) Arrays.stream(this.seats)
                .filter(Seat::occupied)
                .count();
    }

    public void setServer(Server server) {
        this.server = server;
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

    private void send(String uid, Object obj) {
        if (server == null) {
            return;
        }
        var info = Packet.convertAsString(obj);
        log.info("[{}] {} {}", id, info, uid);
        this.server.send(uid, info);
    }

    public void send(Object obj) {
        if (server == null) {
            return;
        }
        var set = new HashSet<>(this.audience.keySet());
        for (var v : seats) {
            if (v.occupied() && v.getUser().isReal()) {
                set.add(v.getUid());
            }
        }
        var info = Packet.convertAsString(obj);
        log.info("[{}] {}", id, info);
        server.send(set, info);
    }
}
