package com.texasthree.zone.net;

import com.texasthree.zone.packet.Packet;
import com.texasthree.zone.packet.PacketHandler;
import com.texasthree.zone.utility.JSONUtils;
import com.texasthree.zone.utility.StringUtils;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

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
@Service
public class PacketDispatcher {

    private static Logger log = LoggerFactory.getLogger(PacketDispatcher.class);

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
    public void dispatch(Packet packet) {
        var uid = packet.parse();
        var name = packet.getName();
        var consumer = this.messageConsumers.get(name.toLowerCase());
        if (consumer == null) {
            log.error("未知消息 {}", name);
            return;
        }

        var who = this.func.apply(uid);
        if (who == null) {
            log.error("没有找到玩家 uid={}" + uid);
            return;
        }
        try {
            var cmd = JSONUtils.readValue(name, messageClass.get(name));
            consumer.accept(cmd, who);
        } catch (Exception e) {
            log.error("消息派发异常 message={} user={} reason={}", name, who, e.getMessage());
        }
    }
}
