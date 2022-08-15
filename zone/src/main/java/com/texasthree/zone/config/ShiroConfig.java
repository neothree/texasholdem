package com.texasthree.zone.config;


import com.texasthree.security.shiro.LoginerRealm;
import com.texasthree.security.shiro.MeSource;
import com.texasthree.security.shiro.filter.FormAuthFilter;
import com.texasthree.zone.entity.User;
import com.texasthree.zone.token.UserService;
import org.apache.commons.collections.map.LinkedMap;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.LinkedHashMap;

@Configuration
public class ShiroConfig {

    @Primary
    @Bean("UserMeSource")
    public MeSource<User> meSource(UserService userService) {
        return userService::getDataByUsername;
    }

    /**
     * Shiro主过滤器本身功能十分强大,其强大之处就在于它支持任何基于URL路径表达式的、自定义的过滤器的执行
     * Web应用中,Shiro可控制的Web请求必须经过Shiro主过滤器的拦截,Shiro对基于Spring的Web应用提供了完美的支持
     *
     * @param manager             安全管理器
     * @return Shiro 主过滤器
     */
    @Bean(name = "shiroFilter")
    public ShiroFilterFactoryBean shiroFilterFactoryBean(
            @Qualifier("securityManager") DefaultWebSecurityManager manager,
            LoginerRealm<User> loginerRealm) {
        var factoryBean = new ShiroFilterFactoryBean();
        factoryBean.setSecurityManager(manager);
        factoryBean.setLoginUrl("/login");
        var filters = new LinkedMap();
        filters.put("authc", new FormAuthFilter(loginerRealm));
        factoryBean.setFilters(filters);

        var map = new LinkedHashMap<String, String>();
        map.put("/login", "anon");
        map.put("/**", "authc");
        factoryBean.setFilterChainDefinitionMap(map);
        return factoryBean;
    }
}
