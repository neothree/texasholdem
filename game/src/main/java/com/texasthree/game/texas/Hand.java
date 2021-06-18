package com.texasthree.game.texas;

import lombok.Getter;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * 手牌
 */
@Getter
public class Hand implements Comparable<Hand> {
    /**
     * 手里的两张牌
     */
    private List<Card> hold;
    /**
     * 牌型
     */
    private CardType type;
    /**
     * 最好的牌
     */
    private List<Card> best;
    /**
     * 关键牌
     */
    private List<Card> keys;
    /**
     * 桌面的底牌
     */
    private List<Card> bottom;

    public Hand(List<Card> hold) {
        this.hold = hold;
    }

    private Hand(List<Card> best, CardType type) {
        this.best = best;
        this.type = type;
    }

    void fresh(List<Card> bottom) {
        this.bottom = bottom;
        var list = new ArrayList<>(this.hold);
        list.addAll(this.bottom);
        var hand = typeOf(list);
        this.type = hand.type;
        this.best = hand.best;
        this.keys = keysOf(list, hand.type);
    }

    @Override
    public int compareTo(Hand other) {
        return compare(this, other);
    }

    @Override
    public String toString() {
        var sb = new StringBuilder().append("hold: ");
        if (hold != null) {
            for (var v : hold) {
                sb.append(v).append(" ");
            }
        }
        sb.append("\nbest: ");
        if (best != null) {
            for (var v : best) {
                sb.append(v).append(" ");
            }

        }
        sb.append(type);
        sb.append("\nkeys: ");
        if (keys != null) {
            for (var v : keys) {
                sb.append(v).append(" ");
            }
        }
        return sb.toString();
    }

    static int MIN_POINT = 2;

    /**
     * 计算牌型
     */
    static Hand typeOf(Collection<Card> cards) {
        return typeOf(cards, MIN_POINT);
    }

    /**
     * 计算牌型
     */
    static Hand typeOf(Collection<Card> cards, int min) {
        var list = new ArrayList<>(cards);
        // 从小到大排列
        sort(list, true);
        var profile = new Profile(list, min);

        // 金刚
        var hand = findFourOfKind(profile);
        if (hand != null) {
            return hand;
        }

        // 皇家同花顺
        hand = findRoyalFlush(profile);
        if (hand != null) {
            return hand;
        }

        // 同花顺
        hand = findStraightFlush(profile);
        if (hand != null) {
            return hand;
        }

        if (min == MIN_POINT) {
            // 葫芦大于同花
            hand = findFullHouse(profile);
            if (hand != null) {
                return hand;
            }

            hand = findNormalFlush(profile);
            if (hand != null) {
                return hand;
            }
        } else {
            // 同花大于葫芦
            hand = findNormalFlush(profile);
            if (hand != null) {
                return hand;
            }

            hand = findFullHouse(profile);
            if (hand != null) {
                return hand;
            }
        }

        // 顺子
        hand = findStraight(profile.list, profile.min);
        if (hand != null) {
            return hand;
        }

        // 三张
        hand = findThreeOfKind(profile);
        if (hand != null) {
            return hand;
        }

        // 两对
        hand = findTwoPairs(profile);
        if (hand != null) {
            return hand;
        }

        // 一对
        hand = findOnePair(profile);
        if (hand != null) {
            return hand;
        }

        return findHighCard(profile.list);
    }

    /**
     * 比较手牌a, b的大小
     */
    static int compare(Hand a, Hand b) {
        return compare(a, b, MIN_POINT);
    }

    static int compare(Hand a, Hand b, int min) {
        if (min != MIN_POINT) {
            if (CardType.Flush.equals(a.getType()) && CardType.FullHouse.equals(b.getType())) {
                return 1;
            }
            if (CardType.Flush.equals(b.getType()) && CardType.FullHouse.equals(a.getType())) {
                return -1;
            }
        }
        int ret = a.getType().getWeight().compareTo(b.getType().getWeight());
        return ret != 0 ? ret : compareSameType(a, b, min);
    }

