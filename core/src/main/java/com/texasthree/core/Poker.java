package com.texasthree.core;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

class Profile {

    public List<Card> list;

    public Map<Integer, List<Card>> pointMap;

    public Map<Integer, List<Card>> suitMap;

    public Map<Integer, List<List<Card>>> pointCountMap;

    public int min = 2;

    public Profile(List<Card> list) {
        this.list = list;
        this.pointMap = this.toPointMap(list);
        this.pointCountMap = this.toPointCountMap(this.pointMap);
        this.suitMap = this.toSuitMap(list);
    }

    private Map<Integer, List<Card>> toPointMap(List<Card> list) {
        Map<Integer, List<Card>> ret = new HashMap<>();
        for (Card v : list) {
            if (!ret.containsKey(v.point)) {
                ret.put(v.point, new ArrayList<>());
            }
            ret.get(v.point).add(v);
        }
        return ret;
    }

    private Map<Integer, List<Card>> toSuitMap(List<Card> list) {
        Map<Integer, List<Card>> ret = new HashMap<>();
        for (Card v : list) {
            if (!ret.containsKey(v.suit)) {
                ret.put(v.suit, new ArrayList<>());
            }
            ret.get(v.suit).add(v);
        }
        return ret;
    }

    private Map<Integer, List<List<Card>>> toPointCountMap(Map<Integer, List<Card>> pointMap) {
        Map<Integer, List<List<Card>>> ret = new HashMap<>();
        for (Integer key : pointMap.keySet()) {
            List<Card> value = pointMap.get(key);
            if (!ret.containsKey(value.size())) {
                ret.put(value.size(), new ArrayList<>());
            }
            ret.get(value.size()).add(value);
        }
        return ret;
    }

}


public class Poker {


    /**
     * 计算牌型
     */
    public static Hand typeOf(List<Card> list) {
        return typeOf(list, 2);
    }

