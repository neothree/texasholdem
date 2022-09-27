package com.texasthree.zone;

import com.texasthree.account.AccountService;
import com.texasthree.utility.utlis.StringUtils;
import com.texasthree.zone.club.Club;
import com.texasthree.zone.club.ClubService;
import com.texasthree.zone.room.Scoreboard;
import com.texasthree.zone.user.User;
import com.texasthree.zone.user.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;

import java.math.BigDecimal;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

@WebAppConfiguration
@ExtendWith(SpringExtension.class)
@SpringBootTest
class FundFlowTest {

    @Autowired
    private FundFlow fundFlow;

    @Autowired
    private UserService userService;

    @Autowired
    private ClubService clubService;

    @Autowired
    private AccountService accountService;

    @Test
    public void testShare() throws Exception {
        var club = this.clubService.club(StringUtils.get10UUID(), StringUtils.get10UUID());
        var club1 = this.clubService.club(StringUtils.get10UUID(), StringUtils.get10UUID());
        var clubs = new ArrayList<Club>();
        clubs.add(club);
        clubs.add(club1);
        var user = this.userService.user(StringUtils.get10UUID(), StringUtils.get10UUID(), true, club.getId());
        var user1 = this.userService.user(StringUtils.get10UUID(), StringUtils.get10UUID(), true, club1.getId());
        var users = new ArrayList<User>();
        users.add(user);
        users.add(user1);

        // 记分牌 - 总带入是 2000
        var sb = new Scoreboard(user);
        sb.buyin(1000);
        sb.insuranceProfit(700);
        sb.gameProfit(-900);
        var sb1 = new Scoreboard(user1);
        sb1.buyin(1000);
        sb1.insuranceProfit(-500);
        sb1.gameProfit(900);
        var list = new ArrayList<Scoreboard>();
        list.add(sb);
        list.add(sb1);
        this.fundFlow.share(list);

        // 结算后金额变化
        user = this.userService.getDataById(user.getId());
        club = this.clubService.getClubById(club.getId());
        assertEquals(0, user.getBalance().compareTo(BigDecimal.valueOf(800)));
        var fund = this.accountService.getDataById(club.getFundId());
        assertEquals(0, fund.getBalance().compareTo(BigDecimal.valueOf(-655)));

        user1 = this.userService.getDataById(user1.getId());
        club1 = this.clubService.getClubById(club1.getId());
        assertEquals(0, user1.getBalance().compareTo(BigDecimal.valueOf(1380)));
        fund = this.accountService.getDataById(club1.getFundId());
        assertEquals(0, fund.getBalance().compareTo(BigDecimal.valueOf(475)));

        // 账是平的
        var sum = BigDecimal.ZERO;
        for (var v : clubs) {
            var c = this.clubService.getClubById(v.getId());
            fund = this.accountService.getDataById(c.getFundId());
            sum = sum.add(fund.getBalance());
        }
        for (var v : users) {
            var c = this.userService.getDataById(v.getId());
            sum = sum.add(c.getBalance());
        }
        assertEquals(0, sum.compareTo(BigDecimal.valueOf(2000)));
    }
}