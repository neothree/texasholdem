package com.texasthree.shell.client.protocol.impl;

import com.texasthree.shell.client.app.Session;
import com.texasthree.shell.client.handlers.netty.DefaultToClientHandler;
import com.texasthree.shell.client.handlers.netty.EventObjectDecoder;
import com.texasthree.shell.client.handlers.netty.EventObjectEncoder;
import com.texasthree.shell.client.protocol.Protocol;
import com.texasthree.shell.client.util.NettyUtils;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;

public class NettyObjectProtocol implements Protocol {

    public static final com.texasthree.shell.client.protocol.impl.NettyObjectProtocol INSTANCE = new com.texasthree.shell.client.protocol.impl.NettyObjectProtocol();

    @Override
    public void applyProtocol(Session session) {
        ChannelPipeline pipeline = NettyUtils.getPipeLineOfSession(session);
        NettyUtils.clearPipeline(pipeline);
        pipeline.addLast("lengthDecoder", new LengthFieldBasedFrameDecoder(
                Integer.MAX_VALUE, 0, 2, 0, 2));
        pipeline.addLast("eventDecoder", new EventObjectDecoder());
        pipeline.addLast(new DefaultToClientHandler(session));
        pipeline.addLast("lengthFieldPrepender", new LengthFieldPrepender(
                2));
        pipeline.addLast("eventEncoder", new EventObjectEncoder());
    }


}
