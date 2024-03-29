package com.texasthree.appzone;

import com.texasthree.club.ClubService;
import com.texasthree.appzone.room.Room;
import com.texasthree.appzone.room.Scoreboard;
import com.texasthree.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collection;

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
        this.userService.balance(user.getId(), BigDecimal.valueOf(-amount));

        // 增加玩家的房间筹码
        room.buyin(user, amount);
    }

    /**
     * 分配房间利润分成
     * <p>
     * A:
     * 买入：1000
     * 保险：+700
     * 牌局：-900
     * --------------
     * 余额：800
     * 利润：-200
     * <p>
     * B:
     * 买入：1000
     * 保险：-500
     * 牌局：+900
     * --------------
     * 余额：1400
     * 利润：400
     * <p>
     * 结算：
     * <p>
     * 玩家A -200       （利润）
     * 玩家B 380        （400 * 0.95）
     * <p>
     * 玩家A保险 -665    （700 * 0.95）
     * 玩家B保险 475      (500 * 0.95)
     * <p>
     * 玩家A俱乐部返水 10  (200 * 0.05)
     * <p>
     * -200+380-665+475+10=0
     */
    @Transactional(rollbackFor = Exception.class)
    public void share(Collection<Scoreboard> scoreboards) {
        log.info("房间分配利润");
        var feeRate = new BigDecimal("0.05");
        for (var v : scoreboards) {
            var balance = BigDecimal.valueOf(v.getBalance());
            var profit = BigDecimal.valueOf(v.getProfit());
            log.info("{} balance={} profit{}", v.getUid(), balance, profit);
            // 赢家扣除5%的利润
            var fee = v.getProfit() > 0 ? profit.multiply(feeRate) : BigDecimal.ZERO;
            var user = this.userService.balance(v.getUid(), balance.subtract(fee));
            if (v.getProfit() < 0) {
                // 输家将5%加到俱乐部基金
                this.clubService.fund(user.getClubId(), profit.multiply(feeRate).abs());
            }
            var giveIns = BigDecimal.valueOf(v.getInsuranceProfit()).multiply(BigDecimal.ONE.subtract(feeRate));
            this.clubService.fund(user.getClubId(), giveIns.negate());
        }
    }
}
