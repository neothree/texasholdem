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

    public int getChips() {
        return chips;
    }

    public void execute() {
        this.execute = true;
    }

    public boolean isExecute() {
        return execute;
    }
}
