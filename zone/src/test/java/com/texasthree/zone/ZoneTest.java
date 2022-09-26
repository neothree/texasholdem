package com.texasthree.zone;

import com.texasthree.security.login.service.LoginerService;
import com.texasthree.utility.utlis.StringUtils;
import com.texasthree.zone.club.ClubService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@WebAppConfiguration
@ExtendWith(SpringExtension.class)
@SpringBootTest
class ZoneTest {

    @Autowired
    private ClubService clubService;

    @Autowired
    private LoginerService loginerService;

    @Autowired
    private Zone zone;

    @Test
    public void testCreateUser() throws Exception {
        var username = StringUtils.get10UUID();
        var password = StringUtils.get10UUID();
        var user = this.zone.createUser(username, password, true);

        var club = clubService.platform();
        assertEquals(club.getId(), user.getClubId());

        var loginer = this.loginerService.getDataByUsername(username);
        assertNotNull((loginer));
    }

}