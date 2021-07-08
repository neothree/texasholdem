package com.texasthree.game.pineapple;

import com.texasthree.game.texas.Card;

import java.util.List;

/**
 * @author: neo
 * @create: 2021-07-03 09:31
 */
public class ResultPlayer {
    public Integer id;

    public int profit;

    public List<RowResult> rowResultList;

    public List<Integer> honors;

    public List<Card> folds;
}
