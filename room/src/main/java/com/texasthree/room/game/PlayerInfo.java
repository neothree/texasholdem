package com.texasthree.room.game;

import com.texasthree.room.User;

/**
 * 玩家统计信息
 *
 * @author : guoqing
 * create at:  2020-06-30  10:23
 */
public class PlayerInfo {
    public int position;

    public User user;

    @Override
    public String toString() {
        return this.user.toString() + ":" + position;
    }
}
