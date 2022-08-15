package com.texasthree.utility.utlis;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author: neo
 * @create: 2022-02-02 15:36
 */
public class MathUtils {

    public static int setBit(int x, int pos) {
        return x |= (1 << pos);
    }

    public static int clearBit(int x, int pos) {
        var s = ~(1 << pos);
        return x & s;
    }

    public static Integer probability(Integer[] weights) {
        var list = new ArrayList<Item>(weights.length);
        for (var i = 0; i < weights.length; i++) {
            list.add(new Item(weights[i], i));
        }

        //按照权重排序
        list.sort((a, b) -> a.weight - b.weight);

        // 概率选择
        int index = 0;
        var sum = list.stream().mapToInt(v -> v.weight).sum();
        var randomNumber = ThreadLocalRandom.current().nextInt(sum);
        randomNumber = ThreadLocalRandom.current().nextInt(sum);
        randomNumber = ThreadLocalRandom.current().nextInt(sum);
        for (var v : list) {
            randomNumber -= v.weight;
            if (randomNumber < 0) {
                index = v.index;
                break;
            }
        }

        return index;
    }

    private static class Item {
        public int weight;
        public int index;

        Item(int weight, int index) {
            this.weight = weight;
            this.index = index;
        }
    }

}
