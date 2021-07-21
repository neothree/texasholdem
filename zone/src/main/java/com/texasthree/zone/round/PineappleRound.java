package com.texasthree.zone.round;

import com.texasthree.game.pineapple.Pineapple;
import com.texasthree.game.texas.Action;

/**
 * 大菠萝
 *
 * @author: neo
 * @create: 2021-07-09 18:11
 */
class PineappleRound implements Round {

    private Pineapple game;

    @Override
    public void start() {
        this.game = Pineapple.builder().build();
        this.game.start();
    }

    @Override
    public void action(Action action) {
        this.game.action(null, null);
    }

    @Override
    public boolean finished() {
        return this.game.isOver();
    }

    @Override
    public void loop() {
    }
}
