package com.texasthree.game.texas;

import java.util.HashMap;
import java.util.Map;

/**
 * 分池
 *
 * @author: neo
 * @create: 2021-06-13 17:38
 */
public class Divide {
    public final int id;

    private int chips;

    private Map<Integer, Integer> members = new HashMap<>();

    private Map<Integer, Integer> putin = new HashMap<>();

    Divide(int id) {
        this.id = id;
    }

    public int getChips() {
        return chips;
    }

    public void setChips(int chips) {
        this.chips = chips;
    }

    public Map<Integer, Integer> getMembers() {
        return members;
    }

    public Map<Integer, Integer> getPutin() {
        return putin;
    }

}
