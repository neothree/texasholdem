package com.texasthree.game.texas;

/**
 * 押注类型
 */
public enum Optype {
    /**
     * 弃牌
     */
    Fold(true),
    /**
     * 跟注
     */
    Call(true),
    /**
     * 加注
     */
    Raise(true),
    /**
     * 全压
     */
    Allin(true),
    /**
     * 看牌
     */
    Check(true),

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

    public final boolean primitive;

    Optype(boolean primitive) {
        this.primitive = primitive;
    }

    Optype() {
        this(false);
    }
}
