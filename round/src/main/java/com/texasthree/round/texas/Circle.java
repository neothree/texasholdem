package com.texasthree.round.texas;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Circle {

    /**
     * 底牌圈 / 前翻牌圈 - 公共牌出现以前的第一轮叫注
     */
    public static final String PREFLOP = "PREFLOP";
    /**
     * 翻牌圈 - 首三张公共牌出现以后的押注圈
     */
    public static final String FLOP = "FLOP";
    /**
     * 转牌圈 - 第四张公共牌出现以后的押注圈
     */
    public static final String TURN = "TURN";
    /**
     * 河牌圈 - 第五张公共牌出现以后,也即是摊牌以前的押注圈
     */
    public static final String RIVER = "RIVER";

    static String[] values() {
        return new String[]{PREFLOP, FLOP, TURN, RIVER};
    }

    private String circle;

    private List<Card> board;

    private int potChips;

    private int playerNum;

    public List<Action> actions = new ArrayList<>();

    public Circle() {
    }

    public Circle(String circle, int potChips, int playerNum) {
        this.circle = circle;
        this.potChips = potChips;
        this.playerNum = playerNum;
    }

}
