package com.texasthree.core.handlers.netty;

import com.texasthree.core.event.Event;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.util.List;

@Component
@Sharable
public class UDPEventEncoder extends MessageBufferEventEncoder {

    @Override
    protected void encode(ChannelHandlerContext ctx, Event event, List<Object> out) throws Exception {
        ByteBuf data = (ByteBuf) super.encode(ctx, event);
        InetSocketAddress clientAddress = (InetSocketAddress) event.getEventContext().getAttachment();
        out.add(new DatagramPacket(data, clientAddress));
    }

}
