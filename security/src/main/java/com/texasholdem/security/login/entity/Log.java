package com.texasholdem.security.login.entity;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.Map;

/**
 * 日志类-记录用户操作行为
 *
 * @author neo
 */
public class Log extends StringEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 类型
     */
    private String type;
    /**
     * 应用
     */
    private String app;
    /**
     * 请求地址
     */
    private String remoteAddr;
    /**
     * URI
     */
    private String requestUri;
    /**
     * 请求方法
     */
    private String method;
    /**
     * 提交参数
     */
    private String params = "";
    /**
     * 异常
     */
    private String exception;
    /**
     * 持续时间
     */
    private String duration;
    /**
     * 哪个域（存款，提款，收款号，商户....）
     */
    private String field;

    private String username;

    public String getType() {
        return StringUtils.isEmpty(type) ? type : type.trim();
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRemoteAddr() {
        return StringUtils.isBlank(remoteAddr) ? remoteAddr : remoteAddr.trim();
    }

    public void setRemoteAddr(String remoteAddr) {
        this.remoteAddr = remoteAddr;
    }


    public String getRequestUri() {
        return StringUtils.isBlank(requestUri) ? requestUri : requestUri.trim();
    }

    public void setRequestUri(String requestUri) {
        this.requestUri = requestUri;
    }


    public String getMethod() {
        return StringUtils.isBlank(method) ? method : method.trim();
    }

    public void setMethod(String method) {
        this.method = method;
    }


    public String getParams() {
        return StringUtils.isBlank(params) ? params : params.trim();
    }

    public void setParams(String params) {
        this.params = params;
    }

    /**
     * 设置请求参数
     *
     * @param paramMap
     */
    public void setMapToParams(Map<String, String[]> paramMap) {
        if (paramMap == null) {
            return;
        }
        StringBuilder params = new StringBuilder();
        for (Map.Entry<String, String[]> param : ((Map<String, String[]>) paramMap).entrySet()) {
            params.append(("".equals(params.toString()) ? "" : "&") + param.getKey() + "=");
            String paramValue = (param.getValue() != null && param.getValue().length > 0 ? param.getValue()[0] : "");
            params.append(StringUtils.abbreviate(StringUtils.endsWithIgnoreCase(param.getKey(), "password") ? "" : paramValue, 100));
        }
        this.params = params.toString();
    }


    public String getException() {
        return StringUtils.isBlank(exception) ? exception : exception.trim();
    }

    public void setException(String exception) {
        this.exception = exception;
    }

    public String getDuration() {
        return StringUtils.isBlank(duration) ? duration : duration.trim();
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }


    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}