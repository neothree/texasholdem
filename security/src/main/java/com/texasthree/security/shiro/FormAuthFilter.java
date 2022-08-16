package com.texasthree.security.shiro;

import com.texasthree.utility.restful.RestResponse;
import com.texasthree.utility.utlis.JSONUtils;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.apache.shiro.web.util.WebUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

/**
 * 自定义form表单认证过滤器<br/>
 * 目的是：验证码过滤器发现验证码错误，不需要做认证过滤
 */
public class FormAuthFilter extends FormAuthenticationFilter {

    private final LoginListener loginListener;

    public FormAuthFilter(LoginListener loginListener) {
        this.loginListener = loginListener;
        this.setUsernameParam("username");
        this.setPasswordParam("password");
        this.setRememberMeParam("rememberMe");
        this.setLoginUrl("/login");
        this.setSuccessUrl("/index");
    }

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response, Object mappedValue)
            throws Exception {
        if (request.getAttribute(getFailureKeyAttribute()) != null) {
            return true;
        }

        // 未登录
        var httpServletResponse = (HttpServletResponse) response;
        var result = new RestResponse<>(1, "not authentication");
        httpServletResponse.getWriter().write(JSONUtils.toString(result));
        httpServletResponse.setCharacterEncoding("UTF-8");
        httpServletResponse.setContentType("application/json");
        httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        return false;
    }

    @Override
    protected boolean onLoginSuccess(AuthenticationToken token, Subject subject, ServletRequest request,
                                     ServletResponse response) throws Exception {
        WebUtils.getAndClearSavedRequest(request); // 清理原先地址
        WebUtils.redirectToSavedRequest(request, response, getSuccessUrl());

        var username = (String) token.getPrincipal();
        loginListener.onSuccess(username);
        return false;
    }
}
