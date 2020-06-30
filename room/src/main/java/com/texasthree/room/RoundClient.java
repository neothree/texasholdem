package com.texasthree.room;

import com.texasthree.room.game.TexasGame;

public class RoundClient {

    /*
     * 庄家位
     */
    private int dealer = 0;

    private TexasGame texas;

    public void start(RoundBuilder builder) {
        if (texas != null) {
            return;
        }


    }



    public boolean runing() {
        return texas != null;
    }

    public void loop() {
        texas.loop();
    }
}
