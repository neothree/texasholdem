package com.texasthree.zone.room;

import com.texasthree.zone.user.User;

/**
 * @author: neo
 * @create: 2022-09-05 16:43
 */
public class Seat {
    public final int id;

    public User user;

    Seat(int id) {
        this.id = id;
    }
}
