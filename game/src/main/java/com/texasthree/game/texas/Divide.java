package com.texasthree.game.texas;

import java.util.*;

/**
 * 分池
 *
 * @author: neo
 * @create: 2021-06-13 17:38
 */
public class Divide {

    public final int id;

    /**
     * 可以进行亮牌玩家的投注
     */
    private Set<Integer> members = new HashSet<>();

    /**
     * 所有玩家的投注
     */
    private Map<Integer, Integer> putin = new HashMap<>();

    public Divide(int id) {
        this.id = id;
    }

    public void add(Set<Integer> all, int amount, Set<Integer> m) {
        for (var key : all) {
            putin.putIfAbsent(key, 0);
            putin.compute(key, (k, v) -> v + amount);
            if (m.contains(key)) {
                members.add(key);
            }
        }
    }

    public boolean contains(Integer id) {
        return members.contains(id);
    }

    public int size() {
        return this.members.size();
    }

    public int chipsBet(int id) {
        return this.putin.getOrDefault(id, 0);
    }

    List<Integer> members() {
        return new ArrayList<>(this.members);
    }

    public int getChips() {
        return this.putin.values().stream().reduce(Integer::sum)
                .orElse(0);
    }
}
