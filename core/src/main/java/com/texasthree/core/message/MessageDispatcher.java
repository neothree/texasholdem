package com.texasthree.core.message;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.texasthree.core.app.PlayerSession;
import com.texasthree.core.app.Session;
import com.texasthree.core.app.impl.InvalidCommandException;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by jiangwenping on 17/2/8.
 */
@Service
public class MessageDispatcher {
    private static final Logger LOG = LoggerFactory.getLogger(MessageDispatcher.class);

    private ObjectMapper mapper = new ObjectMapper();


    private final ConcurrentHashMap<String, Method> messageMap = new ConcurrentHashMap<>();

    public void register(String prefix) {
        //包名且不可忘记，不然扫描全部项目包，包括引用的jar
        Reflections reflections = new Reflections(prefix);

        //获取带MessageController注解的类
        Set<Class<?>> typesAnnotatedWith = reflections.getTypesAnnotatedWith(MessageController.class);
        for (Class clazz : typesAnnotatedWith) {
            Method[] methods = clazz.getDeclaredMethods();
            for (Method method : methods) {
                if (Modifier.isPublic(method.getModifiers())) {
                    LOG.info("加载方法: " + method.getName());
                    messageMap.put(method.getName().toLowerCase(), method);
                }
            }
        }
    }

    public void dispatch(String name, String data, Session session) {
        Method method = this.messageMap.get(name.toLowerCase());
        if (method == null) {
            LOG.error("消息未注册: {}", name);
            return;
        }
        try {
            Class cl = method.getParameterTypes()[0];
            Object o = mapper.readValue(data, cl);
            method.invoke(session, o);
        } catch (Exception e) {
            e.printStackTrace();
            LOG.error("消息派发失败 name={} data={}", name, data);
        }
    }
}

