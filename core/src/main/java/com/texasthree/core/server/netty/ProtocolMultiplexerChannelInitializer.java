package com.texasthree.core.server.netty;

import com.texasthree.core.handlers.netty.LoginProtocol;
import com.texasthree.core.handlers.netty.ProtocolMultiplexerDecoder;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class ProtocolMultiplexerChannelInitializer extends ChannelInitializer<SocketChannel> {
    // TODO make this configurable from spring.
    private static final int MAX_IDLE_SECONDS = 60;

    private int bytesForProtocolCheck = 5;

    @Autowired
    @Qualifier("compositeProtocol")
    private LoginProtocol loginProtocol;

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        // Create a default pipeline implementation.
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast("idleStateCheck", new IdleStateHandler(
                MAX_IDLE_SECONDS, MAX_IDLE_SECONDS, MAX_IDLE_SECONDS));
        pipeline.addLast("multiplexer", createProtcolMultiplexerDecoder());
    }

    protected ChannelHandler createProtcolMultiplexerDecoder() {
        return new ProtocolMultiplexerDecoder(bytesForProtocolCheck, loginProtocol);
    }

    public int getBytesForProtocolCheck() {
        return bytesForProtocolCheck;
    }

    public void setBytesForProtocolCheck(int bytesForProtocolCheck) {
        this.bytesForProtocolCheck = bytesForProtocolCheck;
    }

    public LoginProtocol getLoginProtocol() {
        return loginProtocol;
    }

    public void setLoginProtocol(LoginProtocol loginProtocol) {
        this.loginProtocol = loginProtocol;
    }

}
