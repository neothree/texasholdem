package com.texasthree.account;

import com.texasthree.utility.utlis.DateUtils;
import com.texasthree.utility.utlis.StringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@WebAppConfiguration
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class AccountServiceTest {

    @Autowired
    private AccountService accountService;

    public Account getAccount() {
        return this.accountService.account(StringUtils.get16UUID(), false);
    }

    @Test
    @Transactional
    public void testRegister() throws Exception {
        var name = StringUtils.get16UUID();
        var account = this.accountService.account(name, false);
        account = this.accountService.getDataById(account.getId());
        assertEquals(name, account.getName());
        assertEquals(0, account.getBalance().compareTo(BigDecimal.ZERO));
        assertEquals(0, account.getAvailableBalance().compareTo(BigDecimal.ZERO));
        assertEquals(0, account.getPendingBalance().compareTo(BigDecimal.ZERO));
        assertEquals(0, account.getTotalExpend().compareTo(BigDecimal.ZERO));
        assertEquals(0, account.getTotalIncome().compareTo(BigDecimal.ZERO));
        assertEquals(0, account.getTodayExpend().compareTo(BigDecimal.ZERO));
        assertEquals(0, account.getTodayIncome().compareTo(BigDecimal.ZERO));
        assertNotNull(account.getCreateAt());
        assertNotNull(account.getEditAt());
    }

    private int[] unchanged = new int[]{0, 0, 0, 0, 0, 0, 0};

    @Test
    @Transactional
    public void testCredit() throws Exception {
        var bit = new int[]{1, 0, 1, 1, 0, 1, 0};
        var accountId = this.getAccount().getId();
        var old = this.accountService.getDataById(accountId);
        old = copy(old);

        // 错误 - 不能小于0
        Runnable func = () -> this.accountService.credit(accountId, BigDecimal.valueOf(-1), StringUtils.get16UUID());
        assertException(func, IllegalArgumentException.class);

        // 可以是0
        func = () -> this.accountService.credit(accountId, BigDecimal.ZERO, StringUtils.get16UUID());
        this.check(func, old, BigDecimal.ZERO, unchanged);

        // 加上 amount1
        var amount1 = BigDecimal.valueOf(1000);
        func = () -> this.accountService.credit(accountId, amount1, StringUtils.get16UUID());
        this.check(func, old, amount1, bit);

        var amount2 = BigDecimal.valueOf(10);
        func = () -> this.accountService.credit(accountId, amount2, StringUtils.get16UUID());
        this.check(func, old, amount1.add(amount2), bit);

        func = () -> this.accountService.credit(accountId, amount1, StringUtils.get16UUID());
        this.checkCurrency(accountId, func, amount1, bit);
    }

    @Test
    @Transactional
    public void testDebit() throws Exception {
        int[] bit = new int[]{-1, 0, -1, 0, 1, 0, 1};
        var accountId = this.getAccount().getId();
        var old = copy(this.accountService.getDataById(accountId));

        // 错误 - 不能小于0
        Runnable func = () -> this.accountService.debit(accountId, BigDecimal.valueOf(-1), StringUtils.get16UUID());
        assertException(func, IllegalArgumentException.class);

        // 可以是0
        func = () -> this.accountService.debit(accountId, BigDecimal.ZERO, StringUtils.get16UUID());
        this.check(func, old, BigDecimal.ZERO, unchanged);

        // 错误 - 不能减成负数
        var amount = old.getAvailableBalance().add(BigDecimal.ONE);
        func = () -> this.accountService.debit(accountId, amount, StringUtils.get16UUID());
        assertException(func, AccountException.ACCOUNT_SUB_AMOUNT_OUTLIMIT);


        var add = BigDecimal.valueOf(1000);
        old = this.accountService.credit(accountId, add, StringUtils.get16UUID());
        old = copy(old);

        var amount1 = BigDecimal.valueOf(500);
        func = () -> this.accountService.debit(accountId, amount1, StringUtils.get16UUID());
        this.check(func, old, amount1, bit);

        var amount2 = BigDecimal.valueOf(400);
        func = () -> this.accountService.debit(accountId, amount2, StringUtils.get16UUID());
        this.check(func, old, amount1.add(amount2), bit);

        // 减到0
        var avail = this.accountService.getDataById(accountId).getAvailableBalance();
        func = () -> this.accountService.debit(accountId, avail, StringUtils.get16UUID());
        this.check(func, old, amount1.add(amount2).add(avail), bit);
        old = copy(this.accountService.getDataById(accountId));
        assertEquals(0, old.getAvailableBalance().compareTo(BigDecimal.ZERO));

        // 货币
        this.accountService.credit(accountId, add, StringUtils.get16UUID());

        func = () -> this.accountService.debit(accountId, amount1, StringUtils.get16UUID());
        this.checkCurrency(accountId, func, amount1, bit);

        // 可以为负数
        var account = this.accountService.account(StringUtils.getChineseName(), true);
        assertTrue(account.isEnableNegative());
        this.accountService.debit(account.getId(), BigDecimal.TEN, StringUtils.get10UUID());
        assertEquals(0, account.getBalance().compareTo(BigDecimal.valueOf(-10)));
    }

    @Test
    @Transactional
    public void testPending() throws Exception {
        int[] bit = new int[]{0, 1, -1, 0, 0, 0, 0};
        var accountId = this.getAccount().getId();

        var add = BigDecimal.valueOf(1000);
        this.accountService.credit(accountId, add, StringUtils.get10UUID());
        var old = copy(this.accountService.getDataById(accountId));

        // 错误 - 不能小于0
        Runnable func = () -> this.accountService.pending(accountId, BigDecimal.valueOf(-1));
        assertException(func, IllegalArgumentException.class);

        // 可以是0
        func = () -> this.accountService.pending(accountId, BigDecimal.ZERO);
        this.check(func, old, BigDecimal.ZERO, unchanged);

        // 错误 - 减成了负数
        func = () -> this.accountService.pending(accountId, old.getAvailableBalance().add(BigDecimal.ONE));
        assertException(func, AccountException.ACCOUNT_SUB_AMOUNT_OUTLIMIT);


        var freeze1 = BigDecimal.valueOf(100);
        func = () -> this.accountService.pending(accountId, freeze1);
        this.check(func, old, freeze1, bit);

        var freeze2 = BigDecimal.valueOf(400);
        func = () -> this.accountService.pending(accountId, freeze2);
        this.check(func, old, freeze1.add(freeze2), bit);

        // 货币
        this.accountService.credit(accountId, add, StringUtils.get16UUID());

        func = () -> this.accountService.pending(accountId, freeze1);
        this.checkCurrency(accountId, func, freeze1, bit);
    }

    @Test
    @Transactional
    public void testUnpendingDebit() throws Exception {
        int[] bit = new int[]{-1, -1, 0, 0, 1, 0, 1};
        var accountId = this.getAccount().getId();
        var add = BigDecimal.valueOf(1000);
        this.accountService.credit(accountId, add, StringUtils.get16UUID());
        var old = copy(this.accountService.pending(accountId, add));

        // 错误 - 不能小于0
        Runnable func = () -> this.accountService.unpendingDebit(accountId, BigDecimal.valueOf(-1), StringUtils.get16UUID());
        assertException(func, IllegalArgumentException.class);

        // 可以是0
        func = () -> this.accountService.unpendingDebit(accountId, BigDecimal.ZERO, StringUtils.get16UUID());
        this.check(func, old, BigDecimal.ZERO, unchanged);

        // 错误 - 减成了负数
        func = () -> this.accountService.unpendingDebit(accountId, old.getPendingBalance().add(BigDecimal.ONE), StringUtils.get16UUID());
        assertException(func, AccountException.ACCOUNT_UN_FROZEN_AMOUNT_OUTLIMIT);

        var freeze1 = BigDecimal.valueOf(100);
        func = () -> this.accountService.unpendingDebit(accountId, freeze1, StringUtils.get16UUID());
        this.check(func, old, freeze1, bit);

        var freeze2 = BigDecimal.valueOf(400);
        func = () -> this.accountService.unpendingDebit(accountId, freeze2, StringUtils.get16UUID());
        this.check(func, old, freeze1.add(freeze2), bit);

        // 货币
        this.accountService.credit(accountId, add, StringUtils.get16UUID());
        this.accountService.pending(accountId, add);

        func = () -> this.accountService.unpendingDebit(accountId, freeze1, StringUtils.get16UUID());
        this.checkCurrency(accountId, func, freeze1, bit);
    }

    @Test
    @Transactional
    public void testUnpending() throws Exception {
        var accountId = this.getAccount().getId();
        int[] bit = new int[]{0, -1, 1, 0, 0, 0, 0};
        var add = BigDecimal.valueOf(1000);
        this.accountService.credit(accountId, add, StringUtils.get16UUID());
        this.accountService.pending(accountId, add);
        var old = copy(this.accountService.getDataById(accountId));

        // 错误 - 不能小于0
        Runnable func = () -> this.accountService.unpending(accountId, BigDecimal.valueOf(-1));
        assertException(func, IllegalArgumentException.class);

        // 可以是0
        func = () -> this.accountService.unpending(accountId, BigDecimal.ZERO);
        this.check(func, old, BigDecimal.ZERO, unchanged);

        // 错误 - 减成了负数
        func = () -> this.accountService.unpending(accountId, old.getPendingBalance().add(BigDecimal.ONE));
        assertException(func, AccountException.ACCOUNT_UN_FROZEN_AMOUNT_OUTLIMIT);

        // 执行 500
        var freeze1 = BigDecimal.valueOf(500);
        this.check(() -> this.accountService.unpending(accountId, freeze1), old, freeze1, bit);

        // 执行 400
        var freeze2 = BigDecimal.valueOf(400);
        this.check(() -> this.accountService.unpending(accountId, freeze2), old, freeze1.add(freeze2), bit);

        // 货币
        this.accountService.credit(accountId, add, StringUtils.get16UUID());
        this.accountService.pending(accountId, add);

        func = () -> this.accountService.unpending(accountId, freeze1);
        this.checkCurrency(accountId, func, freeze1, bit);
    }

    @Test
    @Transactional
    public void testAccount() throws Exception {
        var name = StringUtils.get16UUID();
        var account = this.accountService.account(name, false);
        if (!DateUtils.isSameDayWithToday(account.getEditAt())) {
            assertEquals(0, account.getTodayExpend().compareTo(BigDecimal.ZERO));
            assertEquals(0, account.getTodayIncome().compareTo(BigDecimal.ZERO));
        }

        account = this.accountService.getDataById(account.getId());
        assertEquals(name, account.getName());


        account = new Account();
        account.setBalance(BigDecimal.valueOf(100));
        account.setPendingBalance(BigDecimal.valueOf(30));
        assertEquals(0, account.getAvailableBalance().compareTo(BigDecimal.valueOf(70)));


        var todayIncome = BigDecimal.valueOf(100);
        var todayExpand = BigDecimal.valueOf(30);
        account.setTodayIncome(todayIncome);
        account.setTodayExpend(todayExpand);
        account.setEditAt(LocalDateTime.now());
        assertEquals(0, account.getTodayIncome().compareTo(todayIncome));
        assertEquals(0, account.getTodayExpend().compareTo(todayExpand));

        // 换到前一天
        account.setEditAt(LocalDateTime.now().minusDays(-1));
        assertEquals(0, account.getTodayIncome().compareTo(BigDecimal.ZERO));
        assertEquals(0, account.getTodayExpend().compareTo(BigDecimal.ZERO));
    }

    private void checkCurrency(String accountId, Runnable func, BigDecimal amount1, int[] bit) throws Exception {
        var cny = this.accountService.getDataById(accountId);
        this.check(null, cny, BigDecimal.ZERO, bit);
    }

    private void check(Runnable func, Account old, BigDecimal amount, int[] bit) throws Exception {
        if (func != null) {
            func.run();
        }
        var now = this.accountService.getDataById(old.getId());
        for (int i = 0; i < bit.length; i++) {
            int op = bit[i];
            switch (i) {
                case 0:
                    this.comprare(old.getBalance(), now.getBalance(), amount, op);
                    break;
                case 1:
                    this.comprare(old.getPendingBalance(), now.getPendingBalance(), amount, op);
                    break;
                case 2:
                    this.comprare(old.getAvailableBalance(), now.getAvailableBalance(), amount, op);
                    break;
                case 3:
                    this.comprare(old.getTodayIncome(), now.getTodayIncome(), amount, op);
                    break;
                case 4:
                    this.comprare(old.getTodayExpend(), now.getTodayExpend(), amount, op);
                    break;
                case 5:
                    this.comprare(old.getTotalIncome(), now.getTotalIncome(), amount, op);
                    break;
                case 6:
                    this.comprare(old.getTotalExpend(), now.getTotalExpend(), amount, op);
                    break;
                default:
                    throw IllegalArgumentException.class.newInstance();

            }
        }
    }

    private void comprare(BigDecimal old, BigDecimal now, BigDecimal amount, int op) {
        switch (op) {
            case -1:
                assertEquals(0, old.subtract(amount).compareTo(now));
                break;
            case 0:
                assertEquals(0, old.compareTo(now));
                break;
            case 1:
                assertEquals(0, old.add(amount).compareTo(now));
                break;
            default:
                throw new IllegalArgumentException();
        }
    }

    public static void assertException(Runnable func, Class biz) throws Exception {
        try {
            func.run();
            assertTrue(false);
        } catch (Exception e) {
            assertEquals(biz, e.getClass());
        }
    }

    public static void assertException(Runnable func, AccountException biz) throws Exception {
        try {
            func.run();
            assertTrue(false);
        } catch (AccountException e) {
            assertEquals(biz.getCode(), e.getCode());
        }
    }

    private Account copy(Account src) {
        var v = new Account();
        v.setId(src.getId());
        v.setBalance(src.getBalance());
        v.setPendingBalance(src.getPendingBalance());
        v.setTodayExpend(src.getTodayExpend());
        v.setTodayIncome(src.getTodayIncome());
        v.setTotalExpend(src.getTotalExpend());
        v.setTotalIncome(src.getTotalIncome());
        v.setCreateAt(src.getCreateAt());
        v.setEditAt(src.getEditAt());
        return v;

    }
}
