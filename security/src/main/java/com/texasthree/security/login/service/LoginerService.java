package com.texasthree.security.login.service;


import com.texasthree.security.SecurityException;
import com.texasthree.security.login.dao.LoginerDao;
import com.texasthree.security.login.entity.Loginer;
import com.texasthree.security.login.enums.LoginApp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class LoginerService {

    protected final Logger log = LoggerFactory.getLogger(LoginerService.class);

    private final LoginerDao loginDao;

    public LoginerService(
            LoginerDao dao) {
        this.loginDao = dao;
    }

    public Loginer getDataByUsername(String username) {
        return this.loginDao.findByUsername(username).orElse(null);
    }

    @Transactional
    public Loginer loginer(String username, String password, LoginApp app) {
        var loginer = this.getDataByUsername(username);
        if (loginer != null) {
            throw SecurityException.USERNAME_EXISTS.newInstance();
        }

        loginer = new Loginer(username, password, app);
        this.loginDao.save(loginer);
        log.info("注册登录账户 {} {}", username, app);
        return loginer;
    }
}
