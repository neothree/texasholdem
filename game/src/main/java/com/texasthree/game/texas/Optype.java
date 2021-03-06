package com.texasthree.game.texas;

/**
 * 押注类型
 */
public enum Optype {
    /**
     * 弃牌
     */
    Fold,
    /**
     * 跟注
     */
    Call,
    /**
     * 加注
     */
    Raise,
    /**
     * 全压
     */
    Allin,
    /**
     * 看牌
     */
    Check,

    /**
     * 小盲注
     */
    SmallBlind,
    /**
     * 大盲注
     */
    BigBlind,

    /**
     * 2倍大盲
     */
    BBlind2,
    /**
     * 3倍大盲
     */
    BBlind3,
    /**
     * 4倍大盲
     */
    BBlind4,
    /**
     * 1/2 底池
     */
    Pot1_2,
    /**
     * 2/3 底池
     */
    Pot2_3,
    /**
     * 1倍 底池
     */
    Pot1_1,
    /**
     * 庄家前注
     */
    DealerAnte;
}
