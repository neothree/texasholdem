package com.texasthree.core.handlers.netty;

import com.texasthree.core.concurrent.NamedThreadFactory;
import com.texasthree.core.server.netty.NettyConfig;
import io.netty.channel.ChannelOption;
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

import java.util.HashMap;
import java.util.Map;

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

    @Bean(name = "tcpConfig")
    public NettyConfig tcpConfig(@Qualifier("bossGroup") NioEventLoopGroup bossGroup,
                                      @Qualifier("workerGroup") NioEventLoopGroup workerGroup) {
        NettyConfig config = new NettyConfig();
        config.setBossGroup(bossGroup);
        config.setWorkerGroup(workerGroup);
        Map<ChannelOption<?>, Object> channelOptions = new HashMap<>();
        channelOptions.put(ChannelOption.SO_KEEPALIVE, true);
        channelOptions.put(ChannelOption.SO_BACKLOG, 100);
        config.setChannelOptions(channelOptions);
        return config;
    }

    @Bean(name = "udpConfig")
    public NettyConfig udpConfig(@Qualifier("bossGroup") NioEventLoopGroup bossGroup) {
        NettyConfig config = new NettyConfig();
        config.setBossGroup(bossGroup);
        Map<ChannelOption<?>, Object> channelOptions = new HashMap<>();
        channelOptions.put(ChannelOption.SO_SNDBUF, 65536);
        channelOptions.put(ChannelOption.SO_RCVBUF, 65536);
        channelOptions.put(ChannelOption.SO_BROADCAST, false);
        config.setChannelOptions(channelOptions);
        return config;
    }
}
