package com.texasthree.room.net;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.texasthree.room.User;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

/**
 * 消息派发器
 *
 * @author: neo
 * @create: 2021-07-09 16:21
 */
@Slf4j
public class MessageDispatcher {

    private Map<String, BiConsumer> messageConsumers = new HashMap<>();

    private Map<String, Class> messageClass = new HashMap<>();

    public void init() {
        var f = new Reflections("com.texasthree.room");
        var set = f.getTypesAnnotatedWith(Controller.class);
        var cmds = set.stream()
                .map(Class::getMethods)
                .flatMap(Arrays::stream)
                .filter(v -> v.isAnnotationPresent(Command.class))
                .collect(Collectors.toList());
        for (var m : cmds) {
            var params = m.getParameterTypes();
            var name = params[0].getSimpleName().toLowerCase();
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

    public void dispatch(Message message) {
        var consumer = this.messageConsumers.get(message.name);
        if (consumer == null) {
            log.error("未知消息 {}", message.name);
            throw new IllegalArgumentException();
        }

        var user = User.getUser(message.uid);
        if (user == null) {
            throw new IllegalStateException("没有找到玩家 uid=" + message.uid);
        }
        try {
            var cmd = new ObjectMapper().readValue(message.name, messageClass.get(message.name));
            consumer.accept(cmd, user);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
