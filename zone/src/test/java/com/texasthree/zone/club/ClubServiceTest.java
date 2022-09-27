package com.texasthree.zone.club;

import com.texasthree.account.AccountException;
import com.texasthree.account.AccountService;
import com.texasthree.utility.utlis.StringUtils;
import com.texasthree.zone.Tester;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;

import java.math.BigDecimal;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@WebAppConfiguration
@ExtendWith(SpringExtension.class)
@SpringBootTest
class ClubServiceTest {

    @Autowired
    private ClubService clubService;

    @Autowired
    private AccountService accountService;

    @Test
    public void testFund() throws Exception {
        var club = this.clubService.club(StringUtils.get10UUID(), StringUtils.get10UUID());
        var fund = this.accountService.getDataById(club.getFundId());
        assertEquals(0, fund.getBalance().compareTo(BigDecimal.ZERO));

        var amount = BigDecimal.valueOf(100);
        this.clubService.fund(club.getId(), amount);
        fund = this.accountService.getDataById(club.getFundId());
        assertEquals(0, fund.getBalance().compareTo(amount));

        var amount1 = BigDecimal.valueOf(-50);
        this.clubService.fund(club.getId(), amount1);
        fund = this.accountService.getDataById(club.getFundId());
        assertEquals(0, fund.getBalance().compareTo(amount.add(amount1)));

        // 基金扣为负数
        var amount2 = BigDecimal.valueOf(-150);
        this.clubService.fund(club.getId(), amount2);
        fund = this.accountService.getDataById(club.getFundId());
        assertEquals(0, fund.getBalance().compareTo(amount.add(amount1).add(amount2)));
        assertTrue(fund.getBalance().compareTo(BigDecimal.ZERO) < 0);
    }

    @Test
    public void testFundToBalance() throws Exception {
        var club = this.clubService.club(StringUtils.get10UUID(), StringUtils.get10UUID());
        var id = club.getId();
        Runnable func = () -> this.clubService.fundToBalance(id, BigDecimal.ONE);
        Tester.assertException(func, AccountException.ACCOUNT_SUB_AMOUNT_OUTLIMIT);

        var fund = this.accountService.getDataById(club.getFundId());
        assertEquals(0, fund.getBalance().compareTo(BigDecimal.ZERO));
        var balance = this.accountService.getDataById(club.getBalanceId());
        assertEquals(0, balance.getBalance().compareTo(BigDecimal.ZERO));

        var amount = BigDecimal.valueOf(100);
        this.clubService.fund(id, amount);
        this.clubService.fundToBalance(id, BigDecimal.TEN);
        fund = this.accountService.getDataById(club.getFundId());
        assertEquals(0, fund.getBalance().compareTo(BigDecimal.valueOf(90)));
        balance = this.accountService.getDataById(club.getBalanceId());
        assertEquals(0, balance.getBalance().compareTo(BigDecimal.TEN));
    }

    @Test
    public void testPlatform() throws Exception {
        assertNotNull(this.clubService.platform());
    }
}