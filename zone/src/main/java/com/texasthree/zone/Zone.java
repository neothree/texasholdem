package com.texasthree.zone;

import com.texasthree.security.login.enums.LoginApp;
import com.texasthree.security.login.service.LoginerService;
import com.texasthree.utility.utlis.StringUtils;
import com.texasthree.club.ClubService;
import com.texasthree.zone.net.Server;
import com.texasthree.zone.room.Room;
import com.texasthree.user.UserData;
import com.texasthree.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author: neo
 * @create: 2022-08-11 23:28
 */
@EnableAsync
@Service
public class Zone {
    private static Logger log = LoggerFactory.getLogger(Zone.class);

    private final UserService userService;

    private final FundFlow fundFlow;

    private final LoginerService loginerService;

    private final ClubService clubService;

    private final Server server;

    @Autowired
    public Zone(UserService userService,
                ClubService clubService,
                LoginerService loginerService,
                Server server,
                FundFlow fundFlow) {
        this.userService = userService;
        this.loginerService = loginerService;
        this.clubService = clubService;
        this.server = server;
        this.fundFlow = fundFlow;
    }

    @Transactional(rollbackFor = Exception.class)
    public UserData createUser(String username, String password, boolean real) {
        var name = StringUtils.getChineseName();
        this.loginerService.loginer(username, password, LoginApp.USER);
        var club = this.clubService.platform();
        var data = this.userService.user(username, name, real, club.getId());
        return data;
    }

    public void start() {
        log.info("zone 开启启动");
        getRoom();
    }

    private Room room;

    public Room getRoom() {
        if (room == null) {
            room = new Room(StringUtils.get10UUID(), 9, this.fundFlow::share);
            room.setServer(server);

            var user = new User(createUser(StringUtils.get10UUID(), StringUtils.get10UUID(), false));
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
