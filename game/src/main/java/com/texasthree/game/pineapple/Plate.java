package com.texasthree.game.pineapple;

import com.texasthree.game.texas.Card;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: neo
 * @create: 2021-06-20 08:43
 */
@Data
public class Plate {
    public Integer id;
    public List<RowCard> layout = new ArrayList<>();
    public List<Card> waits = new ArrayList<>();
    public List<Card> folds = new ArrayList<>();
    public List<Card> left = new ArrayList<>();
}
