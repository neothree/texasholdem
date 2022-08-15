package com.texasthree.security.login.entity;

import com.texasthree.security.SecurityException;
import com.texasthree.security.login.enums.Active;
import com.texasthree.security.login.enums.LoginApp;
import com.texasthree.security.shiro.LoginerRealm;
import com.texasthree.utility.utlis.StringUtils;

import java.time.LocalDateTime;

import static com.texasthree.security.shiro.LoginerRealm.getPwd;

/**
 * 登录用户
 */
public class Loginer extends Entity<String> {
    /**
     * 登录名
     */
    private String username;
    /**
     * 登录密码
     */
    private String password;
    /**
     * 白名单状态
     */
    private String whiteStatus;
    /**
     * 白名单
     */
    private String white;
    /**
     * 认证加密的盐
     */
    private String salt;

    public String getCredentialsSalt() {
        return username + salt;
    }

    private String app;

    private String email;


    public Loginer() {
    }

    public Loginer(String username, String password, LoginApp app) {
        if (StringUtils.isEmpty(username, password) || app == null) {
            throw new IllegalArgumentException(
                    new StringBuilder("Loginer创建错误")
                            .append("username=").append(username)
                            .append(", password=").append(password)
                            .append(", app=").append(app)
                            .toString());
        }
        username = username.trim();
        password = password.trim();

        if (username.length() < 6
                || password.length() < 6) {
            throw new IllegalArgumentException();
        }


        this.setId(StringUtils.get16UUID());
        this.setUsername(username);
        this.setApp(app.name());
        this.setStatus(Active.ACTIVE.name());
        this.setCreateTime(LocalDateTime.now());
        this.setWhiteStatus(Active.UNACTIVE.name());

        // 生成加密密码
        this.setSalt(LoginerRealm.getSalt());
        var newPassword = getPwd(password, this.getCredentialsSalt());
        this.setPassword(newPassword);
    }


    public void login(String password) {
        if (!getPwd(password, this.getCredentialsSalt()).equals(this.getPassword())) {
            throw SecurityException.USERNAME_PASSWORD_ERROR.newInstance();
        }

        // 被禁止登录
        if (Active.UNACTIVE.name().equals(this.getStatus())) {
            throw SecurityException.AUTH_NOT_PASS.newInstance();
        }
    }

    public void changePassword(String password) {
        if (StringUtils.isEmpty(password)) {
            throw new IllegalArgumentException();
        }
        this.password = getPwd(password, this.getCredentialsSalt());
    }

    public void changePassword(String oldPassword, String newPassword) {
        if (StringUtils.isEmpty(oldPassword, newPassword)) {
            throw new IllegalArgumentException();
        }

        if (!getPwd(oldPassword, this.getCredentialsSalt()).equals(password)) {
            throw SecurityException.PASSWORD_ERROR.newInstance();
        }
        this.password = getPwd(newPassword, this.getCredentialsSalt());
    }

    public void changeWhiteStatus(Active status) {
        if (status == null) {
            throw new IllegalArgumentException();
        }
        this.whiteStatus = status.name();
    }

    public void changeStatus(Active status) {
        if (status == null) {
            throw new IllegalArgumentException();
        }
        this.setStatus(status.name());
    }


    @Override
    public String toString() {
        return new StringBuilder()
                .append("username=").append(username)
                .append(", app=").append(app)
                .append(", id=").append(this.getId())
                .toString();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getWhiteStatus() {
        return whiteStatus;
    }

    public void setWhiteStatus(String whiteStatus) {
        this.whiteStatus = whiteStatus;
    }

    public String getWhite() {
        return white;
    }

    public void setWhite(String white) {
        this.white = white;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
