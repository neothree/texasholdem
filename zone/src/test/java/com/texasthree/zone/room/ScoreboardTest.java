package com.texasthree.zone.room;

import com.texasthree.zone.Tester;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ScoreboardTest {

    @Test
    void testBuyin() throws Exception {
        var user = Tester.createUser();
        var board = new Scoreboard(user);
        assertEquals(user.getId(), board.getUid());
        assertFalse(board.isSettle());
        assertBuyin(board, 0, 0, 0);

        var amount = 1000;
        board.buyin(amount);
        assertBuyin(board, amount, 0, amount);

        var profit = 100;
        board.gameProfit(profit);
        assertBuyin(board, amount, profit, amount + profit);

        var profit1 = -500;
        board.gameProfit(profit1);
        assertBuyin(board, amount, profit + profit1, amount + profit + profit1);

        var amount1 = 200;
        board.buyin(200);
        assertBuyin(board, amount + amount1, profit + profit1, amount + profit + profit1 + amount1);

        assertFalse(board.isSettle());
        board.settle();
        assertTrue(board.isSettle());
        assertBuyin(board, amount + amount1, profit + profit1, 0);
    }

    void assertBuyin(Scoreboard buyin, int sum, int profit, int balance) {
        assertEquals(balance, buyin.getBalance());
        assertEquals(profit, buyin.getGameProfit());
        assertEquals(sum, buyin.getBuyin());
    }
}