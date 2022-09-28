package com.texasthree.zone.club;

import com.texasthree.account.AccountException;
import com.texasthree.account.AccountService;
import com.texasthree.dao.Pagination;
import com.texasthree.utility.utlis.StringUtils;
import com.texasthree.zone.Tester;
import com.texasthree.zone.club.transaction.CTType;
import com.texasthree.zone.club.transaction.ClubTransaction;
import com.texasthree.zone.club.transaction.Status;
import com.texasthree.zone.user.User;
import com.texasthree.zone.user.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;

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
    public void testClub() throws Exception {
        var user = getUser();
        var name = StringUtils.getChineseName();
        var club = this.clubService.club(user.getId(), name);
        assertEquals(user.getId(), club.getCreator());

        var p = this.clubService.memberPage(club.getId(), new Pagination());
        assertEquals(1, p.getTotalElements());
        assertEquals(user.getId(), p.getContent().get(0).getUid());
    }

    @Test
    public void testClubPage() throws Exception {
        var p = this.clubService.clubPage(new Pagination());
        var total = p.getTotalElements();

        this.clubService.club(StringUtils.get10UUID(), StringUtils.get10UUID());
        p = this.clubService.clubPage(new Pagination());
        assertEquals( total + 1, p.getTotalElements());

        this.clubService.club(StringUtils.get10UUID(), StringUtils.get10UUID());
        p = this.clubService.clubPage(new Pagination());
        assertEquals(total + 2, p.getTotalElements());
    }

    @Test
    public void testMember() throws Exception {
        var club = getClub();
        var user = getUser();
        assertNotEquals(user.getClubId(), club.getId());
        assertNull(this.clubService.getDataByClubIdAndUid(club.getId(), user.getId()));

        this.clubService.member(club.getId(), user);
        user = this.userService.getDataById(user.getId());
        assertEquals(club.getId(), user.getClubId());
        assertNotNull(this.clubService.getDataByClubIdAndUid(club.getId(), user.getId()));
    }

    @Test
    public void testMemberPage() throws Exception {
        var club = getClub();
        var user = getUser();
        this.clubService.member(club.getId(), user);
        var p = this.clubService.memberPage(club.getId(), new Pagination());
        assertEquals(2, p.getTotalElements());
        assertTrue(p.getContent().stream().anyMatch(v -> v.getUid().equals(user.getId())));
        assertTrue(p.getContent().stream().anyMatch(v -> v.getUid().equals(club.getCreator())));
    }

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
        var trx = this.clubService.fundToBalance(id, BigDecimal.TEN, StringUtils.get10UUID());
        trx = this.clubService.getTrxById(trx.getId());
        assertTrx(trx, club.getId(), BigDecimal.TEN, CTType.FUND, Status.SUCCESS);

        fund = this.accountService.getDataById(club.getFundId());
        assertEquals(0, fund.getBalance().compareTo(BigDecimal.valueOf(90)));
        balance = this.accountService.getDataById(club.getBalanceId());
        assertEquals(0, balance.getBalance().compareTo(BigDecimal.TEN));
    }

    @Test
    public void testBalanceToMember() throws Exception {
        var club = getClub();
        var user = getUser();
        this.clubService.member(club.getId(), user);
        this.clubService.fund(club.getId(), BigDecimal.valueOf(1000));
        this.clubService.fundToBalance(club.getId(), BigDecimal.valueOf(700), StringUtils.get10UUID());

        var balance = this.accountService.getDataById(club.getBalanceId());
        assertEquals(0, balance.getBalance().compareTo(BigDecimal.valueOf(700)));
        var account = this.accountService.getDataById(user.getAccountId());
        assertEquals(0, account.getBalance().compareTo(BigDecimal.ZERO));

        var amount = BigDecimal.valueOf(211);
        var trx = this.clubService.balanceToMember(club.getId(), user.getId(), amount, club.getCreator());
        trx = this.clubService.getTrxById(trx.getId());
        assertTrx(trx, club.getId(), amount, CTType.TO_MEMBER, Status.SUCCESS);
        balance = this.accountService.getDataById(club.getBalanceId());
        assertEquals(0, balance.getBalance().compareTo(BigDecimal.valueOf(489)));
        account = this.accountService.getDataById(user.getAccountId());
        assertEquals(0, account.getBalance().compareTo(amount));

        // 错误：只有创始人可以发放
        Runnable func = () -> this.clubService.balanceToMember(club.getId(), user.getId(), amount, StringUtils.get10UUID());
        Tester.assertException(func, IllegalArgumentException.class);

        // 错误：不是自己俱乐部成员
        func = () -> this.clubService.balanceToMember(club.getId(), this.getUser().getId(), amount, StringUtils.get10UUID());
        Tester.assertException(func, IllegalArgumentException.class);
    }

    @Test
    public void testMemberToBalance() throws Exception {
        var club = getClub();
        var user = getUser();
        this.clubService.member(club.getId(), user);

        var sum = BigDecimal.valueOf(1000);
        user = this.userService.balance(user.getId(), sum);
        var account = this.accountService.getDataById(user.getAccountId());
        assertEquals(0, account.getBalance().compareTo(sum));
        var balance = this.accountService.getDataById(club.getBalanceId());
        assertEquals(0, balance.getBalance().compareTo(BigDecimal.ZERO));

        var amount = BigDecimal.valueOf(200);
        var trx = this.clubService.memberToBalance(club.getId(), user.getId(), amount);
        trx = this.clubService.getTrxById(trx.getId());
        assertTrx(trx, club.getId(), amount, CTType.FROM_MEMBER, Status.SUCCESS);

        account = this.accountService.getDataById(user.getAccountId());
        assertEquals(0, account.getBalance().compareTo(sum.subtract(amount)));
        balance = this.accountService.getDataById(club.getBalanceId());
        assertEquals(0, balance.getBalance().compareTo(amount));

        // 错误：不是自己俱乐部成员
        Runnable func = () -> this.clubService.memberToBalance(club.getId(), this.getUser().getId(), amount);
        Tester.assertException(func, IllegalArgumentException.class);
    }

    @Test
    public void testPlatform() throws Exception {
        assertNotNull(this.clubService.platform());
    }

    private Club getClub() {
        var user = getUser();
        return this.clubService.club(user.getId(), StringUtils.get10UUID());
    }

    private User getUser() {
        return this.userService.user(StringUtils.get10UUID(), StringUtils.get10UUID(), true, StringUtils.get10UUID());
    }

    private void assertTrx(ClubTransaction trx, String clubId, BigDecimal amount, CTType type, Status status) {
        assertEquals(clubId, trx.getClubId());
        assertEquals(0, trx.getAmount().compareTo(amount));
        assertEquals(type.name(), trx.getType());
        assertEquals(status.name(), trx.getStatus());
    }

}