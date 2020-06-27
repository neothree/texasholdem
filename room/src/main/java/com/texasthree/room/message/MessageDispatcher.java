package com.texasthree.room.message;


import com.texasthree.RoomApplication;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by jiangwenping on 17/2/8.
 */
@Service
public class MessageDispatcher {
    private static final Logger LOG = LoggerFactory.getLogger(MessageDispatcher.class);

    private final ConcurrentHashMap<String, Object> messageMap = new ConcurrentHashMap<>();

    public void init() {
        //包名且不可忘记，不然扫描全部项目包，包括引用的jar
        Reflections reflections = new Reflections("com.texasthree.room");

        //获取带MessageController注解的类
        Set<Class<?>> typesAnnotatedWith = reflections.getTypesAnnotatedWith(MessageController.class);
        for (Class clazz : typesAnnotatedWith) {
            Method[] methods = clazz.getDeclaredMethods();
            for (Method method : methods) {
                if (!method.isAccessible()) {
                    LOG.info("加载方法: " + method.getName());
                    messageMap.put(method.getName(), method);
                }
            }
        }

        LOG.info("消息加载完成");
    }
}

