package com.texasthree.zone.club;

import com.texasthree.account.AccountException;
import com.texasthree.account.AccountService;
import com.texasthree.utility.utlis.StringUtils;
import com.texasthree.zone.club.member.MemberData;
import com.texasthree.zone.club.member.MemberDataDao;
import com.texasthree.zone.club.transaction.CTType;
import com.texasthree.zone.club.transaction.ClubTransaction;
import com.texasthree.zone.club.transaction.ClubTransactionDao;
import com.texasthree.zone.club.transaction.Status;
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
class ClubServiceImpl implements ClubService {
    protected final Logger log = LoggerFactory.getLogger(ClubServiceImpl.class);

    private final Map<String, Club> clubMap = new HashMap<>();

    private final ClubDataDao cdao;

    private final MemberDataDao mdao;

    private final ClubTransactionDao ctdao;

    private final AccountService accountService;

    private final UserService userService;

    @Autowired
    public ClubServiceImpl(ClubDataDao cdao,
                           MemberDataDao mdao,
                           ClubTransactionDao ctdao,
                           AccountService accountService,
                           UserService userService) {
        this.cdao = cdao;
        this.mdao = mdao;
        this.ctdao = ctdao;
        this.accountService = accountService;
        this.userService = userService;
    }

    /**
     * 创建俱乐部
     *
     * @param creator 创始人
     * @param name    名称
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
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

    /**
     * 添加俱乐部成员
     *
     * @param id   俱乐部id
     * @param user 新成员
     */
    @Override
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
     *
     * @param id     俱乐部
     * @param amount 金额
     * @return
     */
    @Override
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
     *
     * @param id      俱乐部
     * @param amount  金额
     * @param creator 创建人
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ClubTransaction fundToBalance(String id, BigDecimal amount, String creator) {
        requireGreatZero(amount);
        var data = this.getDataById(id);
        var fund = this.accountService.getDataById(data.getFundId());
        if (fund.getAvailableBalance().compareTo(amount) < 0) {
            throw AccountException.ACCOUNT_SUB_AMOUNT_OUTLIMIT.newInstance("俱乐部基金余额不足");
        }

        // 交易记录
        var trx = new ClubTransaction(id, amount, CTType.FUND, creator);
        trx.setStatus(Status.SUCCESS.name());
        this.ctdao.save(trx);
        this.log.info("俱乐部基金转入到余额 {} amount={}", data, amount);

        // 修改金额
        this.accountService.debit(data.getFundId(), amount, trx.getId());
        this.accountService.credit(data.getBalanceId(), amount, trx.getId());
        return trx;
    }

    /**
     * 发放余额给成员
     *
     * @param id      俱乐部id
     * @param member  成员uid
     * @param amount  金额
     * @param creator 请求创建人
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ClubTransaction balanceToMember(String id, String member, BigDecimal amount, String creator) {
        var club = this.getClubById(id);
        if (!club.getCreator().equals(creator)) {
            throw new IllegalArgumentException("发放余额操作人错误 " + creator);
        }
        requireGreatZero(amount);

        //俱乐部内的成员
        var md = this.mdao.findByClubIdAndUid(id, member);
        if (md.isEmpty()) {
            throw new IllegalArgumentException("发放余额操作人错误，找不到成员 clubId=" + id + " member=" + member);
        }

        // 修改金额
        this.accountService.debit(club.getBalanceId(), amount, StringUtils.get10UUID());
        this.userService.balance(member, amount);

        // 交易记录
        var trx = new ClubTransaction(id, amount, CTType.TO_MEMBER, creator);
        trx.setStatus(Status.SUCCESS.name());
        this.ctdao.save(trx);
        log.info("俱乐部发余额给成员 {} {} {}", id, member, amount);
        return trx;
    }

    /**
     * 成员捐献给余额
     *
     * @param id     俱乐部
     * @param member 成员
     * @param amount 金额
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ClubTransaction memberToBalance(String id, String member, BigDecimal amount) {
        requireGreatZero(amount);
        var md = this.mdao.findByClubIdAndUid(id, member);
        if (md.isEmpty()) {
            throw new IllegalArgumentException("成员捐献给余额，找不到成员 clubId=" + id + " member=" + member);
        }
        var club = this.getClubById(id);
        log.info("俱乐部成员捐献个余额 {} {} {}", id, member, amount);
        this.userService.balance(member, amount.negate());
        this.accountService.credit(club.getBalanceId(), amount, StringUtils.get10UUID());

        // 交易记录
        var trx = new ClubTransaction(id, amount, CTType.FROM_MEMBER, member);
        trx.setStatus(Status.SUCCESS.name());
        this.ctdao.save(trx);
        return trx;
    }

    @Override
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

    @Override
    public MemberData getDataByClubIdAndUid(String clubId, String uid) {
        var data = this.mdao.findByClubIdAndUid(clubId, uid);
        return data.orElse(null);
    }

    private Club platform;

    @Override
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


    @Override
    public ClubTransaction getTrxById(String id) {
        return this.ctdao.findById(id).orElse(null);
    }

    private void requireGreatZero(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException();
        }
    }
}
