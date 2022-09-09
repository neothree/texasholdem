package com.texasthree.zone.room;

/**
 * 牌局事件
 *
 * @author: neo
 * @create: 2022-08-09 16:42
 */
public enum RoundEvent {

    /**
     * 开始牌局
     */
    START_GAME,
    /**
     * 手牌更新
     */
    HAND,
    /**
     * 新的操作人
     */
    OPERATOR,
    /**
     * 押注
     */
    ACTION,
    /**
     * 一圈结束
     */
    CIRCLE_END,
    /**
     * 亮牌
     */
    SHOWDOWN,
}
