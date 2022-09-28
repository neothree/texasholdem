package com.texasthree.club;

import com.texasthree.account.AccountException;
import com.texasthree.account.AccountService;
import com.texasthree.dao.Pagination;
import com.texasthree.utility.utlis.StringUtils;
import com.texasthree.club.member.ClubMember;
import com.texasthree.club.member.ClubMemberDao;
import com.texasthree.club.transaction.CTType;
import com.texasthree.club.transaction.ClubTransaction;
import com.texasthree.club.transaction.ClubTransactionDao;
import com.texasthree.club.transaction.Status;
import com.texasthree.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * @author: neo
 * @create: 2022-09-22 13:13
 */
@Service
class ClubServiceImpl implements ClubService {
    protected final Logger log = LoggerFactory.getLogger(ClubServiceImpl.class);

    private final ClubDao cdao;

    private final ClubMemberDao mdao;

    private final ClubTransactionDao ctdao;

    private final AccountService accountService;

    private final UserService userService;

    @Autowired
    public ClubServiceImpl(ClubDao cdao,
                           ClubMemberDao mdao,
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
        // 俱乐部数据
        var balance = this.accountService.account(name + "余额", false);
        var fund = this.accountService.account(name + "基金", true);
        var data = new Club(creator, name, balance.getId(), fund.getId());
        this.cdao.save(data);

        // 成员数据
        var md = new ClubMember(data.getId(), creator);
        this.mdao.save(md);

        log.info("创建俱乐部 creator={} name={}", creator, name);
        return data;
    }

    @Override
    public Pagination<Club> clubPage(Pagination p) {
        var page = this.cdao.findAll(PageRequest.of(p.getNumber(), p.getSize(), Sort.by("createAt").descending()));
        return new Pagination<>(page);
    }

    /**
     * 添加俱乐部成员
     *
     * @param id   俱乐部id
     * @param uid 新成员
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void member(String id, String uid) {
        var club = getClubById(id);
        var md = new ClubMember(id, uid);
        this.mdao.save(md);
        this.userService.club(uid, id);
        log.info("俱乐部添加成员 club={} user={}", club, uid);
    }

    @Override
    public Pagination<ClubMember> memberPage(String clubId, Pagination p) {
        var page = this.mdao.findAllByClubId(clubId, PageRequest.of(p.getNumber(), p.getSize(), Sort.by("createAt").descending()));
        return new Pagination<>(page);
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
        return data;
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
        return getDataById(id);
    }

    private Club getDataById(String id) {
        var data = this.cdao.findById(id);
        if (data.isEmpty()) {
            throw new IllegalArgumentException("无找到俱乐部数据 id=" + id);
        }
        return data.get();
    }

    @Override
    public ClubMember getDataByClubIdAndUid(String clubId, String uid) {
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