    /**
     * 类型相同手牌a, b的大小
     */
    private static int compareSameType(Hand a, Hand b, int min) {
        switch (b.getType()) {
            case HighCard:
            case Flush:
                return compareHighCard(a.getBest(), b.getBest());
            case OnePair:
            case ThreeOfKind:
            case FourOfKind:
                return compreOnePair(a.getBest(), b.getBest());
            case TwoPairs:
                return compareTwoPairs(a.getBest(), b.getBest());
            case Straight:
            case StraightFlush:
                return compareStraight(a.getBest(), b.getBest(), min);
            case FullHouse:
                return compareFullHouse(a.getBest(), b.getBest());
            default:
                return 0;
        }
    }

    /**
     * 金刚
     */
    private static Hand findFourOfKind(Profile p) {
        var four = p.pointCountMap.get(4);
        if (four == null) {
            return null;
        }

        // 最大的四张牌
        sortList(four, false);
        var list = four.get(0);

        // 一张剩余的最大牌
        var max = 0;
        for (var key : p.pointMap.keySet()) {
            if (key > max && !p.pointMap.get(key).get(0).point.equals(list.get(0).point)) {
                max = key;
            }
        }
        if (max > 0) {
            list.add(p.pointMap.get(max).get(0));
        }
        return new Hand(list, CardType.FourOfKind);
    }

    /**
     * 顺子
     */
    private static Hand findStraight(List<Card> list, int min) {
        if (list.size() < 5) {
            return null;
        }

        list = list.stream().filter(distinctByKey(v -> v.point)).collect(Collectors.toList());
        if (list.size() < 5) {
            return null;
        }

        sort(list, true);
        for (int last = list.size() - 1; last >= 4; last--) {
            var find = true;
            for (int i = last; i >= last - 3; i--) {
                if (list.get(i).point - list.get(i - 1).point != 1) {
                    find = false;
                }
            }
            if (find) {
                return new Hand(list.subList(last - 4, last + 1), CardType.Straight);
            }
        }

        // A 2 3 4 5 也是顺子
        if (list.get(list.size() - 1).point == 14
                && list.get(0).point == min
                && list.get(1).point == min + 1
                && list.get(2).point == min + 2
                && list.get(3).point == min + 3) {
            var find = list.subList(0, 4);
            find.add(list.get(list.size() - 1));
            return new Hand(find, CardType.Straight);
        }
        return null;
    }

    private static Hand findFlush(Profile p, CardType type) {
        Hand ret = null;
        for (var suit : p.suitMap.keySet()) {
            var v = p.suitMap.get(suit);
            if (v.size() < 5) {
                continue;
            }

            // 同花
            if (CardType.Flush.equals(type)) {
                Hand other = new Hand(v.size() == 5 ? v : findHighCard(v).getBest(), type);
                if (ret == null || other.compareTo(ret) == 1) {
                    ret = other;
                }
            } else {
                var straight = findStraight(v, p.min);
                if (straight != null
                        && ((CardType.RoyalFlush.equals(type) && straight.getBest().get(4).point == 14 && straight.getBest().get(0).point == 10) || CardType.StraightFlush.equals(type))
                        && (ret == null || compare(straight, ret) == 1)) {
                    ret = new Hand(straight.getBest(), type);
                }
            }
        }
        return ret;
    }

    /**
     * 皇家同花顺
     */
    private static Hand findRoyalFlush(Profile p) {
        return findFlush(p, CardType.RoyalFlush);
    }

    /**
     * 同花顺
     */
    private static Hand findStraightFlush(Profile p) {
        return findFlush(p, CardType.StraightFlush);
    }

    /**
     * 同花
     */
    private static Hand findNormalFlush(Profile p) {
        return findFlush(p, CardType.Flush);
    }


    /**
     * 葫芦
     */
    private static Hand findFullHouse(Profile p) {
        var three = p.pointCountMap.get(3);
        var two = p.pointCountMap.get(2);
        if (three == null || (three.size() == 1 && two == null)) {
            return null;
        }


        // 找出三张
        sortList(three, false);
        var list = new ArrayList<>(three.get(0));

        // 找出两张
        if (three.size() == 2) {
            list.addAll(three.get(1).subList(0, 2));
        } else {
            sortList(two, false);
            list.addAll(two.get(0));
        }

        return new Hand(list, CardType.FullHouse);
    }

