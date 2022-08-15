package com.texasthree.security.login.dao;

import com.texasthree.security.login.entity.Loginer;
import com.texasthree.security.login.enums.Active;
import com.texasthree.security.login.enums.LoginApp;
import com.texasthree.utility.utlis.StringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static junit.framework.TestCase.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class LoginDaoTest {

    @Autowired
    private LoginerDao loginDao;

    @Test
    public void testLoginer() throws Exception {
        var username = StringUtils.get10UUID();
        var password = StringUtils.get10UUID();
        var app = LoginApp.USER;
        var loginer = new Loginer(username, password, app);
        this.loginDao.save(loginer);

        var optional = this.loginDao.findByUsername(username);
        assertTrue(optional.isPresent());
        var find = optional.get();
        assertNotNull(loginer.getId());
        assertEquals(0, find.getVersion());
        assertEquals(username, find.getUsername());
        assertEquals(app.name(), find.getApp());
        assertNotNull(find.getPassword());
        assertNotNull(find.getSalt());

        var optional1 = this.loginDao.findById(find.getId());
        assertTrue(optional1.isPresent());
    }

    @Test
    public void testVersion() throws Exception {
        var loginer = new Loginer(StringUtils.get10UUID(), StringUtils.get10UUID(), LoginApp.USER);
        this.loginDao.save(loginer);
        assertEquals(0, loginer.getVersion());

        loginer.setStatus(Active.UNACTIVE.name());
        this.loginDao.save(loginer);
        loginer = this.loginDao.findById(loginer.getId()).get();
        assertEquals(1, loginer.getVersion());
    }
}