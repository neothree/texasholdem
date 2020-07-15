package com.texasthree.shell;

import com.texasthree.shell.client.app.Session;
import com.texasthree.shell.client.app.impl.SessionFactory;
import com.texasthree.shell.client.communication.NettyMessageBuffer;
import com.texasthree.shell.client.event.Event;
import com.texasthree.shell.client.event.impl.AbstractSessionEventHandler;
import com.texasthree.shell.client.util.LoginHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author : neo
 * create at:  2020-06-30  22:54
 * @description:
 */
public class ShellApplication {
    private static final Logger LOG = LoggerFactory.getLogger(ShellApplication.class);


    public static void main(String[] args) throws Exception {

        LOG.info("开始启动");
        LoginHelper.LoginBuilder builder = new LoginHelper.LoginBuilder()
                .username("user")
                .password("pass")
                .connectionKey("Room")
                .tcpHostName("localhost")
                .tcpPort(18090);
        LoginHelper helper = builder.build();
        SessionFactory sessionFactory = new SessionFactory(helper);
        Session session = sessionFactory.createAndConnectSession();
        AbstractSessionEventHandler handler = new AbstractSessionEventHandler(session) {
            @Override
            public void onDataIn(Event event) {
                NettyMessageBuffer buffer = (NettyMessageBuffer) event.getSource();
                LOG.info("Received event: {} ", buffer.readString());
            }
        };
        session.addHandler(handler);
        LOG.info("启动成功");

        Thread.sleep(1000);

        new Shell(session).start();
    }

}
