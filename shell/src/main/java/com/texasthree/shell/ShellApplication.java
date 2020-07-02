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

import java.util.Scanner;

/**
 * @author : neo
 * create at:  2020-06-30  22:54
 * @description:
 */
public class ShellApplication {
    private static final Logger LOG = LoggerFactory.getLogger(ShellApplication.class);

    static ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args) throws Exception {

        LOG.info("开始启动");
        LoginHelper.LoginBuilder builder = new LoginHelper.LoginBuilder()
                .username("user")
                .password("pass")
                .connectionKey("Room")
                .nadronTcpHostName("localhost")
                .tcpPort(18090);
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

        while (true) {
            try {
                Scanner scan = new Scanner(System.in);
                String text = scan.nextLine();
                if (text == null || "".equals(text)) {
                    continue;
                }

                LOG.info("输入命令: {}", text);
                NettyMessageBuffer buffer = new NettyMessageBuffer();
                Cmd.Heartbeat heartbeat = new Cmd.Heartbeat();
                heartbeat.timestamp = System.currentTimeMillis();

                Cmd.Command cmd = new Cmd.Command();
                cmd.name = heartbeat.getClass().getSimpleName();
                cmd.data = mapper.writeValueAsString(heartbeat);
                String send = mapper.writeValueAsString(cmd);
                LOG.info("发送数据 {}", send);

                buffer.writeString(send);
                NetworkEvent event = Events.networkEvent(buffer, DeliveryGuaranty.DeliveryGuarantyOptions.RELIABLE);
                session.onEvent(event);
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(1);
            }
        }

    }
}
