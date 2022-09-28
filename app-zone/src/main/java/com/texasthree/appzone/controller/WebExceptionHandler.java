package com.texasthree.appzone.controller;


import com.texasthree.utility.restful.RestResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.ServletException;

/**
 * Spring异常拦截器.
 */
@ControllerAdvice
public class WebExceptionHandler {

    private static Logger log = LoggerFactory.getLogger(WebExceptionHandler.class);

    @ExceptionHandler({HttpRequestMethodNotSupportedException.class, HttpMediaTypeNotSupportedException.class})
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public RestResponse processException(ServletException re) {
        log.error("ServletException异常: {} : {}", re.getClass().getName(), re.getMessage());
        return response(1, re.getMessage());
    }

    /**
     * 总异常
     */
    @ExceptionHandler({Exception.class})
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public RestResponse processException(Exception e) {
        e.printStackTrace();
        return RestResponse.UNKNOWN_ERROR;
    }

    private RestResponse response(int code, String msg) {
        var ret = new RestResponse(code, msg);
        log.error("回复 {}", ret);
        return ret;
    }
}
