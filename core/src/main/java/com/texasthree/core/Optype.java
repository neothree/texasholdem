package com.texasthree.core;

/**
 * 押注类型
 */
public enum Optype {
    None,
    Fold,
    Call,
    Raise,
    Allin,
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
