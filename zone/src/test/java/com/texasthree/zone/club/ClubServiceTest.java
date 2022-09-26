package com.texasthree.zone.club;

import com.texasthree.utility.utlis.StringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

@WebAppConfiguration
@ExtendWith(SpringExtension.class)
@SpringBootTest
class ClubServiceTest {

    @Autowired
    private ClubService clubService;

    @Test
    public void testFund() throws Exception {
        var club = this.clubService.club(StringUtils.get10UUID(), StringUtils.get10UUID());
        assertEquals(BigDecimal.ZERO, club.getFund());

        var amount = BigDecimal.valueOf(100);
        club = this.clubService.fund(club.getId(), amount);
        assertEquals(0, club.getFund().compareTo(amount));

        var amount1 = BigDecimal.valueOf(-50);
        club = this.clubService.fund(club.getId(), amount1);
        assertEquals(0, club.getFund().compareTo(amount.add(amount1)));
    }
}