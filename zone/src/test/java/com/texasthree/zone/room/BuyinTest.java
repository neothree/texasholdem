package com.texasthree.zone.room;

import com.texasthree.zone.Tester;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BuyinTest {

    @Test
    void testBuyin() throws Exception {
        var user = Tester.createUser();
        var buyin = new Buyin(user);
        assertEquals(user.getId(), buyin.getUid());
        assertFalse(buyin.isSettle());
        assertBuyin(buyin, 0, 0, 0);

        var amount = 1000;
        buyin.buyin(amount);
        assertBuyin(buyin, amount, 0, amount);

        var profit = 100;
        buyin.changeProfit(profit);
        assertBuyin(buyin, amount, profit, amount + profit);

        var profit1 = -500;
        buyin.changeProfit(profit1);
        assertBuyin(buyin, amount, profit + profit1, amount + profit + profit1);

        var amount1 = 200;
        buyin.buyin(200);
        assertBuyin(buyin, amount + amount1, profit + profit1, amount + profit + profit1 + amount1);

        assertFalse(buyin.isSettle());
        buyin.settle();
        assertTrue(buyin.isSettle());
        assertBuyin(buyin, amount + amount1, profit + profit1, 0);
    }

    void assertBuyin(Buyin buyin, int sum, int profit, int balance) {
        assertEquals(balance, buyin.getBalance());
        assertEquals(profit, buyin.getProfit());
        assertEquals(sum, buyin.getSum());
    }
}