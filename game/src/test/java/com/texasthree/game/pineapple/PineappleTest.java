package com.texasthree.game.pineapple;

import com.texasthree.game.AllCard;
import com.texasthree.game.texas.Card;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


class PineappleTest extends AllCard {

    @Test
    public void testOther() throws Exception {
        var game = Pineapple.builder()
                .playerCards(0,
                        diamondA, diamond2, diamond3, diamond4, diamond5,
                        diamond6, diamond7, club7,
                        diamond8, diamond9, club9,
                        diamond10, heart10, diamondJ,
                        clubJ, spadesJ, diamondK)
                .playerCards(1, heartA, heart2, heart3, spadesA, spades2,
                        spades3, spades4, spades5,
                        spades6, spades7, heart4,
                        heart5, heart8, heart9,
                        spadesK, spadesQ, heart6)
                .build();
        game.start();

        // 第1圈
        assertEquals(1, game.opPlayer());
        assertEquals(5, game.giveCardNum());
        assertEquals(5, game.chooseCardNum());
        var move = game.action(1, makeRowCards(null, 2, heartA, heart2, heart3, spadesA, spades2));
        assertEquals(Pineapple.NEXT_OP, move);
        move = game.action(0, makeRowCards(null, 2, diamondA, diamond2, diamond3, diamond4, diamond5));
        assertEquals(Pineapple.CIRCLE_END, move);

        // 第2圈
        assertEquals(3, game.giveCardNum());
        assertEquals(2, game.chooseCardNum());
        move = game.action(1, makeRowCards(null, 1, spades3, spades4));
        assertEquals(Pineapple.NEXT_OP, move);
        move = game.action(0, makeRowCards(null, 1, diamond6, diamond7));
        assertEquals(Pineapple.CIRCLE_END, move);

        // 第3圈
        assertEquals(3, game.giveCardNum());
        assertEquals(2, game.chooseCardNum());
        move = game.action(1, makeRowCards(null, 1, spades6, spades7));
        assertEquals(Pineapple.NEXT_OP, move);
        move = game.action(0, makeRowCards(null, 1, diamond8, diamond9));
        assertEquals(Pineapple.CIRCLE_END, move);

        // 第4圈
        assertEquals(3, game.giveCardNum());
        assertEquals(2, game.chooseCardNum());
        var acts = makeRowCards(null, 0, heart8);
        move = game.action(1, makeRowCards(acts, 1, heart5));
        assertEquals(Pineapple.NEXT_OP, move);

        acts = makeRowCards(null, 0, diamondJ);
        move = game.action(0, makeRowCards(acts, 1, diamond10));
        assertEquals(Pineapple.CIRCLE_END, move);

        // 第5圈
        assertEquals(3, game.giveCardNum());
        assertEquals(2, game.chooseCardNum());
        move = game.action(1, makeRowCards(null, 0, spadesQ, spadesK));
        assertEquals(Pineapple.NEXT_OP, move);
        move = game.action(0, makeRowCards(null, 0, clubJ, spadesJ));
        assertEquals(Pineapple.SHOWDOWN, move);

    }

    List<RowCard> makeRowCards(List<RowCard> src, int row, Card... cards) {
        var ret = src != null ? src : new ArrayList<RowCard>();
        for (var v : cards) {
            ret.add(new RowCard(v, row, false));
        }
        return ret;
    }

