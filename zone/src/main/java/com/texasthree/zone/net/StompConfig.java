package com.texasthree.zone.net;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;
import org.springframework.web.socket.server.standard.TomcatRequestUpgradeStrategy;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import javax.websocket.ContainerProvider;

@Configuration
@EnableWebSocketMessageBroker
@EnableScheduling
public class StompConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * 添加这个Endpoint，这样在网页中就可以通过websocket连接上服务,
     * 也就是我们配置websocket的服务地址,并且可以指定是否使用socketjs
     *
     * @param registry
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        var upgradeStrategy = new TomcatRequestUpgradeStrategy();
        //允许客户端使用socketJs方式访问，访问点为ws，允许跨域
        registry.addEndpoint("/texas")
                .setAllowedOriginPatterns("*")
                .setHandshakeHandler(new DefaultHandshakeHandler(upgradeStrategy))
                .addInterceptors(new HttpSessionHandshakeInterceptor());
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        //订阅广播 Broker（消息代理）名称
        registry.enableSimpleBroker("/app", "/ping");
    }

    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registration) {
        registration.setSendTimeLimit(15 * 1000);
        registration.setMessageSizeLimit(24 * 64 * 1024);
        registration.setSendBufferSizeLimit(24 * 512 * 1024);
    }

    @Bean
    public ServletServerContainerFactoryBean createServletServerContainerFactoryBean() {
        var container = new ServletServerContainerFactoryBean();

        container.setMaxTextMessageBufferSize(24 * 64 * 1024);
        container.setMaxBinaryMessageBufferSize(24 * 64 * 1024);
        ContainerProvider.getWebSocketContainer().setDefaultMaxTextMessageBufferSize(24 * 64 * 1024);
        return container;
    }
}
