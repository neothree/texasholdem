package com.texasholdem.security.shiro;

/**
 * @author: neo
 * @create: 2022-06-22 11:45
 */
public interface MeSource<T> {

    T getMeByUsername(String username);
}
