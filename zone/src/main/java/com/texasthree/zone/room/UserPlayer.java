package com.texasthree.zone.room;

import com.texasthree.zone.user.User;

public class UserPlayer {

    public final int seatId;

    private final User user;

    private int chips;

    /**
     * 是否主动执行押注
     */
    private boolean execute = false;

    private boolean gain = false;

    public UserPlayer(int seatId, User user, int chips) {
        this.seatId = seatId;
        this.user = user;
        this.chips = chips;
    }

    @Override
    public String toString() {
        return this.user.toString() + ":" + seatId;
    }

    public String getId() {
        return this.user.getId();
    }

     int getChips() {
        return chips;
    }

     void gain() {
        this.gain = true;
    }

     boolean isGain() {
        return gain;
    }

     void execute() {
        this.execute = true;
    }

     boolean isExecute() {
        return execute;
    }
}
