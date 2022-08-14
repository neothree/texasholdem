package com.texasholdem.security.shiro.filter;

/**
 * @author: neo
 * @create: 2022-06-25 23:10
 */
public interface LoginListener {
    void onSuccess(String username);
}
