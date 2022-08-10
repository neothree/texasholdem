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

    private Map<String, Integer> members = new HashMap<>();

    private Map<String, Integer> putin = new HashMap<>();

    public int getChips() {
        return chips;
    }

    public void setChips(int chips) {
        this.chips = chips;
    }

    public Map<String, Integer> getMembers() {
        return members;
    }

    public void setMembers(Map<String, Integer> members) {
        this.members = members;
    }

    public Map<String, Integer> getPutin() {
        return putin;
    }

    public void setPutin(Map<String, Integer> putin) {
        this.putin = putin;
    }
}
