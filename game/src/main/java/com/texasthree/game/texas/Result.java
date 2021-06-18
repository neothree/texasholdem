package com.texasthree.game.texas;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 牌局结算信息
 *
 * @author: neo
 * @create: 2021-06-14 10:46
 */
public class Result {
    public Map<Integer, ResultPlayer> playersMap = new HashMap<>();

    public Set<Integer> getWinners() {
        return this.playersMap.values()
                .stream()
                .filter(v -> v.getWin() > 0)
                .map(ResultPlayer::getId)
                .collect(Collectors.toSet());
    }
}
