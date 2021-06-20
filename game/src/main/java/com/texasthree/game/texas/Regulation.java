package com.texasthree.game.texas;

/**
 * 玩法
 * <p>
 * TODO 构造成对象
 */
public enum Regulation {
    /**
     * 同时发牌
     */
    Concurrent,
    /**
     * 小盲
     */
    SmallBlind,
    /**
     * 庄家
     */
    Dealer,
    /**
     * 小盲位
     */
    SB,
    /**
     * 大盲位
     */
    BB,
    /**
     * 前注
     */
    Ante,
    /**
     * 两倍前注
     */
    DoubleAnte,
    /**
     * 强制盲注
     */
    Straddle,
    /**
     *
     */
    AllinOrFold,
    /**
     * 自动埋牌
     */
    CoverCard,
    ;


    private Regulation() {
    }
}
