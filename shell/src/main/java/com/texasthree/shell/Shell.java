package com.texasthree.shell;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.texasthree.proto.Cmd;
import com.texasthree.shell.client.app.Session;
import com.texasthree.shell.client.communication.DeliveryGuaranty;
import com.texasthree.shell.client.communication.NettyMessageBuffer;
import com.texasthree.shell.client.event.Events;
import com.texasthree.shell.client.event.NetworkEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Shell {
    private static final Logger LOG = LoggerFactory.getLogger(Shell.class);

    static ObjectMapper mapper = new ObjectMapper();

    private String name;
    private Session session;

    public Shell(Session session) {
        this.session = session;
    }

    public void start() throws Exception {
        this.setName();
    }

    private void setName() throws Exception {
        System.out.println("请输入名称");
        this.name = SimpleWriter.write("名称");

        Cmd.SetName setName = new Cmd.SetName();
        setName.name = this.name;

        sendCmd(setName);
    }

    private void sendCmd(Object msg) throws Exception {
        Cmd.Command cmd = new Cmd.Command();
        cmd.name = msg.getClass().getSimpleName();
        cmd.data = mapper.writeValueAsString(msg);
        String send = mapper.writeValueAsString(cmd);
        LOG.info("发送数据 {}", send);

        NettyMessageBuffer buffer = new NettyMessageBuffer();
        buffer.writeString(send);
        NetworkEvent event = Events.networkEvent(buffer, DeliveryGuaranty.DeliveryGuarantyOptions.RELIABLE);
        session.onEvent(event);
    }
}
