package com.texasthree.room;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.texasthree.core.app.GameCommandInterpreter;
import com.texasthree.core.app.Session;
import com.texasthree.core.app.impl.InvalidCommandException;
import com.texasthree.core.communication.DeliveryGuaranty.DeliveryGuarantyOptions;
import com.texasthree.core.communication.MessageBuffer;
import com.texasthree.core.communication.NettyMessageBuffer;
import com.texasthree.core.event.Event;
import com.texasthree.core.event.Events;
import com.texasthree.core.event.NetworkEvent;
import com.texasthree.core.event.impl.DefaultSessionEventHandler;
import org.codehaus.jackson.map.SerializationConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


@SuppressWarnings("rawtypes")
public class SessionHandler extends DefaultSessionEventHandler implements GameCommandInterpreter {
    private static final Logger LOG = LoggerFactory.getLogger(SessionHandler.class);
    volatile int cmdCount;

    ObjectMapper mapper = new ObjectMapper();


    public SessionHandler(Session session) {
        super(session);
    }

    @Override
    public void onDataIn(Event event) {
        try {
            interpretCommand(event.getSource());
        } catch (InvalidCommandException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void interpretCommand(Object command) throws InvalidCommandException {
        cmdCount++;
        MessageBuffer buf = (MessageBuffer) command;
        Cmd.Command cmd;

        try {
            cmd = mapper.readValue(buf.readString(), Cmd.Command.class);
            if (Cmd.CreateRoom.class.getName().equals(cmd.name)) {
                CommandController.createRoom(mapper.readValue(cmd.data, Cmd.CreateRoom.class));
            } else if (Cmd.Sitdown.class.getName().equals(cmd.name)) {
                CommandController.sitdown(mapper.readValue(cmd.data, Cmd.Sitdown.class));
            } else if (Cmd.Sitdown.class.getName().equals(cmd.name)) {
                CommandController.situp(mapper.readValue(cmd.data, Cmd.Situp.class));
            } else if (Cmd.Sitdown.class.getName().equals(cmd.name)) {
                CommandController.startGame(mapper.readValue(cmd.data, Cmd.StartGame.class));
            } else {
                LOG.info("消息错误: {}", buf.readString());
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new InvalidCommandException("消息错误");
        }

    }
}
