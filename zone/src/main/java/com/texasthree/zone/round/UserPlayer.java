package com.texasthree.zone.round;

import com.texasthree.zone.entity.User;

public class UserPlayer {

    public final int seatId;

    public final User user;

    public UserPlayer(int seatId, User user) {
        this.seatId = seatId;
        this.user = user;
    }

    @Override
    public String toString() {
        return this.user.toString() + ":" + seatId;
    }

    public String getId() {
        return this.user.getId();
    }
}
