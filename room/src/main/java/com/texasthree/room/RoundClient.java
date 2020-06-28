package com.texasthree.room;

import com.texasthree.round.texas.Law;
import com.texasthree.round.texas.TableCard;
import com.texasthree.round.texas.Texas;

import java.util.HashMap;
import java.util.Map;

public class RoundClient {

    /*
     * 庄家位
     */
    private int dealer = 0;

    private Texas texas;

    public void start(RoundBuilder builder) {
        if (texas != null) {
            return;
        }

        texas = builder.build();
        try {
            texas.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean runing() {
        return texas != null;
    }
}
