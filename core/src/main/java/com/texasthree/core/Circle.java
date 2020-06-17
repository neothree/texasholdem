package com.texasthree.core;

public enum Circle {
    None,
    /**
     * 底牌圈 / 前翻牌圈 - 公共牌出现以前的第一轮叫注
     */
    Preflop,
    /**
     * 翻牌圈 - 首三张公共牌出现以后的押注圈
     */
    Flop,
    /**
     * 转牌圈 - 第四张公共牌出现以后的押注圈
     */
    Turn,
    /**
     * 河牌圈 - 第五张公共牌出现以后,也即是摊牌以前的押注圈
     */
    River;
}
