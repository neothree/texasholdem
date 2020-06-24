package com.texasthree.room;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


@SuppressWarnings("rawtypes")
public class SessionHandler extends DefaultSessionEventHandler implements GameCommandInterpreter {
    private static final Logger LOG = LoggerFactory.getLogger(SessionHandler.class);
    volatile int cmdCount;

    public SessionHandler(Session session) {
        super(session);
    }

    @Override
    public void onDataIn(Event event) {
        try {
            interpretCommand(event.getSource());
        } catch (InvalidCommandException e) {
            e.printStackTrace();
            LOG.error("{}", e);
        }
    }


    @Override
    public void interpretCommand(Object command) throws InvalidCommandException {
        cmdCount++;
        int type;
        int operation;
        boolean isWebSocketProtocol = false;
        if (command instanceof MessageBuffer) {
            MessageBuffer buf = (MessageBuffer) command;
            type = buf.readInt();
            operation = buf.readInt();
        } else {
            // websocket
            isWebSocketProtocol = true;
            List<Integer> data = (List) command;

            type = data.get(0);
            operation = data.get(1);
        }

        if (isWebSocketProtocol) {
            getSession().onEvent(Events.networkEvent(cmdCount));
        } else if ((cmdCount % 10000) == 0) {
            NettyMessageBuffer buffer = new NettyMessageBuffer();
            //System.out.println("Command No: " + cmdCount);
            buffer.writeInt(cmdCount);
//			Event tcpEvent = Events.dataOutTcpEvent(buffer);
//			getSession().onEvent(tcpEvent);
            NetworkEvent udpEvent = null;
            udpEvent = Events.networkEvent(buffer, DeliveryGuarantyOptions.FAST);
            getSession().onEvent(udpEvent);
        }
    }
}
