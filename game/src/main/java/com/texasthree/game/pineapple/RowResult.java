package com.texasthree.game.pineapple;

import com.texasthree.game.texas.Hand;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: neo
 * @create: 2021-07-03 09:35
 */
public class RowResult {
    public Integer id;
    public int row;
    public int profit;
    public Hand hand;

    /**
     * 每个plate比牌的结果
     */
    public Map<Integer, Integer> compare = new HashMap<>();

    public int getPoint() {
        return this.hand.makePoint(row);
    }
}
