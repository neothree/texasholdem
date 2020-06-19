package com.texasthree.core.texas;

public enum CardType {

    Min(1101),
    /**
     * 单牌(高牌)
     */
    HighCard(1101),
    /**
     * 一对
     */
    OnePair(1102),
    /**
     * 两对
     */
    TwoPairs(1103),
    /**
     * 三条
     */
    ThreeOfKind(1104),
    /**
     * 顺子
     */
    Straight(1105),
    /**
     * 同花
     */
    Flush(1106),
    /**
     * 三条加一对(葫芦)
     */
    FullHouse(1107),
    /**
     * 四条(金刚)
     */
    FourOfKind(1108),
    /**
     * 同花顺
     */
    StraightFlush(1109),
    /**
     * 皇家同花顺
     */
    RoyalFlush(1110),
    /**
     * 单牌(高牌)
     */
    Max(1110);

    private Integer weight;

    private CardType(Integer weight) {
        this.weight = weight;
    }

    public Integer getWeight() {
        return weight;
    }
}
