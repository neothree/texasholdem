package com.texasthree.core.protocols.impl;

import com.texasthree.core.app.PlayerSession;
import com.texasthree.core.handlers.netty.DefaultToServerHandler;
import com.texasthree.core.handlers.netty.EventObjectDecoder;
import com.texasthree.core.handlers.netty.EventObjectEncoder;
import com.texasthree.core.protocols.AbstractNettyProtocol;
import com.texasthree.core.util.NettyUtils;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.LengthFieldPrepender;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class NettyObjectProtocol extends AbstractNettyProtocol {

    private static final Logger LOG = LoggerFactory.getLogger(NettyObjectProtocol.class);

    @Autowired
    private LengthFieldPrepender lengthFieldPrepender;

    public NettyObjectProtocol() {
        super("NETTY_OBJECT_PROTOCOL");
    }

    @Override
    public void applyProtocol(PlayerSession playerSession) {
        LOG.trace("Going to apply {} on session: {}", getProtocolName(),
                playerSession);
        ChannelPipeline pipeline = NettyUtils.getPipeLineOfConnection(playerSession);
        NettyUtils.clearPipeline(pipeline);

        // Upstream handlers or encoders (i.e towards server) are added to
        // pipeline now.
        pipeline.addLast("lengthDecoder", createLengthBasedFrameDecoder());
        pipeline.addLast("eventDecoder", new EventObjectDecoder());
        pipeline.addLast("eventHandler", new DefaultToServerHandler(playerSession));

        // Downstream handlers - Filter for data which flows from server to
        // client. Note that the last handler added is actually the first
        // handler for outgoing data.
        pipeline.addLast("lengthFieldPrepender", lengthFieldPrepender);
        pipeline.addLast("eventEncoder", new EventObjectEncoder());
    }

    public LengthFieldPrepender getLengthFieldPrepender() {
        return lengthFieldPrepender;
    }

    public void setLengthFieldPrepender(LengthFieldPrepender lengthFieldPrepender) {
        this.lengthFieldPrepender = lengthFieldPrepender;
    }

}
