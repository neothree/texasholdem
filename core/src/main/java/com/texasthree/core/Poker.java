package com.texasthree.core;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Poker {

    public static Hand Best(List<Card> list) {
        return null;
    }

    /**
     * 寻找高牌
     */
    private static List<Card> FindHighCard(List<Card> list) {
        if (list.size() < 5) {
            return null;
        }
        list.stream().filter(distinctByKey(v -> v.point)).collect(Collectors.toList());
        Collections.sort(list, (a, b) -> a.point.compareTo(b.point));
        return list;
    }

    private static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

    public static int compare(Hand a, Hand b) {
        return 1;
    }

}
