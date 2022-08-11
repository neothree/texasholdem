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
    private int chips;

    private Map<Integer, Integer> members = new HashMap<>();

    private Map<Integer, Integer> putin = new HashMap<>();

    public int getChips() {
        return chips;
    }

    public void setChips(int chips) {
        this.chips = chips;
    }

    public Map<Integer, Integer> getMembers() {
        return members;
    }

    public void setMembers(Map<Integer, Integer> members) {
        this.members = members;
    }

    public Map<Integer, Integer> getPutin() {
        return putin;
    }

    public void setPutin(Map<Integer, Integer> putin) {
        this.putin = putin;
    }
}
