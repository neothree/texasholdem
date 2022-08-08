package com.texasthree.zone.net;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 消息派发器
 *
 * @author: neo
 * @create: 2021-07-09 16:21
 */
public class MessageDispatcher {

    private static Logger log = LoggerFactory.getLogger(MessageDispatcher.class);

    private Map<String, BiConsumer> messageConsumers = new HashMap<>();

    private Map<String, Class> messageClass = new HashMap<>();

    private Function<String, Object> func;

    public void register(String path, Function<String, Object> func) {
        this.func = func;

        var f = new Reflections(path);
        var set = f.getTypesAnnotatedWith(Controller.class);
        var cmds = set.stream()
                .map(Class::getMethods)
                .flatMap(Arrays::stream)
                .filter(v -> v.isAnnotationPresent(Command.class))
                .collect(Collectors.toList());
        for (var m : cmds) {
            var params = m.getParameterTypes();
            var name = params[0].getSimpleName().toLowerCase();
            if (messageClass.containsKey(name)) {
                throw new IllegalStateException("消息重复注册 " + params[0].getSimpleName());
            }

            log.debug("注册消息 {}", name);
            messageClass.put(name, params[0]);
            messageConsumers.put(name, (data, user) -> {
                try {
                    m.invoke(data, user);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
        log.info("注册消息注册数量 {}", messageClass.size());
    }

    @SuppressWarnings("unchecked")
    public void dispatch(Message message) {
        var consumer = this.messageConsumers.get(message.name.toLowerCase());
        if (consumer == null) {
            log.error("未知消息 {}", message.name);
            throw new IllegalArgumentException();
        }

        var who = this.func.apply(message.uid);
        if (who == null) {
            throw new IllegalStateException("没有找到玩家 uid=" + message.uid);
        }
        try {
            var cmd = new ObjectMapper().readValue(message.name, messageClass.get(message.name));
            consumer.accept(cmd, who);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
