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
import com.texasthree.core.message.MessageDispatcher;
import org.codehaus.jackson.map.SerializationConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


@SuppressWarnings("rawtypes")
public class SessionHandler extends DefaultSessionEventHandler implements GameCommandInterpreter {
    private static final Logger LOG = LoggerFactory.getLogger(SessionHandler.class);

    volatile int cmdCount;

    private ObjectMapper mapper = new ObjectMapper();

    private MessageDispatcher dispatcher;

    public SessionHandler(Session session, MessageDispatcher dispatcher) {
        super(session);
        this.dispatcher = dispatcher;
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
        try {
            Cmd.Command cmd = mapper.readValue(buf.readString(), Cmd.Command.class);
            this.dispatcher.dispatch(cmd.name, cmd.data, this.getSession());
        } catch (Exception e) {
            e.printStackTrace();
            throw new InvalidCommandException("消息错误");
        }
    }
}
