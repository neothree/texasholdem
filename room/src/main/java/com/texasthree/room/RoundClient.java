package com.texasthree.room;

import com.texasthree.room.game.TexasGame;

public class RoundClient {

    private TexasGame texas;

    private Desk desk;

    public RoundClient(Desk desk) {
        this.desk = desk;
    }

    public void start() {
        if (texas != null) {
            return;
        }

        this.texas = new TexasGame(desk, null);
    }


    public boolean runing() {
        return texas != null;
    }

    public void loop() {
        texas.loop();
    }
}
