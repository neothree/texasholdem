package com.texasthree.zone.club;

import com.texasthree.account.AccountException;
import com.texasthree.account.AccountService;
import com.texasthree.utility.utlis.StringUtils;
import com.texasthree.zone.Tester;
import com.texasthree.zone.user.User;
import com.texasthree.zone.user.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.print.DocFlavor;
import java.math.BigDecimal;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;

@WebAppConfiguration
@ExtendWith(SpringExtension.class)
@SpringBootTest
class ClubServiceTest {

    @Autowired
    private ClubService clubService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private UserService userService;

    @Test
    public void testFund() throws Exception {
        var club = getClub();
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
        var club = getClub();
        var id = club.getId();
        Runnable func = () -> this.clubService.fundToBalance(id, BigDecimal.ONE, StringUtils.get10UUID());
        Tester.assertException(func, AccountException.ACCOUNT_SUB_AMOUNT_OUTLIMIT);

        var fund = this.accountService.getDataById(club.getFundId());
        assertEquals(0, fund.getBalance().compareTo(BigDecimal.ZERO));
        var balance = this.accountService.getDataById(club.getBalanceId());
        assertEquals(0, balance.getBalance().compareTo(BigDecimal.ZERO));

        var amount = BigDecimal.valueOf(100);
        this.clubService.fund(id, amount);
        this.clubService.fundToBalance(id, BigDecimal.TEN, StringUtils.get10UUID());
        fund = this.accountService.getDataById(club.getFundId());
        assertEquals(0, fund.getBalance().compareTo(BigDecimal.valueOf(90)));
        balance = this.accountService.getDataById(club.getBalanceId());
        assertEquals(0, balance.getBalance().compareTo(BigDecimal.TEN));
    }

    @Test
    public void testMember() throws Exception {
        var club = getClub();
        var user = getUser();
        assertNotEquals(user.getClubId(), club.getId());
        assertNull(this.clubService.getDataByClubIdAndUid(club.getId(), user.getId()));

        this.clubService.addMember(club.getId(), user);
        user = this.userService.getDataById(user.getId());
        assertEquals(club.getId(), user.getClubId());
        assertNotNull(this.clubService.getDataByClubIdAndUid(club.getId(), user.getId()));
    }

    @Test
    public void testBalanceToMember() throws Exception {
        var club = getClub();
        var user = getUser();
        this.clubService.addMember(club.getId(), user);
        this.clubService.fund(club.getId(), BigDecimal.valueOf(1000));
        this.clubService.fundToBalance(club.getId(), BigDecimal.valueOf(700), StringUtils.get10UUID());

        var balance = this.accountService.getDataById(club.getBalanceId());
        assertEquals(0, balance.getBalance().compareTo(BigDecimal.valueOf(700)));
        var account = this.accountService.getDataById(user.getAccountId());
        assertEquals(0, account.getBalance().compareTo(BigDecimal.ZERO));

        var amount = BigDecimal.valueOf(211);
        this.clubService.balanceToMember(club.getId(), user.getId(), amount, club.getCreator());
        balance = this.accountService.getDataById(club.getBalanceId());
        assertEquals(0, balance.getBalance().compareTo(BigDecimal.valueOf(489)));
        account = this.accountService.getDataById(user.getAccountId());
        assertEquals(0, account.getBalance().compareTo(amount));

        // 错误：只有创始人可以发放
        Runnable func = () -> this.clubService.balanceToMember(club.getId(), user.getId(), amount, StringUtils.get10UUID());
        Tester.assertException(func, IllegalArgumentException.class);
    }

    @Test
    public void testMemberToBalance() throws Exception {
        var club = getClub();
        var user = getUser();
        this.clubService.addMember(club.getId(), user);

        var sum = BigDecimal.valueOf(1000);
        user = this.userService.balance(user.getId(), sum);
        var account = this.accountService.getDataById(user.getAccountId());
        assertEquals(0, account.getBalance().compareTo(sum));
        var balance = this.accountService.getDataById(club.getBalanceId());
        assertEquals(0, balance.getBalance().compareTo(BigDecimal.ZERO));

        var amount = BigDecimal.valueOf(200);
        this.clubService.memberToBalance(club.getId(), user.getId(), amount);
        account = this.accountService.getDataById(user.getAccountId());
        assertEquals(0, account.getBalance().compareTo(sum.subtract(amount)));
        balance = this.accountService.getDataById(club.getBalanceId());
        assertEquals(0, balance.getBalance().compareTo(amount));
    }

    @Test
    public void testPlatform() throws Exception {
        assertNotNull(this.clubService.platform());
    }

    private Club getClub() {
        return this.clubService.club(StringUtils.get10UUID(), StringUtils.get10UUID());
    }

    private User getUser() {
        return this.userService.user(StringUtils.get10UUID(), StringUtils.get10UUID(), true, StringUtils.get10UUID());
    }

}