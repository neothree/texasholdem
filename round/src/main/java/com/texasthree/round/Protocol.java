package com.texasthree.round;

import java.util.HashMap;
import java.util.Map;

public class Protocol {

    private static Map<String, Round> all = new HashMap<>();

    /**
     * 创建
     */
    public static RoundState create(String id) {
        Round round = new Round(id);
        all.put(id, round);

        round.start();
        return round.getData();
    }

    /**
     * 销魂
     */
    public static void destroy(String id) {
        all.remove(id);
    }

    /**
     * 获取牌局数据
     */
    public static RoundState getData(String id) {
        Round round = all.get(id);
        if (round != null) {
            return null;
        }

        return round.getData();
    }

    /**
     * 押注
     */
    public static RoundState action(String id) {
        Round round = all.get(id);
        if (round != null) {
            return null;
        }

        round.action();
        return round.getData();
    }


    /**
     * 结算
     */
    public static SettleInfo settle(String id) {
        Round round = all.get(id);
        if (round != null) {
            return null;
        }

        return round.settle();
    }
}
