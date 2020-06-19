package com.texasthree.round.texas;

public enum Law {
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
    Straddle;



    private Law() {
    }
}
