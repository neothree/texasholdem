package com.texasthree.core.handlers.netty;

import com.texasthree.core.event.Events;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
@Sharable
public class EventDecoder extends MessageToMessageDecoder<ByteBuf> {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        int opcode = msg.readUnsignedByte();
        if (Events.LOG_IN == opcode || Events.RECONNECT == opcode) {
            msg.readUnsignedByte();// To read-destroy the protocol version byte.
        }
        ByteBuf buffer = msg.readBytes(msg.readableBytes());
        out.add(Events.event(buffer, opcode));
    }

}
