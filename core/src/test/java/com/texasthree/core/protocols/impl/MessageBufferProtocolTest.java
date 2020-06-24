package com.texasthree.core.protocols.impl;


import com.texasthree.core.handlers.netty.MessageBufferEventDecoder;
import com.texasthree.core.handlers.netty.MessageBufferEventEncoder;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * MessageBufferProtocol Tester.
 *
 * @author <Authors name>
 * @version 1.0
 * @since <pre>Jun 24, 2020</pre>
 */
public class MessageBufferProtocolTest {
    private static MessageBufferProtocol messageBufferProtocol;
    private static LengthFieldBasedFrameDecoder frameDecoder;

    @BeforeAll
    public static void setUp()
    {
        messageBufferProtocol = new MessageBufferProtocol();
        messageBufferProtocol.setLengthFieldPrepender(new LengthFieldPrepender(2, false));
        messageBufferProtocol.setMessageBufferEventDecoder(new MessageBufferEventDecoder());
        messageBufferProtocol.setMessageBufferEventEncoder(new MessageBufferEventEncoder());
        frameDecoder = messageBufferProtocol.createLengthBasedFrameDecoder();
    }

    @Test
    public void verifyEventEncodingAndDecoding() throws InterruptedException
    {
//		EmbeddedMessageChannel decoder = new EmbeddedMessageChannel(frameDecoder,
//				messageBufferProtocol.getMessageBufferEventDecoder(),
//				messageBufferProtocol.getLengthFieldPrepender(),
//				messageBufferProtocol.getMessageBufferEventEncoder());
//		NettyMessageBuffer payload = new NettyMessageBuffer();
//		payload.writeStrings("user","pass","TestRoom1");
//		Event event = Events.event(payload, Events.LOG_IN);
//		encoder.offer(event);
//		ChannelBuffer encoded = encoder.peek();
//
//		Thread.sleep(100);// so that timestamps will differ.
//		decoder.offer(encoded);
//		Event decoded = decoder.peek();
//		assertEquals(decoded.getType(),Events.LOG_IN);
//		assertFalse("Timestamps should not be same",decoded.getTimeStamp() == event.getTimeStamp());
//		NettyMessageBuffer decodedPayload = (NettyMessageBuffer)decoded.getSource();
//		assertEquals("user",decodedPayload.readString());
//		assertEquals("pass",decodedPayload.readString());
//		assertEquals("TestRoom1",decodedPayload.readString());
    }

} 
