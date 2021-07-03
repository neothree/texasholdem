package com.texasthree.game.pineapple;

import com.texasthree.game.texas.Card;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 操作盘
 *
 * @author: neo
 * @create: 2021-06-20 08:43
 */
public class Plate {

    private Integer id;
    /**
     * 已经摆好的牌
     */
    private List<RowCard> layout = new ArrayList<>();
    /**
     * 可用的牌
     */
    private List<Card> waits = new ArrayList<>();
    /**
     * 弃掉的牌
     */
    private List<Card> folds = new ArrayList<>();
    /**
     * 接下来发给玩家的牌
     */
    private List<Card> left;

    Plate(Integer id, List<Card> left) {
        this.id = id;
        this.left = left;
    }

    /**
     * 发牌
     */
    void deal(int num) {
        var w = this.left.subList(0, num);
        w.sort((a, b) -> a.compareToWithSuit(b));
        this.waits = w;
        this.left = this.left.subList(num, this.left.size());
    }

    /**
     * 摆牌
     */
    void put(List<RowCard> rows, boolean before, boolean all, int chooseNum) {
        check(rows, all, chooseNum);

        this.layout.addAll(rows.stream()
                .map(v -> new RowCard(v.card, v.row, before))
                .collect(Collectors.toList()));
        this.layout.sort((a, b) -> a.card.compareToWithSuit(b.card));

        // 弃牌
        var set = rows.stream().map(v -> v.card).collect(Collectors.toSet());
        this.waits.stream()
                .filter(v -> !set.contains(v))
                .forEach(v -> this.folds.add(v));

        this.waits = new ArrayList<>();
    }

    private void check(List<RowCard> rows, boolean all, int chooseNum) {
        // 查看牌是否超过channel数量
        var waitsSet = new HashSet<>(waits);
        if (rows.stream().anyMatch(v -> v == null || !waitsSet.contains(v.card))) {
            throw new IllegalArgumentException("摆牌错误");
        }
        var rowNumMap = new HashMap<Integer, Integer>();
        rows.forEach(v -> rowNumMap.put(v.row, rowNumMap.getOrDefault(v.row, 0) + 1));

        int numHead = rowNumMap.getOrDefault(RowCard.ROW_HEAD, 0), numMiddle = rowNumMap.getOrDefault(RowCard.ROW_MIDDLE, 0), numTail = rowNumMap.getOrDefault(RowCard.ROW_TAIL, 0);
        if (all) {
            // 范特西摆牌
            if (numHead != RowCard.SIZE_HEAD
                    || numMiddle != RowCard.SIZE_MIDDLE
                    || numTail != RowCard.SIZE_TAIL) {
                throw new IllegalArgumentException();
            }
        } else {
            if (rows.size() != chooseNum
                    || this.getRowCards(RowCard.ROW_HEAD).size() + numHead > RowCard.SIZE_HEAD
                    || this.getRowCards(RowCard.ROW_MIDDLE).size() + numMiddle > RowCard.SIZE_MIDDLE
                    || this.getRowCards(RowCard.ROW_TAIL).size() + numTail > RowCard.SIZE_TAIL) {
                throw new IllegalArgumentException();
            }
        }
    }

    void open() {
        this.layout.forEach(v -> v.beforehand = false);
    }

    List<Card> getRowCards(int row) {
        return this.layout.stream()
                .filter(v -> v.row == row)
                .map(v -> v.card)
                .collect(Collectors.toList());
    }

    boolean isStart() {
        return !layout.isEmpty() || !waits.isEmpty();
    }

    int notConcurrentNum() {
        return (int) layout.stream().filter(v -> !v.beforehand).count();
    }

    public Integer getId() {
        return id;
    }

    public List<Card> getLeft() {
        return left;
    }

    public List<Card> getFolds() {
        return folds;
    }

    public List<RowCard> getLayout() {
        return layout;
    }

    public List<Card> getWaits() {
        return waits;
    }
}
