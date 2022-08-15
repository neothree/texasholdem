package com.texasthree.security.login.service;


import com.texasthree.security.SecurityException;
import com.texasthree.security.login.dao.LoginDao;
import com.texasthree.security.login.entity.Loginer;
import com.texasthree.security.login.enums.LoginApp;
import com.texasthree.utility.restful.RestResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class LoginService {

    protected final Logger log = LoggerFactory.getLogger(LoginService.class);

    private final LoginDao loginDao;

    public LoginService(
            LoginDao dao) {
        this.loginDao = dao;
    }

    public Loginer getDataByUsername(String username) {
        return this.loginDao.findDataByUsername(username);
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

    public RestResponse login(String username, String password) {
//        var loginer = checkLoginer(username);
//        loginer.login(password);
        return RestResponse.SUCCESS;
    }
}
