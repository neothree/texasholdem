package com.texasthree.zone.round;

import com.texasthree.zone.entity.User;

/**
 * 玩家统计信息
 *
 * @author : guoqing
 * create at:  2020-06-30  10:23
 */
public class PlayerInfo {
    public int seatId;

    public User user;

    @Override
    public String toString() {
        return this.user.toString() + ":" + seatId;
    }
}
