package com.texasthree.shell.client.handlers.netty;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.DatagramChannel;

import java.net.InetSocketAddress;

public class UDPPipelineFactory extends ChannelInitializer<DatagramChannel> {
    public static final String EVENT_ENCODER_NAME = "eventEncoder";
    private static UDPUpstreamHandler UDP_UPSTREAM_HANDLER;
    private static com.texasthree.shell.client.handlers.netty.UDPPipelineFactory INSTANCE;
    private static UDPEventEncoder udpEventEncoder;

    static {
        UDP_UPSTREAM_HANDLER = new UDPUpstreamHandler();
    }

    private InetSocketAddress udpServerAddress;

    public UDPPipelineFactory(InetSocketAddress udpServerAddress) {
        this.udpServerAddress = udpServerAddress;
    }

    @Override
    protected void initChannel(DatagramChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(EVENT_ENCODER_NAME, getEventEncoder(udpServerAddress));
        pipeline.addLast("UDPUpstreamHandler", UDP_UPSTREAM_HANDLER);
    }

    public synchronized static com.texasthree.shell.client.handlers.netty.UDPPipelineFactory getInstance(InetSocketAddress udpServerAddress) {
        if (null == INSTANCE) {
            INSTANCE = new com.texasthree.shell.client.handlers.netty.UDPPipelineFactory(udpServerAddress);
        }
        return INSTANCE;
    }

    public synchronized static ChannelHandler getEventEncoder(InetSocketAddress udpServerAddress) {
        if (null == udpEventEncoder) {
            udpEventEncoder = new UDPEventEncoder(udpServerAddress);
        }
        return udpEventEncoder;
    }
}