    @Test
    public void testConcurrent() throws Exception {
        var game = Pineapple.builder()
                .playerNum(3)
                .dealer(0)
                .concurrent()
                .playerCards(0,
                        diamond6, diamond7, diamond8, diamond9, diamond10,
                        diamondA, diamond2, club7,
                        diamond3, diamond4, club9,
                        diamond5, heart10, diamondJ,
                        clubJ, spadesJ, diamondK)
                .playerCards(1,
                        heartA, heart2, clubA, spadesA, spades2,
                        spades3, spades4, spades5,
                        spades6, spades7, heart4,
                        heart5, heart8, heart9,
                        spadesK, spadesQ, heart6)
                .playerCards(2,
                        heart3, club2, club3, club4, club5,
                        spades10, heartJ, heartQ,
                        spades9, spades8, heartK,
                        heart7, diamondQ, club6,
                        club8, club10, clubQ)
                .build();
        game.start();

        // 玩家 0 押注
        assertEquals(1, game.opPlayer());
        equalsCards(game.getWaits(0), diamond6, diamond7, diamond8, diamond9, diamond10);
        equalsCards(game.getWaits(1), heartA, heart2, clubA, spadesA, spades2);
        equalsCards(game.getWaits(2), heart3, club2, club3, club4, club5);
        var move = game.action(0, makeRowCards(null, 2, diamond6, diamond7, diamond8, diamond9, diamond10));
        assertNull(move);

        // 玩家 2 押注
        assertEquals(1, game.opPlayer());
        equalsCards(game.getWaits(0));
        equalsCards(game.getWaits(1), heartA, heart2, clubA, spadesA, spades2);
        equalsCards(game.getWaits(2), heart3, club2, club3, club4, club5);
        move = game.action(2, makeRowCards(null, 1, heart3, club2, club3, club4, club5));
        assertNull(move);

        // 玩家 1 押注
        assertEquals(1, game.opPlayer());
        equalsCards(game.getWaits(0));
        equalsCards(game.getWaits(1), heartA, heart2, clubA, spadesA, spades2);
        equalsCards(game.getWaits(2));
        move = game.action(1, makeRowCards(null, 2, heartA, heart2, clubA, spadesA, spades2));
        assertEquals(Pineapple.CONTINUE, move);

        // CONTINUE
        equalsCards(game.getWaits(0));
        equalsCards(game.getWaits(1));
        equalsCards(game.getWaits(2));
        move = game.doContinue();
        assertEquals(Pineapple.CONTINUE, move);
        move = game.doContinue();
        assertEquals(Pineapple.CIRCLE_END, move);

        equalsCards(game.getWaits(0), diamondA, diamond2, club7);
        equalsCards(game.getWaits(1), spades3, spades4, spades5);
        equalsCards(game.getWaits(2), spades10, heartJ, heartQ);
    }