    /**
     * 三张
     */
    private static Hand findThreeOfKind(Profile p) {
        var three = p.pointCountMap.get(3);
        if (three == null) {
            return null;
        }

        var find = new ArrayList<Card>(three.get(0));
        sort(p.list, false);
        if (p.list.get(0).point.equals(p.list.get(1).point)) {
            if (p.list.size() > 3) {
                find.add(p.list.get(3));
            }
            if (p.list.size() > 4) {
                find.add(p.list.get(4));
            }
        } else if (p.list.get(1).point.equals(p.list.get(2).point)) {
            find.add(p.list.get(0));
            if (p.list.size() > 4) {
                find.add(p.list.get(4));
            }
        } else {
            find.add(p.list.get(0));
            find.add(p.list.get(1));
        }

        return new Hand(find, CardType.ThreeOfKind);
    }

    /**
     * 两对
     */
    private static Hand findTwoPairs(Profile p) {
        var two = p.pointCountMap.get(2);
        if (two == null || two.size() < 2) {
            return null;
        }

        sortList(two, false);

        // 两对已经确定，找出剩余牌里的最大一张
        // 从单张中找出剩余一张
        var max = 0;
        var one = p.pointCountMap.get(1);
        if (one != null) {
            for (var v : one) {
                if (v.get(0).point > max) {
                    max = v.get(0).point;
                }
            }
        }

        // 从一对中找出剩余一张
        if (two.size() > 2 && two.get(2).get(0).point > max) {
            max = two.get(2).get(0).point;
        }

        var find = new ArrayList<Card>();
        find.addAll(two.get(0));
        find.addAll(two.get(1));
        find.add(p.pointMap.get(max).get(0));
        return new Hand(find, CardType.TwoPairs);
    }

    /**
     * 一对
     */
    private static Hand findOnePair(Profile p) {
        var two = p.pointCountMap.get(2);
        if (two == null || two.size() != 1) {
            return null;
        }

        var find = new ArrayList<>(two.get(0));
        for (int i = 14; i >= 2; i--) {
            var v = p.pointMap.get(i);
            if (v != null && v.size() == 1) {
                find.add(v.get(0));
                if (find.size() == 5) {
                    break;
                }
            }
        }
        return new Hand(find, CardType.OnePair);
    }

    /**
     * 寻找高牌
     */
    private static Hand findHighCard(List<Card> list) {
        var find = list.stream().filter(distinctByKey(v -> v.point)).collect(Collectors.toList());
        sort(find, false);
        return new Hand(find.size() >= 5 ? find.subList(0, 5) : find, CardType.HighCard);
    }

    private static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        var seen = new ConcurrentHashMap<Object, Boolean>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

    /**
     * 高牌比较
     */
    private static int compareHighCard(List<Card> a, List<Card> b) {
        sort(a, false);
        sort(b, false);

        int size = Math.max(a.size(), b.size());
        for (int i = 0; i < size; i++) {
            if (a.size() <= i) {
                return -1;
            } else if (b.size() <= i) {
                return 1;
            }
            var ret = a.get(i).compareTo(b.get(i));
            if (ret != 0) {
                return ret;
            }
        }
        return 0;
    }

    private static void print(List<Card> a) {
        var sb = new StringBuilder();
        for (var v : a) {
            sb.append(v).append(" ");
        }
        System.out.println(sb.toString());
    }


    private static Integer findSamePoint(List<Card> a) {
        for (int i = 0; i < a.size() - 1; i++) {
            if (a.get(i).point.equals(a.get(i + 1).point)) {
                return a.get(i).point;
            }
        }
        return null;

    }

    /**
     * 一对比较
     */
    private static int compreOnePair(List<Card> a, List<Card> b) {
        var ap = findSamePoint(a);
        var bp = findSamePoint(b);
        if (ap > bp) {
            return 1;
        } else if (ap < bp) {
            return -1;
        }
        return compareHighCard(a, b);
    }

    private static int compareTwoPairs(List<Card> a, List<Card> b) {
        var al = new ArrayList<Integer>();
        var bl = new ArrayList<Integer>();
        for (int i = 0; i < a.size() - 1; i++) {
            if (a.get(i).point.equals(a.get(i + 1).point)) {
                al.add(a.get(i).point);
            }
            if (b.get(i).point.equals(b.get(i + 1).point)) {
                bl.add(b.get(i).point);
            }
        }
        if (al.get(1) > bl.get(1)) {
            return 1;
        } else if (al.get(1) < bl.get(1)) {
            return -1;
        } else if (al.get(0) < al.get(0)) {
            return 1;
        } else if (al.get(0) < bl.get(0)) {
            return -1;
        }
        return compareHighCard(a, b);

    }

