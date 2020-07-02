package com.texasthree.shell;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.texasthree.shell.client.util.LoginHelper;
import com.texasthree.shell.client.app.Session;
import com.texasthree.shell.client.app.impl.SessionFactory;
import com.texasthree.shell.client.communication.DeliveryGuaranty;
import com.texasthree.shell.client.communication.NettyMessageBuffer;
import com.texasthree.shell.client.event.Event;
import com.texasthree.shell.client.event.Events;
import com.texasthree.shell.client.event.NetworkEvent;
import com.texasthree.shell.client.event.impl.AbstractSessionEventHandler;
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
