package com.texasthree.appzone.controller;

import com.texasthree.utility.restful.RestResponse;
import com.texasthree.utility.utlis.JSONUtils;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@RestControllerAdvice
public class RestResponseAdvice implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter methodParameter, Class<? extends HttpMessageConverter<?>> aClass) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object o, MethodParameter methodParameter, MediaType mediaType, Class<? extends HttpMessageConverter<?>> aClass, ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {
        try {
            // 如果Controller直接返回String的话，防止SpringBoot是直接返回，需要手动转换成json
            if (o instanceof String) {
                return JSONUtils.toString(new RestResponse<>(o));
            }
        } catch (Exception e) {
            return o;
        }
        if (o instanceof RestResponse) {
            return o;
        }
        return new RestResponse<>(o);
    }
}
