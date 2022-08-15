package com.texasthree.security.shiro;

import com.texasthree.security.login.entity.Loginer;
import com.texasthree.utility.restful.RestResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * controller基类
 *
 * @author neo
 */
public abstract class AbstractMeController<T> {

    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    /**
     * 获取当前用户信息
     *
     * @return
     */
    protected T getMe() {
        return LoginerRealm.getMe();
    }

    protected Loginer getLoinger() {
        return LoginerRealm.getLoginer();
    }

    protected RestResponse getSuccess() {
        return new RestResponse(0, "成功");
    }

    protected RestResponse getSuccess(String desc) {
        return new RestResponse(0, desc);
    }

    protected RestResponse getError(String retdesc) {
        return new RestResponse(1, retdesc);
    }

    protected RestResponse getError() {
        return new RestResponse(1, "未知错误");
    }
}
