package com.texasthree.core;

public class AllCard {

    private static AllCard instance;
    public static AllCard getInstance() {
        if (instance == null) {
            instance = new AllCard();
        }
        return instance;
    }

    // 2
    public final Card D2 = TableCard.getInstance().getCardById(21);
    public final Card C2 = TableCard.getInstance().getCardById(22);
    public final Card H2 = TableCard.getInstance().getCardById(23);
    public final Card S2 = TableCard.getInstance().getCardById(24);

    // 3
    public final Card D3 = TableCard.getInstance().getCardById(31);
    public final Card C3 = TableCard.getInstance().getCardById(32);
    public final Card H3 = TableCard.getInstance().getCardById(33);
    public final Card S3 = TableCard.getInstance().getCardById(34);

    // 4
    public final Card D4 = TableCard.getInstance().getCardById(41);
    public final Card C4 = TableCard.getInstance().getCardById(42);
    public final Card H4 = TableCard.getInstance().getCardById(43);
    public final Card S4 = TableCard.getInstance().getCardById(44);

    // 5
    public final Card D5 = TableCard.getInstance().getCardById(51);
    public final Card C5 = TableCard.getInstance().getCardById(52);
    public final Card H5 = TableCard.getInstance().getCardById(53);
    public final Card S5 = TableCard.getInstance().getCardById(54);

    // 6
    public final Card D6 = TableCard.getInstance().getCardById(61);
    public final Card C6 = TableCard.getInstance().getCardById(62);
    public final Card H6 = TableCard.getInstance().getCardById(63);
    public final Card S6 = TableCard.getInstance().getCardById(64);

    // 7
    public final Card D7 = TableCard.getInstance().getCardById(71);
    public final Card C7 = TableCard.getInstance().getCardById(72);
    public final Card H7 = TableCard.getInstance().getCardById(73);
    public final Card S7 = TableCard.getInstance().getCardById(74);

    // 8
    public final Card D8 = TableCard.getInstance().getCardById(81);
    public final Card C8 = TableCard.getInstance().getCardById(82);
    public final Card H8 = TableCard.getInstance().getCardById(83);
    public final Card S8 = TableCard.getInstance().getCardById(84);

    // 9
    public final Card D9 = TableCard.getInstance().getCardById(91);
    public final Card C9 = TableCard.getInstance().getCardById(92);
    public final Card H9 = TableCard.getInstance().getCardById(93);
    public final Card S9 = TableCard.getInstance().getCardById(94);

    // 10
    public final Card D10 = TableCard.getInstance().getCardById(101);
    public final Card C10 = TableCard.getInstance().getCardById(102);
    public final Card H10 = TableCard.getInstance().getCardById(103);
    public final Card S10 = TableCard.getInstance().getCardById(104);

    // J
    public final Card DJ = TableCard.getInstance().getCardById(111);
    public final Card CJ = TableCard.getInstance().getCardById(112);
    public final Card HJ = TableCard.getInstance().getCardById(113);
    public final Card SJ = TableCard.getInstance().getCardById(114);

    // Q
    public final Card DQ = TableCard.getInstance().getCardById(121);
    public final Card CQ = TableCard.getInstance().getCardById(122);
    public final Card HQ = TableCard.getInstance().getCardById(123);
    public final Card SQ = TableCard.getInstance().getCardById(124);

    // K
    public final Card DK = TableCard.getInstance().getCardById(131);
    public final Card CK = TableCard.getInstance().getCardById(132);
    public final Card HK = TableCard.getInstance().getCardById(133);
    public final Card SK = TableCard.getInstance().getCardById(134);


    // A
    public final Card DA = TableCard.getInstance().getCardById(141);
    public final Card CA = TableCard.getInstance().getCardById(142);
    public final Card HA = TableCard.getInstance().getCardById(143);
    public final Card SA = TableCard.getInstance().getCardById(144);


    private AllCard() {}

}
