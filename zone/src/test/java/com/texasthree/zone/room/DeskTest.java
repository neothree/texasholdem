package com.texasthree.zone.room;

import com.texasthree.zone.user.User;
import com.texasthree.zone.user.UserData;
import com.texasthree.zone.utility.StringUtils;
import org.junit.jupiter.api.Test;

class DeskTest {


    @Test
    void start() throws Exception {
        var desk = new Desk();
        var u1 = createUser();
        var u2 = createUser();
        desk.sitDown(u1, 0);
        desk.sitDown(u2, 1);
    }

    private User createUser() {
        var data = new UserData(StringUtils.get10UUID(), StringUtils.get10UUID());
        return new User(data);
    }
}