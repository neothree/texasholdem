package com.texasthree.security.shiro;

import com.texasthree.security.login.entity.Loginer;
import com.texasthree.utility.utlis.Base64;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.MethodInvokingFactoryBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.ClassPathResource;

@Configuration
public class LoginShiroConfig {

    @Bean
    public MeSource<Loginer> meSource() {
        return v -> {
            throw new IllegalStateException("未配置 MeSource");
        };
    }

    /**
     * @param realm 自定义Realm
     * @return 安全管理器
     */
    @Bean(name = "securityManager")
    public DefaultWebSecurityManager defaultWebSecurityManager(
            LoginerRealm realm,
            CookieRememberMeManager cookieRememberMeManager) {
        var manager = new DefaultWebSecurityManager(realm);
        manager.setRememberMeManager(cookieRememberMeManager);
        return manager;
    }

    @Bean
    public AuthorizationAttributeSourceAdvisor getAuthorizationAttributeSourceAdvisor(
            @Lazy @Qualifier("securityManager") DefaultWebSecurityManager manager) {
        var advisor = new AuthorizationAttributeSourceAdvisor();
        advisor.setSecurityManager(manager);
        return advisor;
    }

    /**
     * 相当于调用SecurityUtils.setSecurityManager(securityManager)
     *
     * @param manager 安全管理器
     * @return 相当于调用SecurityUtils.setSecurityManager(securityManager)
     */
    @Bean
    public MethodInvokingFactoryBean methodInvokingFactoryBean(
            @Lazy @Qualifier("securityManager") DefaultWebSecurityManager manager) {
        var bean = new MethodInvokingFactoryBean();
        bean.setStaticMethod("org.apache.shiro.SecurityUtils.setSecurityManager");
        bean.setArguments(manager);
        return bean;
    }

    @Bean(name = "ehCacheManagerFactoryBean")
    public EhCacheManagerFactoryBean ehCacheManagerFactoryBean() {
        var factoryBean = new EhCacheManagerFactoryBean();
        factoryBean.setConfigLocation(new ClassPathResource("ehcache/ehcache.xml"));
        return factoryBean;
    }

    @Bean
    public CookieRememberMeManager cookieRememberMeManager() {
        var cookieRememberMeManager = new CookieRememberMeManager();
        var simpleCookie = new SimpleCookie("rememberMe");
        simpleCookie.setMaxAge(259200000);
        cookieRememberMeManager.setCookie(simpleCookie);
        cookieRememberMeManager.setCipherKey(Base64.decode("6ZmI6I2j5Y+R5aSn5ZOlAA=="));
        return cookieRememberMeManager;
    }

    @Bean
    @ConditionalOnMissingBean
    public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
        var app = new DefaultAdvisorAutoProxyCreator();
        app.setProxyTargetClass(true);
        return app;
    }

    @Bean
    public LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }
}
