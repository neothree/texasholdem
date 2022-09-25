package com.texasthree.zone.round;

import com.texasthree.game.pineapple.Pineapple;

/**
 * 大菠萝
 *
 * @author: neo
 * @create: 2021-07-09 18:11
 */
class PineappleRound {

    private Pineapple game;

    public void start() {
        this.game = Pineapple.builder().build();
        this.game.start();
    }

    public void action() {
        this.game.action(null, null);
    }

    public boolean finished() {
        return this.game.isOver();
    }

    public void loop() {
    }
}