    /**
     * 计算牌型
     */
    public static Hand typeOf(List<Card> list, int min) {
        // 从小到大排列
        sort(list, true);
        Profile profile = new Profile(list);

        // 金刚
        Hand hand = findFourOfKind(profile);
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

        if (min == 2) {
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
        hand = findStraight(profile);
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

        return findHighCard(profile);
    }

    /**
     * 比较手牌a, b的大小
     */
    public static int compare(Hand a, Hand b) {
        return compare(a, b, 2);
    }

    public static int compare(Hand a, Hand b, int min) {
        if (min != 2) {
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
                return compareHighCard(a.getList(), b.getList());
            case OnePair:
            case ThreeOfKind:
            case FourOfKind:
                return compreOnePair(a.getList(), b.getList());
            case TwoPairs:
                return compareTwoPairs(a.getList(), b.getList());
            case Straight:
            case StraightFlush:
                return compareStraight(a.getList(), b.getList(), min);
            case FullHouse:
                return compareFullHouse(a.getList(), b.getList());
            default:
                return 0;
        }
    }

    /**
     * 金刚
     */
    private static Hand findFourOfKind(Profile p) {
        List<List<Card>> four = p.pointCountMap.get(4);
        if (four == null) {
            return null;
        }

        // 最大的四张牌
        sortList(four, false);
        List<Card> list = four.get(0);

        // 一张剩余的最大牌
        Integer max = 0;
        for (Integer key : p.pointMap.keySet()) {
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
    private static Hand findStraight(Profile p) {
        if (p.list.size() < 5) {
            return null;
        }

        List<Card> list = p.list.stream().filter(distinctByKey(v -> v.point)).collect(Collectors.toList());
        if (list.size() < 5) {
            return null;
        }

        sort(list, true);
        for (int last = list.size(); last >= 5; last--) {
            boolean find = true;
            for (int i = last; i >= last - 3; i--) {
                if (list.get(i).point - list.get(i - 1).point != 1) {
                    find = false;
                }
            }
            if (find) {
                return new Hand(list.subList(last - 4, last), CardType.Straight);
            }
        }

        // A 2 3 4 5 也是顺子
        if (list.get(list.size()).point == 14
                && list.get(0).point == p.min
                && list.get(0).point == p.min + 1
                && list.get(0).point == p.min + 2
                && list.get(0).point == p.min + 3) {
            List<Card> straight = list.subList(0, 4);
            straight.add(list.get(list.size()));
            return new Hand(straight, CardType.Straight);
        }
        return null;
    }

    private static Hand findFlush(Profile p, CardType type) {

        Hand ret = null;
        for (Integer suit : p.suitMap.keySet()) {
            List<Card> v = p.suitMap.get(suit);
            if (v.size() < 5) {
                continue;
            }

            // 同花
            if (CardType.Flush.equals(type)) {
                Hand other = new Hand(v.size() == 5 ? v : findHighCard(p).getList(), type);
                if (ret == null || other.compareTo(ret) == 1) {
                    ret = other;
                }
            } else {
                Hand straight = findStraight(p);
                if (straight != null
                        && (CardType.RoyalFlush.equals(type) && straight.getList().get(4).point == 14 && straight.getList().get(0).point == 10 || CardType.StraightFlush.equals(type))
                        && (ret == null || compare(straight, ret) == 1)) {
                    ret = new Hand(straight.getList(), type);
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
        List<List<Card>> three = p.pointCountMap.get(3);
        if (three == null) {
            return null;
        }

        // 找出三张
        sortList(three, false);
        List<Card> list = new ArrayList<>();
        list.addAll(three.get(0));

        // 找出两张
        List<List<Card>> two = p.pointCountMap.get(2);
        if (three.size() == 2) {
            list.addAll(three.get(1).subList(0, 2));
        } else if (three.size() == 1 && two != null) {
            sortList(two, false);
            list.addAll(two.get(0));
        }
        return !list.isEmpty() ? new Hand(list, CardType.FullHouse) : null;
    }

    /**
     * 三张
     */
    private static Hand findThreeOfKind(Profile p) {
        List<List<Card>> three = p.pointCountMap.get(3);
        if (three == null) {
            return null;
        }

        List<Card> find = new ArrayList<>();
        find.addAll(three.get(0));
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
        List<List<Card>> two = p.pointCountMap.get(2);
        if (two == null || two.size() < 2) {
            return null;
        }

        sortList(two, false);

        // 两对已经确定，找出剩余牌里的最大一张
        // 从单张中找出剩余一张
        Integer max = 0;
        List<List<Card>> one = p.pointCountMap.get(1);
        if (one != null) {
            for (List<Card> v : one) {
                if (v.get(0).point > max) {
                    max = v.get(0).point;
                }
            }
        }

        // 从一对中找出剩余一张
        if (two.size() > 2 && two.get(2).get(0).point > max) {
            max = two.get(2).get(0).point;
        }

        List<Card> find = new ArrayList<>();
        find.addAll(two.get(0));
        find.addAll(two.get(1));
        find.add(p.pointMap.get(max).get(0));
        return new Hand(find, CardType.TwoPairs);
    }

    /**
     * 一对
     */
    private static Hand findOnePair(Profile p) {
        List<List<Card>> two = p.pointCountMap.get(2);
        if (two == null || two.size() != 1) {
            return null;
        }

        List<Card> find = new ArrayList<>();
        find.addAll(two.get(0));
        for (int i = 14; i >= 2; i--) {
            List<Card> v = p.pointMap.get(i);
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
    private static Hand findHighCard(Profile p) {
        List<Card> find = p.list.stream().filter(distinctByKey(v -> v.point)).collect(Collectors.toList());
        sort(find, true);
        return new Hand(find.size() >= 5 ? p.list.subList(0, 5) : find, CardType.HighCard);
    }

    private static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

    /**
     * 高牌比较
     */
    private static int compareHighCard(List<Card> a, List<Card> b) {
        sort(a, false);
        sort(b, false);

        int size = a.size() > b.size() ? a.size() : b.size();
        for (int i = 0; i < size; size--) {
            if (a.size() <= i) {
                return -1;
            } else if (b.size() <= i) {
                return 1;
            }
            if (a.get(i).point > b.get(i).point) {
                return 1;
            } else if (a.get(i).point < b.get(i).point) {
                return -1;
            }
        }
        return 0;
    }

    /**
     * 一对比较
     */
    private static int compreOnePair(List<Card> a, List<Card> b) {
        Profile ap = new Profile(a);
        Profile bp = new Profile(b);
        if (ap.pointCountMap.get(2).get(0).get(0).point > bp.pointCountMap.get(2).get(0).get(0).point) {
            return 1;
        } else if (ap.pointCountMap.get(2).get(0).get(0).point < bp.pointCountMap.get(2).get(0).get(0).point) {
            return -1;
        }
        return compareHighCard(a, b);
    }

    private static int compareTwoPairs(List<Card> a, List<Card> b) {
        List<Integer> al = new ArrayList<>();
        List<Integer> bl = new ArrayList<>();
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

    private static void sort(List<Card> list, boolean asc) {
        if (asc) {
            Collections.sort(list, (a, b) -> a.point.compareTo(b.point));
        } else {
            Collections.sort(list, (a, b) -> b.point.compareTo(a.point));
        }
    }

    private static void sortList(List<List<Card>> list, boolean asc) {
        if (asc) {
            Collections.sort(list, (a, b) -> a.get(0).point.compareTo(b.get(0).point));
        } else {
            Collections.sort(list, (a, b) -> b.get(0).point.compareTo(a.get(0).point));
        }
    }
}
