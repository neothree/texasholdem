package com.texasthree.zone.club;

import com.texasthree.account.AccountException;
import com.texasthree.account.AccountService;
import com.texasthree.utility.utlis.StringUtils;
import com.texasthree.zone.club.member.MemberData;
import com.texasthree.zone.club.member.MemberDataDao;
import com.texasthree.zone.user.User;
import com.texasthree.zone.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: neo
 * @create: 2022-09-22 13:13
 */
@Service
public class ClubService {
    protected final Logger log = LoggerFactory.getLogger(ClubService.class);

    private final Map<String, Club> clubMap = new HashMap<>();

    private final ClubDataDao cdao;

    private final MemberDataDao mdao;

    private final AccountService accountService;

    private final UserService userService;

    @Autowired
    public ClubService(ClubDataDao cdao, MemberDataDao mdao, AccountService accountService, UserService userService) {
        this.cdao = cdao;
        this.mdao = mdao;
        this.accountService = accountService;
        this.userService = userService;
    }

    @Transactional
    public Club club(String creator, String name) {
        var balance = this.accountService.account(name + "余额", false);
        var fund = this.accountService.account(name + "基金", true);
        var data = new ClubData(creator, name, balance.getId(), fund.getId());
        this.cdao.save(data);
        var club = new Club(data);
        this.clubMap.put(club.getId(), club);

        log.info("创建俱乐部 creator={} name={}", creator, name);
        return club;
    }

    @Transactional(rollbackFor = Exception.class)
    public void addMember(String id, User user) {
        var club = getClubById(id);
        var md = new MemberData(id, user.getId());
        this.mdao.save(md);
        this.userService.club(user.getId(), id);
        log.info("俱乐部添加成员 club={} user={}", club, user);
    }

    /**
     * 修改基金
     */
    @Transactional(rollbackFor = Exception.class)
    public Club fund(String id, BigDecimal amount) {
        log.info("修改俱乐部基金 {} {}", id, amount);
        var data = this.getDataById(id);
        if (amount.compareTo(BigDecimal.ZERO) > 0) {
            this.accountService.credit(data.getFundId(), amount, StringUtils.get10UUID());
        } else {
            this.accountService.debit(data.getFundId(), amount.abs(), StringUtils.get10UUID(), true);
        }
        return getClubById(id);
    }

    /**
     * 基金转入到余额
     */
    @Transactional(rollbackFor = Exception.class)
    public void fundToBalance(String id, BigDecimal amount) {
        requireGreatZero(amount);
        var data = this.getDataById(id);
        var fund = this.accountService.getDataById(data.getFundId());
        if (fund.getAvailableBalance().compareTo(amount) < 0) {
            throw AccountException.ACCOUNT_SUB_AMOUNT_OUTLIMIT.newInstance("俱乐部基金余额不足");
        }
        this.log.info("俱乐部基金转入到余额 {} amount={}", data, amount);
        this.accountService.debit(data.getFundId(), amount, StringUtils.get10UUID());
        this.accountService.credit(data.getBalanceId(), amount, StringUtils.get10UUID());
    }

    /**
     * 发放余额给成员
     */
    @Transactional(rollbackFor = Exception.class)
    public void balanceToMember(String clubId, String member, BigDecimal amount) {
        requireGreatZero(amount);
        this.mdao.findByClubIdAndUid(clubId, member);
        var club = this.getClubById(clubId);
        log.info("俱乐部发余额给成员 {} {} {}", clubId, member, amount);
        this.accountService.debit(club.getBalanceId(), amount, StringUtils.get10UUID());
        this.userService.balance(member, amount);
    }

    /**
     * 成员捐献给余额
     */
    @Transactional(rollbackFor = Exception.class)
    public void memberToBalance(String clubId, String member, BigDecimal amount) {
        requireGreatZero(amount);
        this.mdao.findByClubIdAndUid(clubId, member);
        var club = this.getClubById(clubId);
        log.info("俱乐部成员捐献个余额 {} {} {}", clubId, member, amount);
        this.userService.balance(member, amount.negate());
        this.accountService.credit(club.getBalanceId(), amount, StringUtils.get10UUID());
    }

    public Club getClubById(String id) {
        return new Club(getDataById(id));
    }

    private ClubData getDataById(String id) {
        var data = this.cdao.findById(id);
        if (data.isEmpty()) {
            throw new IllegalArgumentException("无找到俱乐部数据 id=" + id);
        }
        return data.get();
    }

    public MemberData getDataByClubIdAndUid(String clubId, String uid) {
        var data = this.mdao.findByClubIdAndUid(clubId, uid);
        return data.orElse(null);
    }

    private Club platform;

    public Club platform() {
        if (platform == null) {
            var name = "平台俱乐部";
            var data = this.cdao.findByName(name);
            if (data.isEmpty()) {
                this.platform = this.club("系统", name);
            }
        }
        return this.platform;
    }

    private void requireGreatZero(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException();
        }
    }
}
