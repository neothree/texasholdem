package com.texasthree.core.protocols.impl;

import com.texasthree.core.app.PlayerSession;
import com.texasthree.core.handlers.netty.NulEncoder;
import com.texasthree.core.protocols.AbstractNettyProtocol;
import com.texasthree.core.util.NettyUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.TooLongFrameException;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Component;

@Component
public class StringProtocol extends AbstractNettyProtocol {
    /**
     * The maximum size of the incoming message in bytes. The
     * {@link DelimiterBasedFrameDecoder} will use this value in order to throw
     * a {@link TooLongFrameException}.
     */
    int frameSize = 4096;
    /**
     * Flash client expects a nul byte 0x00 to be added as the end byte of any
     * communication with it. This encoder will add this nul byte to the end of
     * the message. Could be considered as a message "footer".
     */
    @Autowired
    private NulEncoder nulEncoder;
    /**
     * Used to decode a netty {@link ByteBuf} (actually a byte array) to a
     * string.
     */
    @Autowired
    private StringDecoder stringDecoder;
    /**
     * Used to encode a normal java String to a netty {@link ByteBuf}
     * (actually a byte array).
     */
    @Autowired
    private StringEncoder stringEncoder;

    public StringProtocol() {
        super("STRING_PROTOCOL");
    }

    public StringProtocol(int frameSize, NulEncoder nulEncoder,
                          StringDecoder stringDecoder, StringEncoder stringEncoder) {
        super("STRING_PROTOCOL");
        this.frameSize = frameSize;
        this.nulEncoder = nulEncoder;
        this.stringDecoder = stringDecoder;
        this.stringEncoder = stringEncoder;
    }

    @Override
    public void applyProtocol(PlayerSession playerSession) {
        ChannelPipeline pipeline = NettyUtils
                .getPipeLineOfConnection(playerSession);
        // Upstream handlers or encoders (i.e towards server) are added to
        // pipeline now.
        pipeline.addLast("framer", new DelimiterBasedFrameDecoder(frameSize,
                Delimiters.nulDelimiter()));
        pipeline.addLast("stringDecoder", stringDecoder);

        // Downstream handlers (i.e towards client) are added to pipeline now.
        pipeline.addLast("nulEncoder", nulEncoder);
        pipeline.addLast("stringEncoder", stringEncoder);

    }

    public int getFrameSize() {
        return frameSize;
    }

    @Required
    public void setFrameSize(int frameSize) {
        this.frameSize = frameSize;
    }

    public NulEncoder getNulEncoder() {
        return nulEncoder;
    }

    @Required
    public void setNulEncoder(NulEncoder nulEncoder) {
        this.nulEncoder = nulEncoder;
    }

    public StringDecoder getStringDecoder() {
        return stringDecoder;
    }

    @Required
    public void setStringDecoder(StringDecoder stringDecoder) {
        this.stringDecoder = stringDecoder;
    }

    public StringEncoder getStringEncoder() {
        return stringEncoder;
    }

    @Required
    public void setStringEncoder(StringEncoder stringEncoder) {
        this.stringEncoder = stringEncoder;
    }

}
