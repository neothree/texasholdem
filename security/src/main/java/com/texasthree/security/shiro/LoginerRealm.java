package com.texasthree.security.shiro;


import com.texasthree.security.login.entity.Loginer;
import com.texasthree.security.login.enums.Active;
import com.texasthree.security.login.service.LoginerService;
import com.texasthree.utility.restful.RestResponse;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.crypto.RandomNumberGenerator;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 自定义realm .
 */
@Component
public class LoginerRealm<T> extends AuthorizingRealm implements LoginListener {

    private static final Logger log = LoggerFactory.getLogger(LoginerRealm.class);

    public static final String ME_KEY = "ME";

    private static final String LOGINER_KEY = "LOGINER";

    private static final String AUTHC = "AUTHC";

    private static Session getSession() {
        return SecurityUtils.getSubject().getSession();
    }

    public static Loginer getLoginer() {
        return (Loginer) getSession().getAttribute(LOGINER_KEY);
    }

    public static <T> T getMe() {
        return (T) getSession().getAttribute(ME_KEY);
    }

    public static void removeMe() {
        getSession().removeAttribute(ME_KEY);
    }

    private static final RandomNumberGenerator SECURE_RANDOM_NUMBER_GENERATOR = new SecureRandomNumberGenerator();

    private static final String ALGORITHM_NAME = "md5";

    private static final int HASH_ITERATIONS = 2;

    public static String getPwd(String password, String salt) {
        return new SimpleHash(ALGORITHM_NAME, password, ByteSource.Util.bytes(salt), HASH_ITERATIONS).toHex();
    }

    public static String getSalt() {
        return SECURE_RANDOM_NUMBER_GENERATOR.nextBytes().toHex();
    }

    private final LoginerService loginerService;

    private final MeSource<T> meSource;

    @Autowired
    public LoginerRealm(RetryLimitHashedCredentialsMatcher matcher,
                        LoginerService loginerService,
                        MeSource<T> meSource) {
        this.setCredentialsMatcher(matcher);
        this.setCachingEnabled(false);
        this.loginerService = loginerService;
        this.meSource = meSource;
    }

    /**
     * 身份验证
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        var username = (String) token.getPrincipal();
        // 交给AuthenticatingRealm使用CredentialsMatcher进行密码匹配，如果觉得人家的不好可以自定义实现
        // 如果没有在 login 注册则不能登录
        // TODO 这个地方可以成为攻击的弱点，会一直请求数据库
        var loginer = this.loginerService.getDataByUsername(username);
        if (loginer == null) {
            throw new UnknownAccountException();
        }
        if (this.meSource.getMeByUsername(username) == null) {
            throw new UnknownAccountException();
        }
        // 帐号锁定
        if (Active.UNACTIVE.name().equals(loginer.getStatus())) {
            throw new LockedAccountException();
        }

        return new SimpleAuthenticationInfo(
                // 登录名
                loginer.getUsername(),
                // 密码
                loginer.getPassword(),
                // salt=username+salt
                ByteSource.Util.bytes(loginer.getCredentialsSalt()),
                // realm name
                getName()
        );
    }

    /**
     * 权限验证
     */
    @SuppressWarnings("unchecked")
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        var auth = (SimpleAuthorizationInfo) getSession().getAttribute(AUTHC);
        if (auth == null) {
            auth = new SimpleAuthorizationInfo();
            getSession().setAttribute(AUTHC, auth);
        }
        return auth;
    }

    public RestResponse login(String username, String password) {
        RestResponse response;
        try {
            var token = new UsernamePasswordToken(username, password);
            token.setRememberMe(true);
            SecurityUtils.getSubject().login(token);
            this.onSuccess(username);
            log.info("账户登录 {}", username);
            response = RestResponse.SUCCESS;
        } catch (UnknownAccountException | IncorrectCredentialsException e) {
            response = UNKNOWN_ACCOUNT;
        } catch (LockedAccountException e) {
            response = LOCKED_ACCOUNT;
        } catch (ExcessiveAttemptsException ea) {
            response = EXCESSIVE_ATTEMPTS;
        } catch (AuthenticationException | SecurityException e) {
            response = RestResponse.error(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            response = RestResponse.error(e.getMessage());
        }
        return response;
    }

    public void logout() {
        log.info("账户退出 {}", SecurityUtils.getSubject().getPrincipal());
        removeMe();
        SecurityUtils.getSubject().logout();
    }

    @Override
    public void onSuccess(String username) {
        getSession().setAttribute(LOGINER_KEY, loginerService.getDataByUsername(username));
        getSession().setAttribute(ME_KEY, meSource.getMeByUsername(username));
    }


    public static final RestResponse UNKNOWN_ACCOUNT = new RestResponse(11, "用户名/密码错误");

    public static final RestResponse LOCKED_ACCOUNT = new RestResponse(12, "账户禁止登录");

    public static final RestResponse EXCESSIVE_ATTEMPTS = new RestResponse(13, "登陆过于频繁，请稍后再试");

}
