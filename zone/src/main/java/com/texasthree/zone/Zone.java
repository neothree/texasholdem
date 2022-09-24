package com.texasthree.zone;

import com.texasthree.security.login.enums.LoginApp;
import com.texasthree.security.login.service.LoginerService;
import com.texasthree.utility.utlis.StringUtils;
import com.texasthree.zone.club.ClubService;
import com.texasthree.zone.net.Server;
import com.texasthree.zone.room.Buyin;
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

    private final ClubService clubService;

    private final LoginerService loginerService;

    private final Server server;

    @Autowired
    public Zone(UserService userService,
                LoginerService loginerService,
                Server server,
                ClubService clubService) {
        this.userService = userService;
        this.loginerService = loginerService;
        this.server = server;
        this.clubService = clubService;
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
            room = new Room(StringUtils.get10UUID(), 9, this::share);
            room.setServer(server);

            var user = createUser(StringUtils.get10UUID(), StringUtils.get10UUID(), false);
            user.enter(room);
            room.addUser(user);
            room.sitDown(user, 7);
        }
        return room;
    }

    /**
     * 房间利润分成
     */
    public void share(Room room) {
        // 返回玩家余额
        var buyins = room.buyins();
        var win = buyins.stream()
                .filter(v -> v.getProfit() > 0)
                .mapToInt(Buyin::getProfit)
                .sum();
        var lose = buyins.stream()
                .filter(v -> v.getProfit() < 0)
                .mapToInt(Buyin::getProfit)
                .sum();
        log.info("房间结算 win={} lose={} insurance={}", win, lose, room.getInsurance());
        for (var v : buyins) {
            var balance = v.getBalance();
            // 赢家扣除5%的利润
            var give = v.getProfit() > 0 ? (int) (v.getProfit() * 0.05) : 0;
            this.userService.addBalance(v.getUid(), balance - give);
            if (v.getProfit() < 0) {
                // 输家将5%加到俱乐部基金
                var clubId = "11";
                this.clubService.addFund(clubId, give);
            }
        }
    }

    @Async
    @Scheduled(fixedRate = 100)
    public void loop() {
        for (var v : Room.all()) {
            v.loop();
        }
    }
}
