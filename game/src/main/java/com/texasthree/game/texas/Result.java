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
public class Result implements Iterable<ResultPlayer> {

    private List<ResultPlayer> players = new ArrayList<>();

    public void add(ResultPlayer p) {
        this.players.add(p);
    }

    public Set<Integer> getWinners() {
        return this.players
                .stream()
                .filter(v -> v.getWin() > 0)
                .map(ResultPlayer::getId)
                .collect(Collectors.toSet());
    }

    public ResultPlayer getPlayer(int id) {
        return this.players.stream().filter(v -> v.id == id).findFirst().get();
    }

    @Override
    public Iterator<ResultPlayer> iterator() {
        return players.iterator();
    }

    public int size() {
        return this.players.size();
    }
}
