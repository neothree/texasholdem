package com.texasthree.zone.token;

import com.texasthree.zone.utility.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;

/**
 * @author: neo
 * @create: 2022-08-14 10:05
 */
@Component
public class AuthorizationInterceptor implements HandlerInterceptor {
    @Autowired
    private TokenService tokenService;

    public final static String USER_KEY = "userId";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Login annotation;
        // 如果处理对象是一个处理方法，则获取到方法上的注解
        if (handler instanceof HandlerMethod) {
            annotation = ((HandlerMethod) handler).getMethodAnnotation(Login.class);
            // 否则直接放过拦截的请求
        } else {
            return true;
        }
        // 说明此方法没有Login注解
        if (annotation == null) {
            return true;
        }

        // 从请求头获取token
        var token = request.getHeader("token");

        // 如果请求头没有token,则从请求参数中取
        if (StringUtils.isEmpty(token)) {
            token = request.getParameter("token");
        }
        // 如果还是没有token,则抛异常
        if (StringUtils.isEmpty(token)) {
            throw new IllegalArgumentException("token null");

        }

        // 查询token信息
        var tokenEntity = tokenService.queryByToken(token);

        // 如果token信息是否为null或是否过期，则抛异常
        if (tokenEntity == null || tokenEntity.getExpireAt().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("token expire");
        }

        // 否则，存入request作用域,后续根据userId，获取用户信息
        request.setAttribute(USER_KEY, tokenEntity.getUid());
        return true;
    }
}
