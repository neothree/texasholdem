package com.texasholdem.security.login.service;


import com.texasholdem.security.SecurityException;
import com.texasholdem.security.login.dao.LoginDao;
import com.texasholdem.security.login.entity.Loginer;
import com.texasholdem.security.login.enums.LoginApp;
import com.texasholdem.utility.restful.RestResponse;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;


@Service
public class LoginService {

    private final LoginDao loginDao;

    public LoginService(
                            LoginDao dao) {
        this.loginDao = dao;
    }

    public Loginer getDataByUsername(String username) {
        if (StringUtils.isEmpty(username)) {
            throw new IllegalArgumentException();
        }
        return null;
    }

    public Loginer loginer(String username, String password, LoginApp app) {
        var loginer = this.getDataByUsername(username);
        if (loginer != null) {
            throw SecurityException.USERNAME_EXISTS.newInstance();
        }

//        loginer = new Loginer(username, password, app);
//        this.insert(loginer);
//        log.info("注册登录账户 {} {}", username, app);
        return loginer;
    }

    public RestResponse login(String username, String password) {
//        var loginer = checkLoginer(username);
//        loginer.login(password);
        return RestResponse.SUCCESS;
    }


}
