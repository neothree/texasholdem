package com.texasthree.zone.round;


/**
 * 牌局事件监听
 *
 * @author: neo
 * @create: 2022-08-09 16:50
 */
interface TexasEventListener {
    /**
     * 开始牌局
     */
    void onStartGame(TexasEvent event);

    /**
     * 手牌更新
     */
    void onUpdateHand(TexasEvent event);

    /**
     * 新的操作人
     */
    void onOperator(TexasEvent event);

    /**
     * 押注
     */
    void onAction(TexasEvent event);

    /**
     * 一圈结束
     */
    void onCircleEnd(TexasEvent event);

    /**
     * 亮牌
     */
    void onShowdown(TexasEvent event);

    /**
     * 保险开始
     */
    void onInsurance(TexasEvent event);

    /**
     * 新的购买人
     */
    void onBuyer(TexasEvent event);

    /**
     * 购买保险结束
     */
    void onBuyEnd(TexasEvent event);
}
