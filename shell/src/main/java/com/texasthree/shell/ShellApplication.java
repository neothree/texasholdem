package com.texasthree.shell;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.nadron.client.app.Session;
import io.nadron.client.app.impl.SessionFactory;
import io.nadron.client.communication.DeliveryGuaranty;
import io.nadron.client.communication.NettyMessageBuffer;
import io.nadron.client.event.Event;
import io.nadron.client.event.Events;
import io.nadron.client.event.NetworkEvent;
import io.nadron.client.event.impl.AbstractSessionEventHandler;
import io.nadron.client.util.LoginHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author : neo
 * create at:  2020-06-30  22:54
 * @description:
 */
@SpringBootApplication
public class ShellApplication implements CommandLineRunner {
    private static final Logger LOG = LoggerFactory.getLogger(ShellApplication.class);

    public static void main(String[] args) throws Exception {
        SpringApplication.run(ShellApplication.class, args);
    }

    //access command line arguments
    @Override
    public void run(String... args) throws Exception {
        //do something

        LOG.info("开始启动");
        LoginHelper.LoginBuilder builder = new LoginHelper.LoginBuilder()
                .username("user")
                .password("pass")
                .connectionKey("Room")
                .nadronTcpHostName("localhost").tcpPort(18090);
        LoginHelper helper = builder.build();
        SessionFactory sessionFactory = new SessionFactory(helper);
        Session session = sessionFactory.createAndConnectSession();
        AbstractSessionEventHandler handler = new AbstractSessionEventHandler(session) {
            @Override
            public void onDataIn(Event event) {
                System.out.println("Received event: " + event);
            }
        };
        session.addHandler(handler);
        LOG.info("启动成功");

        Thread.sleep(1000);
        NettyMessageBuffer buffer = new NettyMessageBuffer();
        ObjectMapper mapper = new ObjectMapper();

        buffer.writeString(mapper.writeValueAsString(new Greet()));
        NetworkEvent event = Events.networkEvent(buffer, DeliveryGuaranty.DeliveryGuarantyOptions.RELIABLE);
        session.onEvent(event);
    }

}
