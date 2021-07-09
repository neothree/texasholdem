package com.texasthree.room.round;

import com.texasthree.game.texas.Action;
import com.texasthree.room.User;

import java.util.function.Consumer;

/**
 * 一局牌局
 *
 * @author: neo
 * @create: 2021-07-09 17:58
 */
public interface Round {

    /**
     * 创建德州扑克
     */
    static Round texas(User[] users, Consumer send) {
        return new TexasRound(users, send);
    }

    /**
     * 创建大菠萝
     */
    static Round pineapple() {
        return new PineappleRound();
    }

    /**
     * 开始
     */
    void start();

    /**
     * 操作
     */
    void action(Action action);

    /**
     * 事件循环
     */
    void loop();
}
