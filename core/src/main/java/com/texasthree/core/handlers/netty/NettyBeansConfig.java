package com.texasthree.core.handlers.netty;

import com.texasthree.core.concurrent.NamedThreadFactory;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.codehaus.jackson.map.ObjectMapper;
import org.msgpack.MessagePack;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NettyBeansConfig {

    @Bean
    public LengthFieldPrepender lengthFieldPrepender() {
        return new LengthFieldPrepender(2, false);
    }

    @Bean
    public StringDecoder stringDecoder() {
        return new StringDecoder();
    }

    @Bean
    public StringEncoder stringEncoder() {
        return new StringEncoder();
    }

    @Bean
    public LengthFieldBasedFrameDecoder lengthFieldBasedFrameDecoder() {
        return new LengthFieldBasedFrameDecoder(4096, 0, 2, 0, 2);
    }

    @Bean
    public ObjectMapper jackson() {
        return new ObjectMapper();
    }

    @Bean
    public MessagePack msgPack() {
        return new MessagePack();
    }

    @Bean(name = "bossGroup", destroyMethod = "shutdownGracefully")
    public NioEventLoopGroup bossGroup(@Qualifier("bossThreadFactory") NamedThreadFactory factory) {
        // TODO 线程数
        return new NioEventLoopGroup(1, factory);
    }

    @Bean(name = "workerGroup", destroyMethod = "shutdownGracefully")
    public NioEventLoopGroup workerGroup(@Qualifier("workerThreadFactory") NamedThreadFactory factory) {
        // TODO 线程数
        return new NioEventLoopGroup(1, factory);
    }

    @Bean(name = "bossThreadFactory")
    public NamedThreadFactory bossThreadFactory() {
        return new NamedThreadFactory("Server-Boss");
    }

    @Bean(name = "workerThreadFactory")
    public NamedThreadFactory workerThreadFactory() {
        return new NamedThreadFactory("Server-Worker");
    }
}
