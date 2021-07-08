package com.texasthree.game;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: neo
 * @create: 2021-07-08 17:50
 */
public class Utils {
    public static List<List<Object>> zip(List list, int size) {
        if (list.size() < size || size <= 1) {
            throw new IllegalArgumentException();
        }

        var ret = new ArrayList<List<Object>>();
        var take = list.get(0);
        var sublist = list.subList(1, list.size());
        var find = zip(sublist, size - 1);
        for (var v : find) {
            v.add(take);
            ret.add(v);
        }
        ret.addAll(zip(sublist, size));
        return ret;
    }

}
