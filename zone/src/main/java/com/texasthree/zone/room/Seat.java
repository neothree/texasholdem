package com.texasthree.zone.room;

import com.texasthree.zone.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    Seat(String roomId, int id) {
        this.roomId = roomId;
        this.id = id;
    }

    void sitDown(User user) {
        Objects.requireNonNull(user);
        log.info("玩家坐下 roomId={} seatId={} id={} name={}", roomId, id, user.getId(), user.getName());
        this.user = user;
    }

    void sitUp() {
        log.info("玩家站起 roomId={} seatId={} id={} name={}", roomId, id, user.getId(), user.getName());
        this.user = null;
    }

    boolean occupied() {
        return this.user != null;
    }

    String getUid() {
        return this.user != null ? this.user.getId() : null;
    }

    User getUser() {
        return this.user;
    }
}