    private static int compareStraight(List<Card> a, List<Card> b, int min) {
        if (a.get(4).point == 14 && a.get(0).point == min && b.get(4).point != 14) {
            return -1;
        }
        if (b.get(4).point == 14 && b.get(0).point == min && a.get(4).point != 14) {
            return 1;
        }
        return compareHighCard(a, b);
    }

    private static int compareFullHouse(List<Card> a, List<Card> b) {
        Card a3, a2, b3, b2;
        if (a.get(0).point.equals(a.get(2).point)) {
            a3 = a.get(0);
            a2 = a.get(4);
        } else {
            a3 = a.get(4);
            a2 = a.get(0);
        }
        if (b.get(0).point.equals(b.get(2).point)) {
            b3 = b.get(0);
            b2 = b.get(4);
        } else {
            b3 = b.get(4);
            b2 = b.get(0);
        }

        int ret = a3.compareTo(b3);
        return ret != 0 ? ret : a2.compareTo(b2);
    }

    public static List<Card> keysOf(Collection<Card> cards, CardType type) {
        var list = new ArrayList<>(cards);
        var profile = new Profile(list, MIN_POINT);
        sort(list, true);
        if (CardType.HighCard == type) {
            return Arrays.asList(list.get(list.size() - 1));
        } else if (CardType.OnePair == type) {
            return profile.pointCountMap.get(2).get(0);
        } else if (CardType.TwoPairs == type) {
            var ret = new ArrayList<>(profile.pointCountMap.get(2).get(0));
            ret.addAll(profile.pointCountMap.get(2).get(1));
            return ret;
        } else if (CardType.ThreeOfKind == type) {
            return profile.pointCountMap.get(3).get(0);
        } else if (CardType.FourOfKind == type) {
            return profile.pointCountMap.get(4).get(0);
        } else {
            return new ArrayList<>(list);
        }
    }

    private static void sort(List<Card> list, boolean asc) {
        if (asc) {
            // 升序
            list.sort((a, b) -> a.compareTo(b));
        } else {
            list.sort((a, b) -> b.compareTo(a));
        }
    }

    private static void sortList(List<List<Card>> list, boolean asc) {
        if (asc) {
            // 升序
            list.sort((a, b) -> a.get(0).compareTo(b.get(0)));
        } else {
            list.sort((a, b) -> b.get(0).compareTo(a.get(0)));
        }
    }

    private static class Profile {

        public List<Card> list;

        Map<Integer, List<Card>> pointMap;

        Map<Integer, List<Card>> suitMap;

        Map<Integer, List<List<Card>>> pointCountMap;

        int min = 2;

        Profile(List<Card> list, int min) {
            this.list = list;
            this.pointMap = this.toPointMap(list);
            this.pointCountMap = this.toPointCountMap(this.pointMap);
            this.suitMap = this.toSuitMap(list);
            this.min = min;

        }

        public Profile(List<Card> list) {
            this(list, 2);
        }

        private Map<Integer, List<Card>> toPointMap(List<Card> list) {
            var ret = new HashMap<Integer, List<Card>>();
            for (var v : list) {
                if (!ret.containsKey(v.point)) {
                    ret.put(v.point, new ArrayList<>());
                }
                ret.get(v.point).add(v);
            }
            return ret;
        }

        private Map<Integer, List<Card>> toSuitMap(List<Card> list) {
            var ret = new HashMap<Integer, List<Card>>();
            for (var v : list) {
                if (!ret.containsKey(v.suit)) {
                    ret.put(v.suit, new ArrayList<>());
                }
                ret.get(v.suit).add(v);
            }
            return ret;
        }

        private Map<Integer, List<List<Card>>> toPointCountMap(Map<Integer, List<Card>> pointMap) {
            var ret = new HashMap<Integer, List<List<Card>>>();
            for (var key : pointMap.keySet()) {
                var value = pointMap.get(key);
                if (!ret.containsKey(value.size())) {
                    ret.put(value.size(), new ArrayList<>());
                }
                ret.get(value.size()).add(value);
            }
            return ret;
        }
    }
}
