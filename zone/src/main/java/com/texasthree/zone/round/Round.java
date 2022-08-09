package com.texasthree.zone.round;

import com.texasthree.game.texas.Action;
import com.texasthree.zone.entity.User;

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
    static Round texas(User[] users) {
        var con = new RoundEventHandler();
        return new TexasRound(users, con::trigger);
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

    boolean finished();

    /**
     * 事件循环
     */
    void loop();
}
