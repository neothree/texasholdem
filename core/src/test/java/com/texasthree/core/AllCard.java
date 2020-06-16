package com.texasthree.core;

public class AllCard {

    private static AllCard instance;
    public static AllCard getInstance() {
        if (instance == null) {
            instance = new AllCard();
        }
        return instance;
    }

    // 黑桃
    public final Card spades2 = TableCard.getInstance().getCardById(24);
    public final Card spades3 = TableCard.getInstance().getCardById(34);
    public final Card spades4 = TableCard.getInstance().getCardById(44);
    public final Card spades5 = TableCard.getInstance().getCardById(54);
    public final Card spades6 = TableCard.getInstance().getCardById(64);
    public final Card spades7 = TableCard.getInstance().getCardById(74);
    public final Card spades8 = TableCard.getInstance().getCardById(84);
    public final Card spades9 = TableCard.getInstance().getCardById(94);
    public final Card spades10 = TableCard.getInstance().getCardById(104);
    public final Card spadesJ = TableCard.getInstance().getCardById(114);
    public final Card spadesQ = TableCard.getInstance().getCardById(124);
    public final Card spadesK = TableCard.getInstance().getCardById(134);
    public final Card spadesA = TableCard.getInstance().getCardById(144);

    // 红桃
    public final Card heart2 = TableCard.getInstance().getCardById(23);
    public final Card heart3 = TableCard.getInstance().getCardById(33);
    public final Card heart4 = TableCard.getInstance().getCardById(43);
    public final Card heart5 = TableCard.getInstance().getCardById(53);
    public final Card heart6 = TableCard.getInstance().getCardById(63);
    public final Card heart7 = TableCard.getInstance().getCardById(73);
    public final Card heart8 = TableCard.getInstance().getCardById(83);
    public final Card heart9 = TableCard.getInstance().getCardById(93);
    public final Card heart10 = TableCard.getInstance().getCardById(103);
    public final Card heartJ = TableCard.getInstance().getCardById(113);
    public final Card heartQ = TableCard.getInstance().getCardById(123);
    public final Card heartK = TableCard.getInstance().getCardById(133);
    public final Card heartA = TableCard.getInstance().getCardById(143);

    // 梅花
    public final Card club2 = TableCard.getInstance().getCardById(22);
    public final Card club3 = TableCard.getInstance().getCardById(32);
    public final Card club4 = TableCard.getInstance().getCardById(42);
    public final Card club5 = TableCard.getInstance().getCardById(52);
    public final Card club6 = TableCard.getInstance().getCardById(62);
    public final Card club7 = TableCard.getInstance().getCardById(72);
    public final Card club8 = TableCard.getInstance().getCardById(82);
    public final Card club9 = TableCard.getInstance().getCardById(92);
    public final Card club10 = TableCard.getInstance().getCardById(102);
    public final Card clubJ = TableCard.getInstance().getCardById(112);
    public final Card clubQ = TableCard.getInstance().getCardById(122);
    public final Card clubK = TableCard.getInstance().getCardById(132);
    public final Card clubA = TableCard.getInstance().getCardById(142);

    // 方块
    public final Card diamond2 = TableCard.getInstance().getCardById(21);
    public final Card diamond3 = TableCard.getInstance().getCardById(31);
    public final Card diamond4 = TableCard.getInstance().getCardById(41);
    public final Card diamond5 = TableCard.getInstance().getCardById(51);
    public final Card diamond6 = TableCard.getInstance().getCardById(61);
    public final Card diamond7 = TableCard.getInstance().getCardById(71);
    public final Card diamond8 = TableCard.getInstance().getCardById(81);
    public final Card diamond9 = TableCard.getInstance().getCardById(91);
    public final Card diamond10 = TableCard.getInstance().getCardById(101);
    public final Card diamondJ = TableCard.getInstance().getCardById(111);
    public final Card diamondQ = TableCard.getInstance().getCardById(121);
    public final Card diamondK = TableCard.getInstance().getCardById(131);
    public final Card diamondA = TableCard.getInstance().getCardById(141);


    private AllCard() {}

}
