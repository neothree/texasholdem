package com.texasthree.shell;


import com.texasthree.proto.Cmd;
import com.texasthree.shell.client.app.Session;
import com.texasthree.shell.client.message.MessageController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@MessageController
public class CommandController {

    private static final Logger LOG = LoggerFactory.getLogger(CommandController.class);

    public void setName(Session ps, Cmd.SetName cmd) throws Exception {
        System.out.println("名称设置成功: " + cmd.name);
        SimpleWriter.write(cmd.name);
    }
}
