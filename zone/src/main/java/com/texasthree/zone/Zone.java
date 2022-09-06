package com.texasthree.zone;

import com.texasthree.security.login.enums.LoginApp;
import com.texasthree.security.login.service.LoginerService;
import com.texasthree.utility.utlis.StringUtils;
import com.texasthree.zone.net.Server;
import com.texasthree.zone.room.Room;
import com.texasthree.zone.user.User;
import com.texasthree.zone.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * @author: neo
 * @create: 2022-08-11 23:28
 */
@EnableAsync
@Service
public class Zone {
    private static Logger log = LoggerFactory.getLogger(Zone.class);

    private final UserService userService;

    private final LoginerService loginerService;

    private final Server server;

    @Autowired
    public Zone(UserService userService,
                LoginerService loginerService,
                Server server) {
        this.userService = userService;
        this.loginerService = loginerService;
        this.server = server;
    }


    public User createUser(String username, String password, boolean real) {
        var name = StringUtils.getChineseName();
        this.loginerService.loginer(username, password, LoginApp.USER);
        return this.userService.user(username, name, real);
    }


    public void start() {
        log.info("zone 开启启动");
        getRoom();
    }

    private Room room;

    public Room getRoom() {
        if (room == null) {
            room = new Room(StringUtils.get10UUID(), 9);
            room.setServer(server);

            var user = createUser(StringUtils.get10UUID(), StringUtils.get10UUID(), false);
            user.enter(room);
            room.addUser(user);
            room.sitDown(user, 7);
        }
        return room;
    }

    @Async
    @Scheduled(fixedRate = 100)
    public void loop() {
        for (var v : Room.all()) {
            v.loop();
        }
    }
}
