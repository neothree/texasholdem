package com.texasthree.game.pineapple;

import com.texasthree.game.texas.Card;

import java.util.List;
import java.util.Set;

/**
 * @author: neo
 * @create: 2021-07-03 09:31
 */
public class ResultPlayer {

    public static final String HONOR_EXPLODE = "HONOR_EXPLODE";

    public static final String HONOR_FANTASY = "HONOR_FANTASY";

    public Integer id;

    public int profit;

    public List<RowResult> rowResultList;

    public Set<String> honors;

    public List<Card> folds;
}
