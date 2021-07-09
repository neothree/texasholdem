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
        var state = game.action(1, makeRowCards(RowCard.ROW_TAIL, heartA, heart2, heart3, spadesA, spades2));
        assertEquals(Pineapple.STATE_NEXT_OP, state);
        state = game.action(0, makeRowCards(RowCard.ROW_TAIL, diamondA, diamond2, diamond3, diamond4, diamond5));
        assertEquals(Pineapple.STATE_CIRCLE_END, state);

        // 第2圈
        assertEquals(3, game.giveCardNum());
        assertEquals(2, game.chooseCardNum());
        state = game.action(1, makeRowCards(RowCard.ROW_MIDDLE, spades3, spades4));
        assertEquals(Pineapple.STATE_NEXT_OP, state);
        state = game.action(0, makeRowCards(RowCard.ROW_MIDDLE, diamond6, diamond7));
        assertEquals(Pineapple.STATE_CIRCLE_END, state);

        // 第3圈
        assertEquals(3, game.giveCardNum());
        assertEquals(2, game.chooseCardNum());
        state = game.action(1, makeRowCards(RowCard.ROW_MIDDLE, spades6, spades7));
        assertEquals(Pineapple.STATE_NEXT_OP, state);
        state = game.action(0, makeRowCards(RowCard.ROW_MIDDLE, diamond8, diamond9));
        assertEquals(Pineapple.STATE_CIRCLE_END, state);

        // 第4圈
        assertEquals(3, game.giveCardNum());
        assertEquals(2, game.chooseCardNum());
        var acts = makeRowCards(RowCard.ROW_HEAD, heart8);
        state = game.action(1, makeRowCards(acts, RowCard.ROW_MIDDLE, heart5));
        assertEquals(Pineapple.STATE_NEXT_OP, state);

        acts = makeRowCards(RowCard.ROW_HEAD, diamondJ);
        state = game.action(0, makeRowCards(acts, RowCard.ROW_MIDDLE, diamond10));
        assertEquals(Pineapple.STATE_CIRCLE_END, state);

        // 第5圈
        assertEquals(3, game.giveCardNum());
        assertEquals(2, game.chooseCardNum());
        state = game.action(1, makeRowCards(RowCard.ROW_HEAD, spadesQ, spadesK));
        assertEquals(Pineapple.STATE_NEXT_OP, state);
        state = game.action(0, makeRowCards(RowCard.ROW_HEAD, clubJ, spadesJ));
        assertEquals(Pineapple.STATE_SHOWDOWN, state);

    }

    List<RowCard> makeRowCards(List<RowCard> src, int row, Card... cards) {
        for (var v : cards) {
            src.add(new RowCard(v, row, false));
        }
        return src;
    }

    List<RowCard> makeRowCards(int row, Card... cards) {
        return makeRowCards(new ArrayList<>(), row, cards);
    }

    @Test
    public void testOpen() throws Exception {
        var game = Pineapple.builder()
                .playerNum(3)
                .dealer(0)
                .beforehand()
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
        var state = game.action(0, makeRowCards(RowCard.ROW_TAIL, diamond6, diamond7, diamond8, diamond9, diamond10));
        assertNull(state);

        // 玩家 2 押注
        assertEquals(1, game.opPlayer());
        equalsCards(game.getWaits(0));
        equalsCards(game.getWaits(1), heartA, heart2, clubA, spadesA, spades2);
        equalsCards(game.getWaits(2), heart3, club2, club3, club4, club5);
        state = game.action(2, makeRowCards(RowCard.ROW_MIDDLE, heart3, club2, club3, club4, club5));
        assertNull(state);

        // 玩家 1 押注
        assertEquals(1, game.opPlayer());
        equalsCards(game.getWaits(0));
        equalsCards(game.getWaits(1), heartA, heart2, clubA, spadesA, spades2);
        equalsCards(game.getWaits(2));
        state = game.action(1, makeRowCards(RowCard.ROW_TAIL, heartA, heart2, clubA, spadesA, spades2));
        assertEquals(Pineapple.STATE_OPEN, state);

        // STATE_OPEN
        equalsCards(game.getWaits(0));
        equalsCards(game.getWaits(1));
        equalsCards(game.getWaits(2));
        state = game.open();
        assertEquals(Pineapple.STATE_OPEN, state);
        state = game.open();
        assertEquals(Pineapple.STATE_CIRCLE_END, state);

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

        equalsCards(game.getRowCards(0, RowCard.ROW_HEAD));
        equalsCards(game.getRowCards(0, RowCard.ROW_MIDDLE));
        equalsCards(game.getRowCards(0, RowCard.ROW_TAIL));
        equalsCards(game.getRowCards(1, RowCard.ROW_HEAD));
        equalsCards(game.getRowCards(1, RowCard.ROW_MIDDLE));
        equalsCards(game.getRowCards(1, RowCard.ROW_TAIL));
        equalsCards(game.getRowCards(2, RowCard.ROW_HEAD));
        equalsCards(game.getRowCards(2, RowCard.ROW_MIDDLE));
        equalsCards(game.getRowCards(2, RowCard.ROW_TAIL));

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
        game.action(0, makeRowCards(RowCard.ROW_TAIL, diamond6, diamond7, diamond8, diamond9, diamond10));

        equalsCards(game.getWaits(0));
        equalsCards(game.getWaits(1), heartA, heart2, clubA, spadesA, spades2);
        equalsCards(game.getWaits(2));
        game.action(1, makeRowCards(RowCard.ROW_TAIL, heartA, heart2, clubA, spadesA, spades2));

        equalsCards(game.getWaits(0));
        equalsCards(game.getWaits(1));
        equalsCards(game.getWaits(2), heart3, club2, club3, club4, club5);
        game.action(2, makeRowCards(RowCard.ROW_MIDDLE, heart3, club2, club3, club4, club5));

        equalsCards(game.getFolds(0));
        equalsCards(game.getFolds(1));
        equalsCards(game.getFolds(2));

        equalsCards(game.getRowCards(0, RowCard.ROW_HEAD));
        equalsCards(game.getRowCards(0, RowCard.ROW_MIDDLE));
        equalsCards(game.getRowCards(0, RowCard.ROW_TAIL), diamond6, diamond7, diamond8, diamond9, diamond10);
        equalsCards(game.getRowCards(1, RowCard.ROW_HEAD));
        equalsCards(game.getRowCards(1, RowCard.ROW_MIDDLE));
        equalsCards(game.getRowCards(1, RowCard.ROW_TAIL), heartA, heart2, clubA, spadesA, spades2);
        equalsCards(game.getRowCards(2, RowCard.ROW_HEAD));
        equalsCards(game.getRowCards(2, RowCard.ROW_MIDDLE), heart3, club2, club3, club4, club5);
        equalsCards(game.getRowCards(2, RowCard.ROW_TAIL));

        // 第2圈
        equalsCards(game.getWaits(0), diamondA, diamond2, club7);
        waits = game.getWaits(0); // 顺序
        assertEquals(diamond2, waits.get(0));
        assertEquals(club7, waits.get(1));
        assertEquals(diamondA, waits.get(2));

        equalsCards(game.getWaits(1));
        equalsCards(game.getWaits(2));
        game.action(0, makeRowCards(RowCard.ROW_MIDDLE, diamondA, diamond2));

        equalsCards(game.getWaits(0));
        equalsCards(game.getWaits(1), spades3, spades4, spades5);
        equalsCards(game.getWaits(2));
        game.action(1, makeRowCards(RowCard.ROW_MIDDLE, spades3, spades4));

        equalsCards(game.getWaits(0));
        equalsCards(game.getWaits(1));
        equalsCards(game.getWaits(2), spades10, heartJ, heartQ);
        game.action(2, makeRowCards(RowCard.ROW_TAIL, spades10, heartJ));

        equalsCards(game.getFolds(0), club7);
        equalsCards(game.getFolds(1), spades5);
        equalsCards(game.getFolds(2), heartQ);

        equalsCards(game.getRowCards(0, RowCard.ROW_HEAD));
        equalsCards(game.getRowCards(0, RowCard.ROW_MIDDLE), diamondA, diamond2);
        equalsCards(game.getRowCards(0, RowCard.ROW_TAIL), diamond6, diamond7, diamond8, diamond9, diamond10);
        equalsCards(game.getRowCards(1, RowCard.ROW_HEAD));
        equalsCards(game.getRowCards(1, RowCard.ROW_MIDDLE), spades3, spades4);
        equalsCards(game.getRowCards(1, RowCard.ROW_TAIL), heartA, heart2, clubA, spadesA, spades2);
        equalsCards(game.getRowCards(2, RowCard.ROW_HEAD));
        equalsCards(game.getRowCards(2, RowCard.ROW_MIDDLE), heart3, club2, club3, club4, club5);
        equalsCards(game.getRowCards(2, RowCard.ROW_TAIL), spades10, heartJ);

        // 第3圈
        equalsCards(game.getWaits(0), diamond3, diamond4, club9);
        waits = game.getWaits(0); // 顺序
        assertEquals(diamond3, waits.get(0));
        assertEquals(diamond4, waits.get(1));
        assertEquals(club9, waits.get(2));

        equalsCards(game.getWaits(1));
        equalsCards(game.getWaits(2));
        game.action(0, makeRowCards(RowCard.ROW_MIDDLE, diamond3, diamond4));

        equalsCards(game.getWaits(0));
        equalsCards(game.getWaits(1), spades6, spades7, heart4);
        equalsCards(game.getWaits(2));
        game.action(1, makeRowCards(RowCard.ROW_MIDDLE, spades6, spades7));

        equalsCards(game.getWaits(0));
        equalsCards(game.getWaits(1));
        equalsCards(game.getWaits(2), spades9, heartK, spades8);
        game.action(2, makeRowCards(RowCard.ROW_TAIL, spades9, heartK));

        equalsCards(game.getFolds(0), club7, club9);
        equalsCards(game.getFolds(1), spades5, heart4);
        equalsCards(game.getFolds(2), heartQ, spades8);

        equalsCards(game.getRowCards(0, RowCard.ROW_HEAD));
        equalsCards(game.getRowCards(0, RowCard.ROW_MIDDLE), diamondA, diamond2, diamond3, diamond4);
        equalsCards(game.getRowCards(0, RowCard.ROW_TAIL), diamond6, diamond7, diamond8, diamond9, diamond10);
        equalsCards(game.getRowCards(1, RowCard.ROW_HEAD));
        equalsCards(game.getRowCards(1, RowCard.ROW_MIDDLE), spades3, spades4, spades6, spades7);
        equalsCards(game.getRowCards(1, RowCard.ROW_TAIL), heartA, heart2, clubA, spadesA, spades2);
        equalsCards(game.getRowCards(2, RowCard.ROW_HEAD));
        equalsCards(game.getRowCards(2, RowCard.ROW_MIDDLE), heart3, club2, club3, club4, club5);
        equalsCards(game.getRowCards(2, RowCard.ROW_TAIL), spades10, heartJ, spades9, heartK);

        // 第4圈
        equalsCards(game.getWaits(0), diamondJ, diamond5, heart10);
        waits = game.getWaits(0); // 顺序
        assertEquals(diamond5, waits.get(0));
        assertEquals(heart10, waits.get(1));
        assertEquals(diamondJ, waits.get(2));
        equalsCards(game.getWaits(1));
        equalsCards(game.getWaits(2));
        game.action(0, makeRowCards(makeRowCards(RowCard.ROW_HEAD, diamondJ), RowCard.ROW_MIDDLE, diamond5));

        equalsCards(game.getWaits(0));
        equalsCards(game.getWaits(1), heart8, heart5, heart9);
        equalsCards(game.getWaits(2));
        game.action(1, makeRowCards(makeRowCards(RowCard.ROW_HEAD, heart8), RowCard.ROW_MIDDLE, heart5));

        equalsCards(game.getWaits(0));
        equalsCards(game.getWaits(1));
        equalsCards(game.getWaits(2), heart7, diamondQ, club6);
        game.action(2, makeRowCards(makeRowCards(RowCard.ROW_TAIL, heart7), RowCard.ROW_HEAD, diamondQ));

        equalsCards(game.getFolds(0), club7, club9, heart10);
        equalsCards(game.getFolds(1), spades5, heart4, heart9);
        equalsCards(game.getFolds(2), heartQ, spades8, club6);

        equalsCards(game.getRowCards(0, RowCard.ROW_HEAD), diamondJ);
        equalsCards(game.getRowCards(0, RowCard.ROW_MIDDLE), diamondA, diamond2, diamond3, diamond4, diamond5);
        equalsCards(game.getRowCards(0, RowCard.ROW_TAIL), diamond6, diamond7, diamond8, diamond9, diamond10);
        equalsCards(game.getRowCards(1, RowCard.ROW_HEAD), heart8);
        equalsCards(game.getRowCards(1, RowCard.ROW_MIDDLE), spades3, spades4, spades6, spades7, heart5);
        equalsCards(game.getRowCards(1, RowCard.ROW_TAIL), heartA, heart2, clubA, spadesA, spades2);
        equalsCards(game.getRowCards(2, RowCard.ROW_HEAD), diamondQ);
        equalsCards(game.getRowCards(2, RowCard.ROW_MIDDLE), heart3, club2, club3, club4, club5);
        equalsCards(game.getRowCards(2, RowCard.ROW_TAIL), spades10, heartJ, spades9, heartK, heart7);

        // 第5圈
        equalsCards(game.getWaits(0), clubJ, spadesJ, diamondK);
        waits = game.getWaits(0); // 顺序
        assertTrue(clubJ.equals(waits.get(0)) || spadesJ.equals(waits.get(0)));
        assertTrue(clubJ.equals(waits.get(1)) || spadesJ.equals(waits.get(1)));
        assertEquals(diamondK, waits.get(2));
        equalsCards(game.getWaits(1));
        equalsCards(game.getWaits(2));
        game.action(0, makeRowCards(RowCard.ROW_HEAD, clubJ, spadesJ));

        equalsCards(game.getWaits(0));
        equalsCards(game.getWaits(1), spadesQ, spadesK, heart6);
        equalsCards(game.getWaits(2));
        game.action(1, makeRowCards(RowCard.ROW_HEAD, spadesQ, spadesK));

        equalsCards(game.getWaits(0));
        equalsCards(game.getWaits(1));
        equalsCards(game.getWaits(2), club8, club10, clubQ);
        game.action(2, makeRowCards(RowCard.ROW_HEAD, club8, club10));

        equalsCards(game.getFolds(0), club7, club9, heart10, diamondK);
        equalsCards(game.getFolds(1), spades5, heart4, heart9, heart6);
        equalsCards(game.getFolds(2), heartQ, spades8, club6, clubQ);

        equalsCards(game.getRowCards(0, RowCard.ROW_HEAD), diamondJ, clubJ, spadesJ);
        equalsCards(game.getRowCards(0, RowCard.ROW_MIDDLE), diamondA, diamond2, diamond3, diamond4, diamond5);
        equalsCards(game.getRowCards(0, RowCard.ROW_TAIL), diamond6, diamond7, diamond8, diamond9, diamond10);
        equalsCards(game.getRowCards(1, RowCard.ROW_HEAD), heart8, spadesQ, spadesK);
        equalsCards(game.getRowCards(1, RowCard.ROW_MIDDLE), spades3, spades4, spades6, spades7, heart5);
        equalsCards(game.getRowCards(1, RowCard.ROW_TAIL), heartA, heart2, clubA, spadesA, spades2);
        equalsCards(game.getRowCards(2, RowCard.ROW_HEAD), diamondQ, club8, club10);
        equalsCards(game.getRowCards(2, RowCard.ROW_MIDDLE), heart3, club2, club3, club4, club5);
        equalsCards(game.getRowCards(2, RowCard.ROW_TAIL), spades10, heartJ, spades9, heartK, heart7);
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
        var state = game.action(1, makeRowCards(RowCard.ROW_TAIL, heartA, heart2, clubA, spadesA, spades2));
        assertEquals(Pineapple.STATE_CIRCLE_END, state);

        // 第2圈
        assertEquals(1, game.opPlayer());
        state = game.action(1, makeRowCards(RowCard.ROW_MIDDLE, spades3, spades4));
        assertEquals(Pineapple.STATE_CIRCLE_END, state);

        // 第3圈
        assertEquals(1, game.opPlayer());
        state = game.action(1, makeRowCards(RowCard.ROW_MIDDLE, spades6, spades7));
        assertEquals(Pineapple.STATE_CIRCLE_END, state);

        // 第4圈
        assertEquals(1, game.opPlayer());
        state = game.action(1, makeRowCards(makeRowCards(RowCard.ROW_HEAD, heart8), RowCard.ROW_MIDDLE, heart5));
        assertEquals(Pineapple.STATE_CIRCLE_END, state);

        // 第5圈
        assertEquals(1, game.opPlayer());
        state = game.action(1, makeRowCards(RowCard.ROW_HEAD, spadesQ, spadesK));
        assertEquals(Pineapple.STATE_NEXT_OP, state);

        assertEquals(0, game.opPlayer());
        assertEquals(Pineapple.FANTASY_CARD_NUM, game.getWaits(0).size());

        var act = makeRowCards(RowCard.ROW_HEAD, club7, club9, heart10);
        act = makeRowCards(act, RowCard.ROW_MIDDLE, diamondA, diamond2, diamond3, diamond4, diamond5);
        act = makeRowCards(act, RowCard.ROW_TAIL, diamond6, diamond7, diamond8, diamond9, diamond10);
        state = game.action(0, act);
        assertEquals(Pineapple.STATE_SHOWDOWN, state);


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
        state = game.action(0, makeRowCards(RowCard.ROW_TAIL, diamond6, diamond7, diamond8, diamond9, diamond10));
        assertEquals(Pineapple.STATE_CIRCLE_END, state);

        // 第2圈
        assertEquals(0, game.opPlayer());
        state = game.action(0, makeRowCards(RowCard.ROW_MIDDLE, diamondA, diamond2));
        assertEquals(Pineapple.STATE_CIRCLE_END, state);

        // 第3圈
        assertEquals(0, game.opPlayer());
        state = game.action(0, makeRowCards(RowCard.ROW_MIDDLE, diamond3, diamond4));
        assertEquals(Pineapple.STATE_CIRCLE_END, state);

        // 第4圈
        assertEquals(0, game.opPlayer());
        state = game.action(0, makeRowCards(makeRowCards(RowCard.ROW_HEAD, diamondJ), RowCard.ROW_MIDDLE, diamond5));
        assertEquals(Pineapple.STATE_CIRCLE_END, state);

        // 第5圈
        assertEquals(0, game.opPlayer());
        state = game.action(0, makeRowCards(RowCard.ROW_HEAD, clubJ, spadesJ));
        assertEquals(Pineapple.STATE_NEXT_OP, state);

        assertEquals(1, game.opPlayer());
        assertEquals(Pineapple.FANTASY_CARD_NUM, game.getWaits(1).size());

        act = makeRowCards(RowCard.ROW_HEAD, heart9, heart8, spades5);
        act = makeRowCards(act, RowCard.ROW_MIDDLE, spades3, spades4, spades6, spades7, heart5);
        act = makeRowCards(act, RowCard.ROW_TAIL, heartA, heart2, clubA, spadesA, spades2);
        state = game.action(1, act);
        assertEquals(Pineapple.STATE_SHOWDOWN, state);

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
        state = game.action(0, makeRowCards(RowCard.ROW_TAIL, diamond6, diamond7, diamond8, diamond9, diamond10));
        assertEquals(Pineapple.STATE_NEXT_OP, state);
        state = game.action(2, makeRowCards(RowCard.ROW_MIDDLE, heart3, club2, club3, club4, club5));
        assertEquals(Pineapple.STATE_CIRCLE_END, state);

        // 第2圈
        state = game.action(0, makeRowCards(RowCard.ROW_MIDDLE, diamondA, diamond2));
        assertEquals(Pineapple.STATE_NEXT_OP, state);
        state = game.action(2, makeRowCards(RowCard.ROW_TAIL, spades10, heartJ));
        assertEquals(Pineapple.STATE_CIRCLE_END, state);

        // 第3圈
        state = game.action(0, makeRowCards(RowCard.ROW_MIDDLE, diamond3, diamond4));
        assertEquals(Pineapple.STATE_NEXT_OP, state);
        state = game.action(2, makeRowCards(RowCard.ROW_TAIL, spades9, heartK));
        assertEquals(Pineapple.STATE_CIRCLE_END, state);

        // 第4圈
        state = game.action(0, makeRowCards(makeRowCards(RowCard.ROW_HEAD, diamondJ), RowCard.ROW_MIDDLE, diamond5));
        assertEquals(Pineapple.STATE_NEXT_OP, state);
        state = game.action(2, makeRowCards(makeRowCards(RowCard.ROW_HEAD, heart7), RowCard.ROW_TAIL, diamondQ));
        assertEquals(Pineapple.STATE_CIRCLE_END, state);

        // 第5圈
        state = game.action(0, makeRowCards(RowCard.ROW_HEAD, clubJ, spadesJ));
        assertEquals(Pineapple.STATE_NEXT_OP, state);
        state = game.action(2, makeRowCards(RowCard.ROW_HEAD, club10, clubQ));
        assertEquals(Pineapple.STATE_NEXT_OP, state);

        assertEquals(1, game.opPlayer());
        assertEquals(Pineapple.FANTASY_CARD_NUM, game.getWaits(1).size());

        act = makeRowCards(RowCard.ROW_HEAD, heart9, heart8, spades5);
        act = makeRowCards(act, RowCard.ROW_MIDDLE, spades3, spades4, spades6, spades7, heart5);
        act = makeRowCards(act, RowCard.ROW_TAIL, heartA, heart2, clubA, spadesA, spades2);
        state = game.action(1, act);
        assertEquals(Pineapple.STATE_SHOWDOWN, state);

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
        act = makeRowCards(RowCard.ROW_HEAD, heart9, heart8, spades5);
        act = makeRowCards(act, RowCard.ROW_MIDDLE, spades3, spades4, spades6, spades7, heart5);
        act = makeRowCards(act, RowCard.ROW_TAIL, heartA, heart2, clubA, spadesA, spades2);
        state = game.action(1, act);
        assertEquals(Pineapple.STATE_NEXT_OP, state);

        act = makeRowCards(RowCard.ROW_HEAD, club7, club9, heart10);
        act = makeRowCards(act, RowCard.ROW_MIDDLE, diamondA, diamond2, diamond3, diamond4, diamond5);
        act = makeRowCards(act, RowCard.ROW_TAIL, diamond6, diamond7, diamond8, diamond9, diamond10);
        state = game.action(0, act);
        assertEquals(Pineapple.STATE_SHOWDOWN, state);
    }

    @Test
    public void testGetMemo() throws Exception {
        var game = this.builder(3)
                .beforehand()
                .dealer(0)
                .build();

        game.start();

        assertEquals(47, game.getMemo(0).size());
        assertEquals(47, game.getMemo(1).size());
        assertEquals(47, game.getMemo(2).size());

        // 玩家1操作位，玩家0押注，玩家1，2看不到
        assertEquals(1, game.opPlayer());
        var state = game.action(0, makeRowCards(RowCard.ROW_TAIL, diamond6, diamond7, diamond8, diamond9, diamond10));
        assertNull(state);
        assertEquals(47, game.getMemo(0).size());
        assertEquals(47, game.getMemo(1).size());
        assertEquals(47, game.getMemo(2).size());

        // 玩家1操作位，玩家1押注，玩家0，2看到
        assertEquals(1, game.opPlayer());
        state = game.action(1, makeRowCards(RowCard.ROW_TAIL, heartA, heart2, clubA, spadesA, spades2));
        assertEquals(Pineapple.STATE_NEXT_OP, state);
        assertEquals(42, game.getMemo(0).size());
        assertEquals(47, game.getMemo(1).size());
        assertEquals(42, game.getMemo(2).size());

        // 玩家2押注，玩家1, 0看到，
        assertEquals(2, game.opPlayer());
        state = game.action(2, makeRowCards(RowCard.ROW_MIDDLE, heart3, club2, club3, club4, club5));
        assertEquals(Pineapple.STATE_OPEN, state);
        assertEquals(37, game.getMemo(0).size());
        assertEquals(42, game.getMemo(1).size());
        assertEquals(42, game.getMemo(2).size());

        // continue
        state = game.open();
        assertEquals(Pineapple.STATE_CIRCLE_END, state);
        assertEquals(34, game.getMemo(0).size());
        assertEquals(34, game.getMemo(1).size());
        assertEquals(34, game.getMemo(2).size());

        // 第2圈
        // 玩家1权限, 玩家0押注，玩家1, 2看不到
        assertEquals(1, game.opPlayer());
        state = game.action(0, makeRowCards(RowCard.ROW_MIDDLE, diamondA, diamond2));
        assertNull(state);
        assertEquals(34, game.getMemo(0).size());
        assertEquals(34, game.getMemo(1).size());
        assertEquals(34, game.getMemo(2).size());

        // 玩家1权限 玩家1押注，玩家0, 2看到
        assertEquals(1, game.opPlayer());
        state = game.action(1, makeRowCards(RowCard.ROW_MIDDLE, spades3, spades4));
        assertEquals(Pineapple.STATE_NEXT_OP, state);
        assertEquals(32, game.getMemo(0).size());
        assertEquals(34, game.getMemo(1).size());
        assertEquals(32, game.getMemo(2).size());

        // 玩家2权限 玩家2押注，玩家1, 0看到，
        assertEquals(2, game.opPlayer());
        state = game.action(2, makeRowCards(RowCard.ROW_TAIL, spades10, heartJ));
        assertEquals(Pineapple.STATE_OPEN, state);
        assertEquals(30, game.getMemo(0).size());
        assertEquals(32, game.getMemo(1).size());
        assertEquals(32, game.getMemo(2).size());

        // Continue
        state = game.open();
        assertEquals(Pineapple.STATE_CIRCLE_END, state);
        assertEquals(27, game.getMemo(0).size());
        assertEquals(27, game.getMemo(1).size());
        assertEquals(27, game.getMemo(2).size());

        // 第3圈
        // 玩家1权限 玩家0押注，玩家1, 2看不到
        assertEquals(1, game.opPlayer());
        state = game.action(0, makeRowCards(RowCard.ROW_MIDDLE, diamond3, diamond4));
        assertNull(state);
        assertEquals(27, game.getMemo(0).size());
        assertEquals(27, game.getMemo(1).size());
        assertEquals(27, game.getMemo(2).size());

        // 玩家1权限, 玩家2押注，玩家1, 0看不到
        assertEquals(1, game.opPlayer());
        state = game.action(2, makeRowCards(RowCard.ROW_TAIL, spades9, heartK));
        assertNull(state);
        assertEquals(27, game.getMemo(0).size());
        assertEquals(27, game.getMemo(1).size());
        assertEquals(27, game.getMemo(2).size());

        // 玩家1权限, 玩家1押注，玩家0, 2看到
        assertEquals(1, game.opPlayer());
        state = game.action(1, makeRowCards(RowCard.ROW_MIDDLE, spades6, spades7));
        assertEquals(Pineapple.STATE_OPEN, state);
        assertEquals(25, game.getMemo(0).size());
        assertEquals(27, game.getMemo(1).size());
        assertEquals(25, game.getMemo(2).size());

        // Continue
        state = game.open();
        assertEquals(Pineapple.STATE_OPEN, state);
        assertEquals(23, game.getMemo(0).size());
        assertEquals(25, game.getMemo(1).size());
        assertEquals(25, game.getMemo(2).size());

        state = game.open();
        assertEquals(Pineapple.STATE_CIRCLE_END, state);
        assertEquals(20, game.getMemo(0).size());
        assertEquals(20, game.getMemo(1).size());
        assertEquals(20, game.getMemo(2).size());


        game = this.builder(2)
                .beforehand()
                .dealer(0)
                .build();

        game.start();

        assertEquals(47, game.getMemo(0).size());
        assertEquals(47, game.getMemo(1).size());

        // 玩家1操作位，玩家0押注，玩家1
        assertEquals(1, game.opPlayer());
        state = game.action(0, makeRowCards(RowCard.ROW_TAIL, diamond6, diamond7, diamond8, diamond9, diamond10));
        assertNull(state);
        assertEquals(47, game.getMemo(0).size());
        assertEquals(47, game.getMemo(1).size());

        // 玩家1操作位，玩家1押注，玩家0看到
        assertEquals(1, game.opPlayer());
        state = game.action(1, makeRowCards(RowCard.ROW_TAIL, heartA, heart2, clubA, spadesA, spades2));
        assertEquals(Pineapple.STATE_OPEN, state);
        assertEquals(42, game.getMemo(0).size());
        assertEquals(47, game.getMemo(1).size());

        // Continue
        state = game.open();
        assertEquals(Pineapple.STATE_CIRCLE_END, state);
        assertEquals(39, game.getMemo(0).size());
        assertEquals(39, game.getMemo(1).size());

        // -- 第2圈
        // 玩家1权限, 玩家0押注，玩家1看不到
        assertEquals(1, game.opPlayer());
        state = game.action(0, makeRowCards(RowCard.ROW_MIDDLE, diamondA, diamond2));
        assertNull(state);
        assertEquals(39, game.getMemo(0).size());
        assertEquals(39, game.getMemo(1).size());
        //
        // 玩家1权限, 玩家1押注，玩家0)看到
        assertEquals(1, game.opPlayer());
        state = game.action(1, makeRowCards(RowCard.ROW_MIDDLE, spades3, spades4));
        assertEquals(Pineapple.STATE_OPEN, state);
    }

    @Test
    public void testMakeResult() throws Exception {
        var game = this.builder(2)
                .beforehand()
                .dealer(1)
                .build();

        game.start();

        game.action(0, makeRowCards(RowCard.ROW_TAIL, diamond6, diamond7, diamond8, diamond9, diamond10));
        game.action(1, makeRowCards(RowCard.ROW_MIDDLE, heartA, heart2, clubA, spadesA, spades2));

        game.action(0, makeRowCards(RowCard.ROW_MIDDLE, diamondA, diamond2));
        game.action(1, makeRowCards(RowCard.ROW_TAIL, spades3, spades4));

        game.action(0, makeRowCards(RowCard.ROW_MIDDLE, diamond3, diamond4));
        game.action(1, makeRowCards(RowCard.ROW_TAIL, spades6, spades7));

        var acts = makeRowCards(RowCard.ROW_HEAD, diamondJ);
        game.action(0, makeRowCards(acts, RowCard.ROW_MIDDLE, diamond5));
        acts = makeRowCards(RowCard.ROW_HEAD, heart8);
        game.action(1, makeRowCards(acts, RowCard.ROW_TAIL, heart5));

        game.action(0, makeRowCards(RowCard.ROW_HEAD, clubJ, spadesJ));
        var state = game.action(1, makeRowCards(RowCard.ROW_HEAD, spadesQ, spadesK));
        assertEquals(Pineapple.STATE_SHOWDOWN, state);

        var result = game.makeResult();
        assertResult(result.playerList.get(0), new String[]{ResultPlayer.HONOR_FANTASY}, 70, new Integer[]{20, 31, 16}, 1, new Integer[]{20, 31, 16});
        assertResult(result.playerList.get(1), new String[]{ResultPlayer.HONOR_EXPLODE}, -70, new Integer[]{-20, -31, -16}, 0, new Integer[]{-20, -31, -16});
    }

    private void assertResult(ResultPlayer resultPlayer, String[] honors, Integer profit, Integer[] rowProfit, Integer compareId, Integer[] compareProfit) {
        assertEquals(honors.length, resultPlayer.honors.size());
        for (var v : honors) {
            assertTrue(resultPlayer.honors.contains(v));
        }

        assertEquals(profit, resultPlayer.profit);
        var rowResultList = resultPlayer.rowResultList;
        assertEquals(rowProfit[RowCard.ROW_HEAD], rowResultList.get(RowCard.ROW_HEAD).profit);
        assertEquals(rowProfit[RowCard.ROW_MIDDLE], rowResultList.get(RowCard.ROW_MIDDLE).profit);
        assertEquals(rowProfit[RowCard.ROW_TAIL], rowResultList.get(RowCard.ROW_TAIL).profit);

        assertEquals(compareProfit[RowCard.ROW_HEAD], rowResultList.get(RowCard.ROW_HEAD).compare.get(compareId));
        assertEquals(compareProfit[RowCard.ROW_MIDDLE], rowResultList.get(RowCard.ROW_MIDDLE).compare.get(compareId));
        assertEquals(compareProfit[RowCard.ROW_TAIL], rowResultList.get(RowCard.ROW_TAIL).compare.get(compareId));
    }

    Pineapple.Builder builder(int num) {
        var ret = Pineapple.builder()
                .playerNum(num)
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
                        spadesK, spadesQ, heart6);
        if (num == 3) {

            ret.playerCards(2,
                    heart3, club2, club3, club4, club5,
                    spades10, heartJ, heartQ,
                    spades9, spades8, heartK,
                    heart7, diamondQ, club6,
                    club8, club10, clubQ);
        }
        return ret;
    }
}