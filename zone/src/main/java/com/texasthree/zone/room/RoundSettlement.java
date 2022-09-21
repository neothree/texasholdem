package com.texasthree.zone.room;

import com.texasthree.game.texas.Settlement;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author: neo
 * @create: 2022-09-21 11:00
 */
public class RoundSettlement implements Iterable<RoundSettlementEntry> {

    private final List<RoundSettlementEntry> entries = new ArrayList<>();

    RoundSettlement(Settlement settlement, Map<Integer, Integer> claims) {
        for (var v : settlement) {
            var info = new RoundSettlementEntry(v.getId(), v.getProfit(), claims.getOrDefault(v.getId(), 0), v.getPot());
            entries.add(info);
        }
    }

    @Override
    public Iterator<RoundSettlementEntry> iterator() {
        return entries.iterator();
    }
}
