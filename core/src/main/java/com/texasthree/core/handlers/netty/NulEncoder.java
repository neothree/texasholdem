package com.texasthree.core.handlers.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.springframework.stereotype.Component;

@Component
@Sharable
public class NulEncoder extends MessageToByteEncoder<ByteBuf> {

    private static final ByteBuf NULL_BUFFER = Unpooled.wrappedBuffer(new byte[]{0});

    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf msg, ByteBuf out)
            throws Exception {
        out.writeBytes(Unpooled.wrappedBuffer(msg, NULL_BUFFER));
    }

}