package com.texasthree.game.pineapple;

import com.texasthree.game.texas.Card;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: neo
 * @create: 2021-06-20 08:43
 */
public class Plate {
    private Integer id;
    private List<RowCard> layout = new ArrayList<>();
    private List<Card> waits = new ArrayList<>();
    private List<Card> folds = new ArrayList<>();
    private List<Card> left;

    Plate(Integer id, List<Card> left) {
        this.id = id;
        this.left = left;
    }

    void give(int num) {
        this.waits = this.sort(this.left.subList(0, num));
        this.left = this.left.subList(num, this.left.size());
    }

    void put(List<RowCard> rows, boolean con, boolean all, int chooseNum) {
        check(rows, all, chooseNum);

        for (var v : rows) {
            this.layout.add(new RowCard(v.card, v.row, con));
        }
        this.sortLane(this.layout);

        // 弃牌
        var set = rows.stream().map(v -> v.card).collect(Collectors.toSet());
        this.waits.stream()
                .filter(v -> !set.contains(v))
                .forEach(v -> this.folds.add(v));

        this.waits = new ArrayList<>();
    }

    private void check(List<RowCard> rows, boolean all, int chooseNum) {
        // 查看牌是否超过channel数量
        int num0 = 0, num1 = 0, num2 = 0;
        var waitsSet = new HashSet<>(waits);
        for (var v : rows) {
            if (v == null || !waitsSet.contains(v.card)) {
                throw new IllegalArgumentException();
            }
            switch (v.row) {
                case 0:
                    num0++;
                    break;
                case 1:
                    num1++;
                    break;
                case 2:
                    num2++;
                    break;
                default:
                    throw new IllegalArgumentException();
            }
        }
        if (all) {
            // 范特西摆牌
            if (num0 != 3 || num1 != 5 || num2 != 5) {
                throw new IllegalArgumentException();
            }
        } else {
            if (rows.size() != chooseNum
                    || this.getRowCards(0).size() + num0 > 3
                    || this.getRowCards(1).size() + num1 > 5
                    || this.getRowCards(2).size() + num2 > 5) {
                throw new IllegalArgumentException();
            }
        }
    }

    void doContinue() {
        this.layout.forEach(v -> v.concurrent = false);
    }

    List<Card> getRowCards(int row) {
        return this.layout.stream()
                .filter(v -> v.row == row)
                .map(v -> v.card)
                .collect(Collectors.toList());
    }

    List<Card> sort(List<Card> cards) {
        cards.sort((a, b) -> a.compareToWithSuit(b));
        return cards;
    }

    List<RowCard> sortLane(List<RowCard> cards) {
        cards.sort((a, b) -> a.card.compareToWithSuit(b.card));
        return cards;
    }

    boolean isStart() {
        return !layout.isEmpty() || !waits.isEmpty();
    }

    int notConcurrentNum() {
        return (int) layout.stream().filter(v -> !v.concurrent).count();
    }

    public Integer getId() {
        return id;
    }

    public List<Card> getLeft() {
        return left;
    }
}