    @Test
    public void testFoldsAndRowCardsAndWaits() throws Exception {
        var game = Pineapple.builder()
                .playerNum(3)
                .dealer(2)
                .playerCards(0,
                        diamond6, diamond7, diamond8, diamond9, diamond10,
                        diamondA, diamond2, club7,
                        diamond3, diamond4, club9,
                        diamond5, heart10, diamondJ,
                        clubJ, spadesJ, diamondK)
                .playerCards(1,
                        heartA, heart2, clubA, spadesA, spades2,
                        spades3, spades4, spades5,
                        spades6, spades7, heart4,
                        heart5, heart8, heart9,
                        spadesK, spadesQ, heart6)
                .playerCards(2,
                        heart3, club2, club3, club4, club5,
                        spades10, heartJ, heartQ,
                        spades9, spades8, heartK,
                        heart7, diamondQ, club6,
                        club8, club10, clubQ)
                .build();

        // 开始
        game.start();
        equalsCards(game.getFolds(0));
        equalsCards(game.getFolds(1));
        equalsCards(game.getFolds(2));

        equalsCards(game.getRowCards(0, 0));
        equalsCards(game.getRowCards(0, 1));
        equalsCards(game.getRowCards(0, 2));
        equalsCards(game.getRowCards(1, 0));
        equalsCards(game.getRowCards(1, 1));
        equalsCards(game.getRowCards(1, 2));
        equalsCards(game.getRowCards(2, 0));
        equalsCards(game.getRowCards(2, 1));
        equalsCards(game.getRowCards(2, 2));

        // 第1圈
        equalsCards(game.getWaits(0), diamond6, diamond7, diamond8, diamond9, diamond10);

        var waits = game.getWaits(0); // 顺序
        assertEquals(diamond6, waits.get(0));
        assertEquals(diamond7, waits.get(1));
        assertEquals(diamond8, waits.get(2));
        assertEquals(diamond9, waits.get(3));
        assertEquals(diamond10, waits.get(4));

        equalsCards(game.getWaits(1));
        equalsCards(game.getWaits(2));
        game.action(0, makeRowCards(null, 2, diamond6, diamond7, diamond8, diamond9, diamond10));

        equalsCards(game.getWaits(0));
        equalsCards(game.getWaits(1), heartA, heart2, clubA, spadesA, spades2);
        equalsCards(game.getWaits(2));
        game.action(1, makeRowCards(null, 2, heartA, heart2, clubA, spadesA, spades2));

        equalsCards(game.getWaits(0));
        equalsCards(game.getWaits(1));
        equalsCards(game.getWaits(2), heart3, club2, club3, club4, club5);
        game.action(2, makeRowCards(null, 1, heart3, club2, club3, club4, club5));

        equalsCards(game.getFolds(0));
        equalsCards(game.getFolds(1));
        equalsCards(game.getFolds(2));

        equalsCards(game.getRowCards(0, 0));
        equalsCards(game.getRowCards(0, 1));
        equalsCards(game.getRowCards(0, 2), diamond6, diamond7, diamond8, diamond9, diamond10);
        equalsCards(game.getRowCards(1, 0));
        equalsCards(game.getRowCards(1, 1));
        equalsCards(game.getRowCards(1, 2), heartA, heart2, clubA, spadesA, spades2);
        equalsCards(game.getRowCards(2, 0));
        equalsCards(game.getRowCards(2, 1), heart3, club2, club3, club4, club5);
        equalsCards(game.getRowCards(2, 2));

        // 第2圈
        equalsCards(game.getWaits(0), diamondA, diamond2, club7);
        waits = game.getWaits(0); // 顺序
        assertEquals(diamond2, waits.get(0));
        assertEquals(club7, waits.get(1));
        assertEquals(diamondA, waits.get(2));

        equalsCards(game.getWaits(1));
        equalsCards(game.getWaits(2));
        game.action(0, makeRowCards(null, 1, diamondA, diamond2));

        equalsCards(game.getWaits(0));
        equalsCards(game.getWaits(1), spades3, spades4, spades5);
        equalsCards(game.getWaits(2));
        game.action(1, makeRowCards(null, 1, spades3, spades4));

        equalsCards(game.getWaits(0));
        equalsCards(game.getWaits(1));
        equalsCards(game.getWaits(2), spades10, heartJ, heartQ);
        game.action(2, makeRowCards(null, 2, spades10, heartJ));

        equalsCards(game.getFolds(0), club7);
        equalsCards(game.getFolds(1), spades5);
        equalsCards(game.getFolds(2), heartQ);

        equalsCards(game.getRowCards(0, 0));
        equalsCards(game.getRowCards(0, 1), diamondA, diamond2);
        equalsCards(game.getRowCards(0, 2), diamond6, diamond7, diamond8, diamond9, diamond10);
        equalsCards(game.getRowCards(1, 0));
        equalsCards(game.getRowCards(1, 1), spades3, spades4);
        equalsCards(game.getRowCards(1, 2), heartA, heart2, clubA, spadesA, spades2);
        equalsCards(game.getRowCards(2, 0));
        equalsCards(game.getRowCards(2, 1), heart3, club2, club3, club4, club5);
        equalsCards(game.getRowCards(2, 2), spades10, heartJ);

        // 第3圈
        equalsCards(game.getWaits(0), diamond3, diamond4, club9);
        waits = game.getWaits(0); // 顺序
        assertEquals(diamond3, waits.get(0));
        assertEquals(diamond4, waits.get(1));
        assertEquals(club9, waits.get(2));

        equalsCards(game.getWaits(1));
        equalsCards(game.getWaits(2));
        game.action(0, makeRowCards(null, 1, diamond3, diamond4));

        equalsCards(game.getWaits(0));
        equalsCards(game.getWaits(1), spades6, spades7, heart4);
        equalsCards(game.getWaits(2));
        game.action(1, makeRowCards(null, 1, spades6, spades7));

        equalsCards(game.getWaits(0));
        equalsCards(game.getWaits(1));
        equalsCards(game.getWaits(2), spades9, heartK, spades8);
        game.action(2, makeRowCards(null, 2, spades9, heartK));

        equalsCards(game.getFolds(0), club7, club9);
        equalsCards(game.getFolds(1), spades5, heart4);
        equalsCards(game.getFolds(2), heartQ, spades8);

        equalsCards(game.getRowCards(0, 0));
        equalsCards(game.getRowCards(0, 1), diamondA, diamond2, diamond3, diamond4);
        equalsCards(game.getRowCards(0, 2), diamond6, diamond7, diamond8, diamond9, diamond10);
        equalsCards(game.getRowCards(1, 0));
        equalsCards(game.getRowCards(1, 1), spades3, spades4, spades6, spades7);
        equalsCards(game.getRowCards(1, 2), heartA, heart2, clubA, spadesA, spades2);
        equalsCards(game.getRowCards(2, 0));
        equalsCards(game.getRowCards(2, 1), heart3, club2, club3, club4, club5);
        equalsCards(game.getRowCards(2, 2), spades10, heartJ, spades9, heartK);

        // 第4圈
        equalsCards(game.getWaits(0), diamondJ, diamond5, heart10);
        waits = game.getWaits(0); // 顺序
        assertEquals(diamond5, waits.get(0));
        assertEquals(heart10, waits.get(1));
        assertEquals(diamondJ, waits.get(2));
        equalsCards(game.getWaits(1));
        equalsCards(game.getWaits(2));
        game.action(0, makeRowCards(makeRowCards(null, 0, diamondJ), 1, diamond5));

        equalsCards(game.getWaits(0));
        equalsCards(game.getWaits(1), heart8, heart5, heart9);
        equalsCards(game.getWaits(2));
        game.action(1, makeRowCards(makeRowCards(null, 0, heart8), 1, heart5));

        equalsCards(game.getWaits(0));
        equalsCards(game.getWaits(1));
        equalsCards(game.getWaits(2), heart7, diamondQ, club6);
        game.action(2, makeRowCards(makeRowCards(null, 2, heart7), 0, diamondQ));

        equalsCards(game.getFolds(0), club7, club9, heart10);
        equalsCards(game.getFolds(1), spades5, heart4, heart9);
        equalsCards(game.getFolds(2), heartQ, spades8, club6);

        equalsCards(game.getRowCards(0, 0), diamondJ);
        equalsCards(game.getRowCards(0, 1), diamondA, diamond2, diamond3, diamond4, diamond5);
        equalsCards(game.getRowCards(0, 2), diamond6, diamond7, diamond8, diamond9, diamond10);
        equalsCards(game.getRowCards(1, 0), heart8);
        equalsCards(game.getRowCards(1, 1), spades3, spades4, spades6, spades7, heart5);
        equalsCards(game.getRowCards(1, 2), heartA, heart2, clubA, spadesA, spades2);
        equalsCards(game.getRowCards(2, 0), diamondQ);
        equalsCards(game.getRowCards(2, 1), heart3, club2, club3, club4, club5);
        equalsCards(game.getRowCards(2, 2), spades10, heartJ, spades9, heartK, heart7);

        // 第5圈
        equalsCards(game.getWaits(0), clubJ, spadesJ, diamondK);
        waits = game.getWaits(0); // 顺序
        assertTrue(clubJ.equals(waits.get(0)) || spadesJ.equals(waits.get(0)));
        assertTrue(clubJ.equals(waits.get(1)) || spadesJ.equals(waits.get(1)));
        assertEquals(diamondK, waits.get(2));
        equalsCards(game.getWaits(1));
        equalsCards(game.getWaits(2));
        game.action(0, makeRowCards(null, 0, clubJ, spadesJ));

        equalsCards(game.getWaits(0));
        equalsCards(game.getWaits(1), spadesQ, spadesK, heart6);
        equalsCards(game.getWaits(2));
        game.action(1, makeRowCards(null, 0, spadesQ, spadesK));

        equalsCards(game.getWaits(0));
        equalsCards(game.getWaits(1));
        equalsCards(game.getWaits(2), club8, club10, clubQ);
        game.action(2, makeRowCards(null, 0, club8, club10));

        equalsCards(game.getFolds(0), club7, club9, heart10, diamondK);
        equalsCards(game.getFolds(1), spades5, heart4, heart9, heart6);
        equalsCards(game.getFolds(2), heartQ, spades8, club6, clubQ);

        equalsCards(game.getRowCards(0, 0), diamondJ, clubJ, spadesJ);
        equalsCards(game.getRowCards(0, 1), diamondA, diamond2, diamond3, diamond4, diamond5);
        equalsCards(game.getRowCards(0, 2), diamond6, diamond7, diamond8, diamond9, diamond10);
        equalsCards(game.getRowCards(1, 0), heart8, spadesQ, spadesK);
        equalsCards(game.getRowCards(1, 1), spades3, spades4, spades6, spades7, heart5);
        equalsCards(game.getRowCards(1, 2), heartA, heart2, clubA, spadesA, spades2);
        equalsCards(game.getRowCards(2, 0), diamondQ, club8, club10);
        equalsCards(game.getRowCards(2, 1), heart3, club2, club3, club4, club5);
        equalsCards(game.getRowCards(2, 2), spades10, heartJ, spades9, heartK, heart7);
    }

