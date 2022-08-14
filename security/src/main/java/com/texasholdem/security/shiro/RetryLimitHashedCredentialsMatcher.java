package com.texasholdem.security.shiro;

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.ExcessiveAttemptsException;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 凭证匹配器，做登录次数验证，和密码匹配验证
 */
@Component
public class RetryLimitHashedCredentialsMatcher extends HashedCredentialsMatcher {

    private final Cache<String, AtomicInteger> passwordRetryCache;

    @Autowired
    public RetryLimitHashedCredentialsMatcher(
            CacheManager cacheManager) {
        this.passwordRetryCache = cacheManager.getCache("passwordRetryCache");
        this.setHashAlgorithmName("md5");
        this.setHashIterations(2);
        this.setStoredCredentialsHexEncoded(true);
    }

    /**
     * 做认证匹配
     */
    @Override
    public boolean doCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) {
        var username = (String) token.getPrincipal();
        // retry count + 1
        var retryCount = passwordRetryCache.get(username);
        if (retryCount == null) {
            retryCount = new AtomicInteger(0);
            passwordRetryCache.put(username, retryCount);
        }
        if (retryCount.incrementAndGet() > 5) {
            throw new ExcessiveAttemptsException();
        }

        boolean matches = super.doCredentialsMatch(token, info);
        if (matches) {
            // clear retry count
            passwordRetryCache.remove(username);
        }
        return matches;
    }
}
