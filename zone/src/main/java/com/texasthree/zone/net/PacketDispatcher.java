package com.texasthree.zone.net;

import com.texasthree.utility.packet.Packet;
import com.texasthree.zone.entity.User;
import com.texasthree.zone.utility.JSONUtils;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 消息派发器
 *
 * @author: neo
 * @create: 2021-07-09 16:21
 */
public class PacketDispatcher {

    private static Logger log = LoggerFactory.getLogger(PacketDispatcher.class);

    private Map<String, BiConsumer<Object, User>> messageConsumers = new HashMap<>();

    private Map<String, Class<?>> messageClass = new HashMap<>();

    private Function<String, User> userSource;

    public PacketDispatcher(Function<String, User> userSource) {
        this.userSource = userSource;
    }

    public void register(String path) {
        var f = new Reflections(path);
        var set = f.getTypesAnnotatedWith(CommandController.class);
        var cmds = set.stream()
                .map(Class::getMethods)
                .flatMap(Arrays::stream)
                .filter(v -> v.isAnnotationPresent(Command.class) && Modifier.isStatic(v.getModifiers()))
                .collect(Collectors.toList());
        for (var m : cmds) {
            var params = m.getParameterTypes();
            var name = params[0].getSimpleName();
            if (messageClass.containsKey(name)) {
                throw new IllegalStateException("消息重复注册 " + params[0].getSimpleName());
            }

            messageClass.put(name, params[0]);
            messageConsumers.put(name, (data, user) -> {
                try {
                    m.invoke(null, data, user);
                } catch (Exception e) {
                    log.error("消息调用异常 {} {}", name, m);
                }
            });
        }
        log.info("注册消息注册数量 {}", messageClass.size());
    }

    public void dispatch(Packet packet, User user) {
        if (packet == null || user == null) {
            throw new IllegalArgumentException();
        }
        this.dispatch(packet.getName(), packet.getPayload(), user);
    }

    public void dispatch(String name, String payload, User user) {
        Objects.requireNonNull(name);
        var consumer = this.messageConsumers.get(name);
        if (consumer == null) {
            log.error("消息派发失败，未知的消息名称 {}", name);
            return;
        }

        try {
            var data = JSONUtils.readValue(payload, messageClass.get(name));
            consumer.accept(data, user);
        } catch (Exception e) {
            log.error("消息派发异常 message={} reason={}", name, e.getMessage());
        }
    }
}
