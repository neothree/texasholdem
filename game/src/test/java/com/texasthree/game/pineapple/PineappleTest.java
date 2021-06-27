package com.texasthree.game.pineapple;

import com.texasthree.game.AllCard;
import com.texasthree.game.texas.Card;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


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
}