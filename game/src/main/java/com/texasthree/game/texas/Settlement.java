package com.texasthree.game.texas;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 牌局结算信息
 *
 * @author: neo
 * @create: 2021-06-14 10:46
 */
public class Settlement implements Iterable<SettlementItem> {

    private List<SettlementItem> players = new ArrayList<>();

    public void add(SettlementItem p) {
        this.players.add(p);
    }

    public Set<Integer> getWinners() {
        return this.players
                .stream()
                .filter(v -> v.getWin() > 0)
                .map(SettlementItem::getId)
                .collect(Collectors.toSet());
    }

    public SettlementItem getPlayer(int id) {
        return this.players.stream().filter(v -> v.id == id).findFirst().get();
    }

    @Override
    public Iterator<SettlementItem> iterator() {
        return players.iterator();
    }

    public int size() {
        return this.players.size();
    }
}
