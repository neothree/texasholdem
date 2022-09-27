package com.texasthree.zone.user;

import com.texasthree.utility.utlis.StringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;


@WebAppConfiguration
@ExtendWith(SpringExtension.class)
@SpringBootTest
class UserDataDaoTest {

    @Autowired
    private UserDataDao userDataDao;

    @Test
    public void testSave() throws Exception {
        var username = StringUtils.get10UUID();
        var name = StringUtils.get10UUID();
        var real = false;
        var clubId = StringUtils.get10UUID();
        var data = new UserData(username, name, real, clubId, StringUtils.get10UUID());
        this.userDataDao.save(data);
        var opt = this.userDataDao.findByUsername(username);
        assertTrue(opt.isPresent());
        data = opt.get();
        assertEquals(username, data.getUsername());
        assertEquals(name, data.getName());
        assertEquals(0, data.getVersion());
        assertEquals(real, data.isReal());
        assertEquals(clubId, data.getClubId());

        var avatar = StringUtils.get10UUID();
        data.setAvatar(avatar);
        this.userDataDao.save(data);
        data = this.userDataDao.findById(data.getId()).get();
        assertEquals(avatar, data.getAvatar());
        assertEquals(1, data.getVersion());
    }
}