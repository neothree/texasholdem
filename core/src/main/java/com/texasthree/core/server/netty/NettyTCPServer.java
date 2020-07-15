package com.texasthree.core.server.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.util.Map;
import java.util.Set;

/**
 * This class is used for TCP IP communications with client. It uses Netty tcp
 * server bootstrap for this.
 *
 * @author Abraham Menacherry
 */
@Service
public class NettyTCPServer extends AbstractNettyServer {
    private static final Logger LOG = LoggerFactory.getLogger(NettyTCPServer.class);

    private ServerBootstrap serverBootstrap;

    public NettyTCPServer(@Qualifier("tcpConfig") NettyConfig nettyConfig,
                          ProtocolMultiplexerChannelInitializer channelInitializer) {
        super(nettyConfig, channelInitializer);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void startServer() throws Exception {
        try {
            serverBootstrap = new ServerBootstrap();
            Map<ChannelOption<?>, Object> channelOptions = nettyConfig.getChannelOptions();
            if (null != channelOptions) {
                Set<ChannelOption<?>> keySet = channelOptions.keySet();
                for (@SuppressWarnings("rawtypes") ChannelOption option : keySet) {
                    serverBootstrap.option(option, channelOptions.get(option));
                }
            }
            serverBootstrap.group(getBossGroup(), getWorkerGroup())
                    .channel(NioServerSocketChannel.class)
                    .childHandler(getChannelInitializer());
            Channel serverChannel = serverBootstrap
                    .bind(nettyConfig.getSocketAddress())
                    .sync()
                    .channel();
            ALL_CHANNELS.add(serverChannel);
        } catch (Exception e) {
            e.printStackTrace();
            LOG.error("TCP Server start error {}, going to shut down", e.getMessage());
            super.stopServer();
            throw e;
        }
    }

    @Override
    public TransmissionProtocol getTransmissionProtocol() {
        return TRANSMISSION_PROTOCOL.TCP;
    }

    @Override
    public void setChannelInitializer(ChannelInitializer<? extends Channel> initializer) {
        this.channelInitializer = initializer;
        serverBootstrap.childHandler(initializer);
    }

    @Override
    public String toString() {
        return "NettyTCPServer [socketAddress=" + nettyConfig.getSocketAddress()
                + ", portNumber=" + nettyConfig.getPortNumber() + "]";
    }

    @Override
    @PreDestroy
    public void stopServer() throws Exception {
        super.stopServer();
    }
}