    void equalsCards(List<Card> cards, Card... expect) {
        var set = new HashSet<>(cards);
        assertEquals(set.size(), expect.length);
        if (!set.isEmpty() && expect.length != 0) {
            assertTrue(Arrays.stream(expect).anyMatch(v -> set.contains(v)));
        }
    }

    @Test
    public void testFantasy() throws Exception {
        var game = Pineapple.builder()
                .playerNum(2)
                .dealer(0)
                .fantasy(0)
                .playerCards(0,
                        diamond6, diamond7, diamond8, diamond9, diamond10,
                        diamondA, diamond2, club7,
                        diamond3, diamond4, club9,
                        diamond5, heart10, diamondJ,
                        clubJ, spadesJ, diamondK)
                .playerCards(1,
                        heartA, heart2, clubA, spadesA, spades2,
                        spades3, spades4, spades5,
                        spades6, spades7, heart4,
                        heart5, heart8, heart9,
                        spadesK, spadesQ, heart6)
                .build();

        // 开始
        game.start();

        var f = game.getFantasy();
        assertEquals(1, f.size());
        assertTrue(f.contains(0));
        assertEquals(Pineapple.FANTASY_CARD_NUM, game.chooseCardNum(0));

        // 第1圈
        var move = game.action(1, makeRowCards(null, 2, heartA, heart2, clubA, spadesA, spades2));
        assertEquals(Pineapple.CIRCLE_END, move);

        // 第2圈
        assertEquals(1, game.opPlayer());
        move = game.action(1, makeRowCards(null, 1, spades3, spades4));
        assertEquals(Pineapple.CIRCLE_END, move);

        // 第3圈
        assertEquals(1, game.opPlayer());
        move = game.action(1, makeRowCards(null, 1, spades6, spades7));
        assertEquals(Pineapple.CIRCLE_END, move);

        // 第4圈
        assertEquals(1, game.opPlayer());
        move = game.action(1, makeRowCards(makeRowCards(null, 0, heart8), 1, heart5));
        assertEquals(Pineapple.CIRCLE_END, move);

        // 第5圈
        assertEquals(1, game.opPlayer());
        move = game.action(1, makeRowCards(null, 0, spadesQ, spadesK));
        assertEquals(Pineapple.NEXT_OP, move);

        assertEquals(0, game.opPlayer());
        assertEquals(Pineapple.FANTASY_CARD_NUM, game.getWaits(0).size());

        var act = makeRowCards(null, 0, club7, club9, heart10);
        act = makeRowCards(act, 1, diamondA, diamond2, diamond3, diamond4, diamond5);
        act = makeRowCards(act, 2, diamond6, diamond7, diamond8, diamond9, diamond10);
        move = game.action(0, act);
        assertEquals(Pineapple.SHOWDOWN, move);


        // 庄家不是 fantasy
        game = Pineapple.builder()
                .playerNum(2)
                .dealer(0)
                .fantasy(1)
                .playerCards(0,
                        diamond6, diamond7, diamond8, diamond9, diamond10,
                        diamondA, diamond2, club7,
                        diamond3, diamond4, club9,
                        diamond5, heart10, diamondJ,
                        clubJ, spadesJ, diamondK)
                .playerCards(1,
                        heartA, heart2, clubA, spadesA, spades2,
                        spades3, spades4, spades5,
                        spades6, spades7, heart4,
                        heart5, heart8, heart9,
                        spadesK, spadesQ, heart6)
                .build();

        // 开始
        game.start();

        f = game.getFantasy();
        assertEquals(1, f.size());
        assertTrue(f.contains(1));
        assertEquals(Pineapple.FANTASY_CARD_NUM, game.chooseCardNum(1));

        // 第1圈
        move = game.action(0, makeRowCards(null, 2, diamond6, diamond7, diamond8, diamond9, diamond10));
        assertEquals(Pineapple.CIRCLE_END, move);

        // 第2圈
        assertEquals(0, game.opPlayer());
        move = game.action(0, makeRowCards(null, 1, diamondA, diamond2));
        assertEquals(Pineapple.CIRCLE_END, move);

        // 第3圈
        assertEquals(0, game.opPlayer());
        move = game.action(0, makeRowCards(null, 1, diamond3, diamond4));
        assertEquals(Pineapple.CIRCLE_END, move);

        // 第4圈
        assertEquals(0, game.opPlayer());
        move = game.action(0, makeRowCards(makeRowCards(null, 0, diamondJ), 1, diamond5));
        assertEquals(Pineapple.CIRCLE_END, move);

        // 第5圈
        assertEquals(0, game.opPlayer());
        move = game.action(0, makeRowCards(null, 0, clubJ, spadesJ));
        assertEquals(Pineapple.NEXT_OP, move);

        assertEquals(1, game.opPlayer());
        assertEquals(Pineapple.FANTASY_CARD_NUM, game.getWaits(1).size());

        act = makeRowCards(null, 0, heart9, heart8, spades5);
        act = makeRowCards(act, 1, spades3, spades4, spades6, spades7, heart5);
        act = makeRowCards(act, 2, heartA, heart2, clubA, spadesA, spades2);
        move = game.action(1, act);
        assertEquals(Pineapple.SHOWDOWN, move);

        // 三人
        game = Pineapple.builder()
                .playerNum(3)
                .fantasy(1)
                .dealer(2)
                .playerCards(0,
                        diamond6, diamond7, diamond8, diamond9, diamond10,
                        diamondA, diamond2, club7,
                        diamond3, diamond4, club9,
                        diamond5, heart10, diamondJ,
                        clubJ, spadesJ, diamondK)
                .playerCards(1,
                        heartA, heart2, clubA, spadesA, spades2,
                        spades3, spades4, spades5,
                        spades6, spades7, heart4,
                        heart5, heart8, heart9,
                        spadesK, spadesQ, heart6)
                .playerCards(2,
                        heart3, club2, club3, club4, club5,
                        spades10, heartJ, heartQ,
                        spades9, spades8, heartK,
                        heart7, diamondQ, club6,
                        club8, club10, clubQ)
                .build();

        // 开始
        game.start();

        // 第1圈
        move = game.action(0, makeRowCards(null, 2, diamond6, diamond7, diamond8, diamond9, diamond10));
        assertEquals(Pineapple.NEXT_OP, move);
        move = game.action(2, makeRowCards(null, 1, heart3, club2, club3, club4, club5));
        assertEquals(Pineapple.CIRCLE_END, move);

        // 第2圈
        move = game.action(0, makeRowCards(null, 1, diamondA, diamond2));
        assertEquals(Pineapple.NEXT_OP, move);
        move = game.action(2, makeRowCards(null, 2, spades10, heartJ));
        assertEquals(Pineapple.CIRCLE_END, move);

        // 第3圈
        move = game.action(0, makeRowCards(null, 1, diamond3, diamond4));
        assertEquals(Pineapple.NEXT_OP, move);
        move = game.action(2, makeRowCards(null, 2, spades9, heartK));
        assertEquals(Pineapple.CIRCLE_END, move);

        // 第4圈
        move = game.action(0, makeRowCards(makeRowCards(null, 0, diamondJ), 1, diamond5));
        assertEquals(Pineapple.NEXT_OP, move);
        move = game.action(2, makeRowCards(makeRowCards(null, 0, heart7), 2, diamondQ));
        assertEquals(Pineapple.CIRCLE_END, move);

        // 第5圈
        move = game.action(0, makeRowCards(null, 0, clubJ, spadesJ));
        assertEquals(Pineapple.NEXT_OP, move);
        move = game.action(2, makeRowCards(null, 0, club10, clubQ));
        assertEquals(Pineapple.NEXT_OP, move);

        assertEquals(1, game.opPlayer());
        assertEquals(Pineapple.FANTASY_CARD_NUM, game.getWaits(1).size());

        act = makeRowCards(null, 0, heart9, heart8, spades5);
        act = makeRowCards(act, 1, spades3, spades4, spades6, spades7, heart5);
        act = makeRowCards(act, 2, heartA, heart2, clubA, spadesA, spades2);
        move = game.action(1, act);
        assertEquals(Pineapple.SHOWDOWN, move);

        // 两人范特西
        game = Pineapple.builder()
                .playerNum(2)
                .fantasy(0)
                .fantasy(1)
                .dealer(0)
                .playerCards(0,
                        diamond6, diamond7, diamond8, diamond9, diamond10,
                        diamondA, diamond2, club7,
                        diamond3, diamond4, club9,
                        diamond5, heart10, diamondJ,
                        clubJ, spadesJ, diamondK)
                .playerCards(1,
                        heartA, heart2, clubA, spadesA, spades2,
                        spades3, spades4, spades5,
                        spades6, spades7, heart4,
                        heart5, heart8, heart9,
                        spadesK, spadesQ, heart6)
                .build();

        // 开始
        game.start();

        f = game.getFantasy();
        assertEquals(2, f.size());
        assertTrue(f.contains(0));
        assertTrue(f.contains(1));
        assertEquals(Pineapple.FANTASY_CARD_NUM, game.chooseCardNum(0));
        assertEquals(Pineapple.FANTASY_CARD_NUM, game.chooseCardNum(1));

        assertEquals(1, game.opPlayer());
        act = makeRowCards(null, 0, heart9, heart8, spades5);
        act = makeRowCards(act, 1, spades3, spades4, spades6, spades7, heart5);
        act = makeRowCards(act, 2, heartA, heart2, clubA, spadesA, spades2);
        move = game.action(1, act);
        assertEquals(Pineapple.NEXT_OP, move);

        act = makeRowCards(null, 0, club7, club9, heart10);
        act = makeRowCards(act, 1, diamondA, diamond2, diamond3, diamond4, diamond5);
        act = makeRowCards(act, 2, diamond6, diamond7, diamond8, diamond9, diamond10);
        move = game.action(0, act);
        assertEquals(Pineapple.SHOWDOWN, move);
    }
}