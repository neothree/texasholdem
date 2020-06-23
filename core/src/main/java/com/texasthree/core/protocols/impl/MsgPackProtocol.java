package com.texasthree.core.protocols.impl;

import com.texasthree.core.app.PlayerSession;
import com.texasthree.core.handlers.netty.DefaultToServerHandler;
import com.texasthree.core.handlers.netty.MsgPackDecoder;
import com.texasthree.core.handlers.netty.MsgPackEncoder;
import com.texasthree.core.protocols.AbstractNettyProtocol;
import com.texasthree.core.util.NettyUtils;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.LengthFieldPrepender;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class MsgPackProtocol extends AbstractNettyProtocol {
    private static final Logger LOG = LoggerFactory.getLogger(MsgPackProtocol.class);

    /**
     * Utility handler provided by netty to add the length of the outgoing
     * message to the message as a header.
     */
    @Autowired
    private LengthFieldPrepender lengthFieldPrepender;

    @Autowired
    private MsgPackDecoder msgPackDecoder;

    @Autowired
    private MsgPackEncoder msgPackEncoder;


    public MsgPackProtocol() {
        super("MSG_PACK_PROTOCOL");
    }

    @Override
    public void applyProtocol(PlayerSession playerSession) {
        LOG.trace("Going to apply {} on session: {}", getProtocolName(),
                playerSession);
        ChannelPipeline pipeline = NettyUtils
                .getPipeLineOfConnection(playerSession);
        NettyUtils.clearPipeline(pipeline);

        // Upstream handlers or encoders (i.e towards server) are added to
        // pipeline now.
        pipeline.addLast("lengthDecoder", createLengthBasedFrameDecoder());
        pipeline.addLast("eventDecoder", msgPackDecoder);
        pipeline.addLast("eventHandler", new DefaultToServerHandler(
                playerSession));

        // Downstream handlers - Filter for data which flows from server to
        // client. Note that the last handler added is actually the first
        // handler for outgoing data.
        pipeline.addLast("lengthFieldPrepender", lengthFieldPrepender);
        pipeline.addLast("eventEncoder", msgPackEncoder);
    }

    public LengthFieldPrepender getLengthFieldPrepender() {
        return lengthFieldPrepender;
    }

    public void setLengthFieldPrepender(LengthFieldPrepender lengthFieldPrepender) {
        this.lengthFieldPrepender = lengthFieldPrepender;
    }

    public MsgPackDecoder getMsgPackDecoder() {
        return msgPackDecoder;
    }

    public void setMsgPackDecoder(MsgPackDecoder msgPackDecoder) {
        this.msgPackDecoder = msgPackDecoder;
    }

    public MsgPackEncoder getMsgPackEncoder() {
        return msgPackEncoder;
    }

    public void setMsgPackEncoder(MsgPackEncoder msgPackEncoder) {
        this.msgPackEncoder = msgPackEncoder;
    }

}
