package com.texasthree.account;


import com.texasthree.utility.utlis.StringUtils;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

public class AccountTest {

    @Test
    public void testAccount() {
        var name = StringUtils.get16UUID();
        var account = new Account(name, false);
        assertEquals(name, account.getName());
        assertEquals(0, account.getBalance().compareTo(BigDecimal.ZERO));
        assertEquals(0, account.getAvailableBalance().compareTo(BigDecimal.ZERO));
        assertEquals(0, account.getPendingBalance().compareTo(BigDecimal.ZERO));
        assertEquals(0, account.getTotalExpend().compareTo(BigDecimal.ZERO));
        assertEquals(0, account.getTotalIncome().compareTo(BigDecimal.ZERO));
        assertEquals(0, account.getTodayExpend().compareTo(BigDecimal.ZERO));
        assertEquals(0, account.getTodayIncome().compareTo(BigDecimal.ZERO));
        assertFalse(account.isEnableNegative());
        assertNotNull(account.getCreateAt());
        assertNotNull(account.getEditAt());
    }

    @Test
    public void testCredit() throws Exception {
        var account = this.getAccount();
        assertEquals(0, account.getBalance().compareTo(BigDecimal.ZERO));
        assertEquals(0, account.getAvailableBalance().compareTo(BigDecimal.ZERO));

        var amount = BigDecimal.valueOf(100);
        var requestNo = StringUtils.get16UUID();
        account.credit(amount, requestNo);
        assertEquals(0, account.getBalance().compareTo(amount));
        assertEquals(0, account.getAvailableBalance().compareTo(amount));
        assertEquals(0, account.getTodayIncome().compareTo(amount));
        assertEquals(0, account.getTotalIncome().compareTo(amount));
    }

    @Test
    public void testDebit() throws Exception {
        var account = new Account(StringUtils.get16UUID(), false);
        var amount = BigDecimal.valueOf(100);
        account.credit(amount, StringUtils.get10UUID());
        assertEquals(0, account.getBalance().compareTo(amount));

        var amount1 = BigDecimal.valueOf(20);
        account.debit(amount1, StringUtils.get10UUID(), false);
        assertEquals(0, account.getBalance().compareTo(amount.subtract(amount1)));

        // 错误 - 没有开启 enableNegative 不能小于0
        var big = BigDecimal.valueOf(1000);
        Runnable func = () -> account.debit(big, StringUtils.get10UUID(), false);
        Tester.assertException(func, AccountException.ACCOUNT_SUB_AMOUNT_OUTLIMIT);
        func = () -> account.debit(big, StringUtils.get10UUID(), true);
        Tester.assertException(func, AccountException.ACCOUNT_SUB_AMOUNT_OUTLIMIT);

        // enableNegative 没有开启选项
        var account1 = new Account(StringUtils.get16UUID(), true);
        func = () -> account1.debit(amount, StringUtils.get10UUID(), false);
        Tester.assertException(func, AccountException.ACCOUNT_SUB_AMOUNT_OUTLIMIT);

        account1.debit(amount, StringUtils.get10UUID(), true);
        assertEquals(0, account1.getBalance().compareTo(amount.negate()));
    }

    @Test
    public void testAvailableBalanceIsEnough() {
        var account = this.getAccount();
        assertEquals(0, account.getBalance().compareTo(BigDecimal.ZERO));
        assertFalse(account.isEnableNegative());
        assertFalse(account.availableBalanceIsEnough(BigDecimal.TEN));

        account.credit(BigDecimal.valueOf(100), StringUtils.get10UUID());
        assertTrue(account.availableBalanceIsEnough(BigDecimal.TEN));
    }

    private Account getAccount() {
        var account = new Account(StringUtils.get16UUID(), false);
        account.setEditAt(LocalDateTime.now());
        account.setCreateAt(LocalDateTime.now());
        return account;
    }

}
