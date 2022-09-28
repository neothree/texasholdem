package com.texasthree.zone.net;

import com.texasthree.utility.packet.Packet;
import com.texasthree.utility.utlis.JSONUtils;
import com.texasthree.utility.utlis.StringUtils;
import com.texasthree.zone.net.some.SomeCommand;
import com.texasthree.zone.net.some.SomeUser;
import com.texasthree.user.UserData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class PacketDispatcherTest {

    @Test
    void testStart() throws Exception {
        var data = new UserData(StringUtils.get10UUID(), StringUtils.get10UUID(), true, StringUtils.get10UUID(), StringUtils.get10UUID());
        data.setId(1 + "");
        var user = new SomeUser(data);
        assertNull(user.say);
        var dispatcher = new PacketDispatcher(v -> user);
        dispatcher.register("com.texasthree.zone.net.some");

        var sm = new SomeCommand();
        sm.content = StringUtils.get32UUID();
        dispatcher.dispatch(SomeCommand.class.getSimpleName(), JSONUtils.toString(sm), user);
        assertEquals(sm.content, user.say);
    }

    @Test
    void testPacket() throws Exception {
        var packet = new PacketDispatcherTest.CreateRoom();
        packet.name = "123";
        var msg = Packet.convertAsString(packet);
        assertEquals("12CreateRoom{\"name\":\"123\"}", msg);
    }

    private static class CreateRoom {
        public String name;
    }
}