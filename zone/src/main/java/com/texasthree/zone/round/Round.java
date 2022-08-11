package com.texasthree.zone.round;

import com.texasthree.game.texas.Action;
import com.texasthree.game.texas.Player;

import java.util.Collection;
import java.util.List;

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
    static Round texas(List<Integer> users) {
        var con = new TexasEventHandler();
        return new TexasRound(users, con::trigger);
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

    boolean isLeave(int id);

    Collection<Player> getPlayers();

    /**
     * 事件循环
     */
    void loop();

    void send(Object obj);

    void send(String uid, Object obj);

    void send(int uid, Object obj);

    Player opPlayer();
}
