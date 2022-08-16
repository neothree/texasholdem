package com.texasthree.zone.net;

import com.texasthree.zone.net.some.SomeCommand;
import com.texasthree.zone.net.some.SomeUser;
import com.texasthree.zone.user.UserData;
import com.texasthree.zone.utility.JSONUtils;
import com.texasthree.zone.utility.StringUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class PacketDispatcherTest {

    @Test
    void start() throws Exception {
        var data = new UserData(StringUtils.get10UUID(), StringUtils.get10UUID());
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
}