package com.texasthree.appzone.room;

import com.texasthree.appzone.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * @author: neo
 * @create: 2022-09-05 16:43
 */
public class Seat {
    private static Logger log = LoggerFactory.getLogger(Seat.class);

    public final String roomId;

    public final int id;

    private User user;

    /**
     * 没有主动押注次数
     */
    private int noExecute;
    /**
     * 留座离桌开始计时时间
     */
    private LocalDateTime pendingAt;

    Seat(String roomId, int id) {
        this.roomId = roomId;
        this.id = id;
    }


    /**
     * 玩家占据座位
     *
     * @param user 玩家
     */
    void occupy(User user) {
        Objects.requireNonNull(user);
        log.info("玩家坐下 roomId={} seatId={} id={} name={}", roomId, id, user.getId(), user.getName());
        this.user = user;
    }

    /**
     * 玩家离开座位
     */
    void occupyEnd() {
        if (!occupied()) {
            throw new IllegalArgumentException("座位上没有玩家");
        }
        log.info("玩家站起 roomId={} seatId={} id={} name={}", roomId, id, user.getId(), user.getName());
        this.user = null;
        this.noExecute = 0;
        this.pendingAt = null;
    }

    /**
     * 座位是否被占据
     *
     * @return
     */
    public boolean occupied() {
        return this.user != null;
    }

    /**
     * 是否被玩家{@code uid}占据
     *
     * @param uid 玩家id
     * @return
     */
    boolean occupiedBy(String uid) {
        return this.user != null && this.user.getId().equals(uid);
    }

    /**
     * 记录座位是否主动压住
     *
     * @param e 是否主动押注
     */
    void execute(boolean e) {
        if (!occupied()) {
            return;
        }
        if (e) {
            this.noExecute = 0;
        } else {
            this.noExecute++;
        }
    }

    int getNoExecute() {
        return noExecute;
    }

    /**
     * 留座离桌
     */
    void pending() {
        if (occupied()) {
            log.info("留座离桌 roomId={} seatId={} user={}", roomId, id, user);
            this.pendingAt = LocalDateTime.now();
        }
    }

    /**
     * 留座离桌结束
     */
    void pendingCancel() {
        log.info("留座离桌取消 roomId={} seatId={} user={}", roomId, id, user);
        this.pendingAt = null;
    }

    boolean isPending() {
        return this.pendingAt != null;
    }

    String getUid() {
        return this.user != null ? this.user.getId() : null;
    }

    public User getUser() {
        return this.user;
    }

    @Override
    public String toString() {
        return new StringBuilder()
                .append("roomId=").append(roomId)
                .append(", id=").append(id)
                .append(", occupied=").append(occupied())
                .append(", user=").append(user)
                .toString();
    }
}
