package com.texasthree.zone;

import com.texasthree.zone.club.ClubService;
import com.texasthree.zone.room.Room;
import com.texasthree.zone.room.Scoreboard;
import com.texasthree.zone.user.User;
import com.texasthree.zone.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 管理所有的资金流动
 *
 * @author: neo
 * @create: 2022-09-25 11:36
 */
@Service
public class FundFlow {

    private static Logger log = LoggerFactory.getLogger(Zone.class);

    private final UserService userService;

    private final ClubService clubService;


    @Autowired
    public FundFlow(UserService userService, ClubService clubService) {
        this.userService = userService;
        this.clubService = clubService;
    }

    /**
     * 房间买入
     */
    @Transactional(rollbackFor = Exception.class)
    public void buyin(Room room, User user, int amount) {
        // 玩家扣除余额
        this.userService.balance(user.getId(), -amount);

        // 增加玩家的房间筹码
        room.buyin(user, amount);
    }

    /**
     * 分配房间利润分成
     */
    @Transactional(rollbackFor = Exception.class)
    public void share(Room room) {
        // 返回玩家余额
        var scoreboards = room.scoreboards();
        var win = scoreboards.stream()
                .filter(v -> v.getGameProfit() > 0)
                .mapToInt(Scoreboard::getGameProfit)
                .sum();
        var lose = scoreboards.stream()
                .filter(v -> v.getGameProfit() < 0)
                .mapToInt(Scoreboard::getGameProfit)
                .sum();
        log.info("房间结算 win={} lose={} insurance={}", win, lose, room.getInsurance());
        for (var v : scoreboards) {
            var balance = v.getBalance();
            // 赢家扣除5%的利润
            var give = v.getGameProfit() > 0 ? (int) (v.getGameProfit() * 0.05) : 0;
            var user = this.userService.balance(v.getUid(), balance - give);
            if (v.getGameProfit() < 0) {
                // 输家将5%加到俱乐部基金
                this.clubService.addFund(user.getClubId(), give);
            }
        }
    }
}
