package com.texasthree.core.handlers.netty;

import com.texasthree.core.event.Event;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * This encoder will convert an incoming object (mostly expected to be an
 * {@link Event} object) to a {@link TextWebSocketFrame} object. It uses
 * {@link ObjectMapper} from jackson library to do the Object to JSon String
 * encoding.
 *
 * @author Abraham Menacherry
 */
@Component
@Sharable
public class TextWebsocketEncoder extends MessageToMessageEncoder<Event> {

    @Autowired
    private ObjectMapper jackson;

    @Override
    protected void encode(ChannelHandlerContext ctx, Event msg,
                          List<Object> out) throws Exception {
        String json = jackson.writeValueAsString(msg);
        out.add(new TextWebSocketFrame(json));
    }

    public ObjectMapper getJackson() {
        return jackson;
    }

    public void setJackson(ObjectMapper jackson) {
        this.jackson = jackson;
    }

}
