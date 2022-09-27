package com.texasthree.account;

import com.texasthree.utility.utlis.DateUtils;
import com.texasthree.utility.utlis.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * @author: neo
 * @create: 2020-08-13 22:04
 */
@Service
public class AccountService {
    private final Logger log = LoggerFactory.getLogger(AccountService.class);

    private final AccountDao accountDao;

    private final AccountStatementDao accountStatementDao;

    @Autowired
    public AccountService(AccountDao accountDao,
                          AccountStatementDao accountStatementDao) {
        this.accountDao = accountDao;
        this.accountStatementDao = accountStatementDao;
    }

    /**
     * 注册账户
     *
     * @param name 名称
     * @return 账户
     */
    @Transactional(rollbackFor = Exception.class)
    public Account account(String name, boolean enableNegative) {
        var account = new Account(name, enableNegative);
        this.accountDao.save(account);
        log.info("注册新账户 {} ", account.toString());
        return account;
    }

    /**
     * 加款
     *
     * @param accountId 用户编号
     * @param requestNo 请求号
     * @return 账户
     */
    @Transactional(rollbackFor = Exception.class)
    public Account credit(String accountId, BigDecimal amount, String requestNo) {
        if (StringUtils.isEmpty(requestNo)) {
            throw new IllegalArgumentException();
        }
        var account = this.checkAccountArgs(accountId, amount);
        if (drop(amount)) {
            return account;
        }

        var statement = account.credit(amount, requestNo);
        this.accountDao.save(account);
        this.accountStatementDao.save(statement);

        log.info("账户加款 {}", statement.toString());
        return account;
    }

    private boolean drop(BigDecimal amount) {
        return BigDecimal.ZERO.compareTo(amount) == 0;
    }

    /**
     * 减款
     *
     * @param accountId 用户编号
     * @param requestNo 请求号
     * @return 账户
     */
    @Transactional(rollbackFor = Exception.class)
    public Account debit(String accountId, BigDecimal amount, String requestNo) {
        if (StringUtils.isEmpty(requestNo)) {
            throw new IllegalArgumentException();
        }

        var account = this.checkAccountArgs(accountId, amount);
        if (drop(amount)) {
            return account;
        }
        var statement = account.debit(amount, requestNo);
        this.accountDao.save(account);
        this.accountStatementDao.save(statement);
        // 记录账户历史
        log.info("账户减款 {}", statement);

        return account;
    }

    /**
     * 冻结账户资金
     *
     * @param accountId 用户编号
     * @return 账户
     */
    @Transactional(rollbackFor = Exception.class)
    public Account pending(String accountId, BigDecimal amount) {
        var account = this.checkAccountArgs(accountId, amount);
        if (drop(amount)) {
            return account;
        }

        account.freeze(amount);
        this.accountDao.save(account);

        log.info("账户冻结资金 amount={} {}", amount, account.toString());

        return account;
    }

    /**
     * 解冻资金
     *
     * @param accountId 用户编号
     */
    @Transactional(rollbackFor = Exception.class)
    public Account unpending(String accountId, BigDecimal amount) {
        var account = this.checkAccountArgs(accountId, amount);
        if (drop(amount)) {
            return account;
        }

        account.unfreeze(amount);
        this.accountDao.save(account);

        log.info("账户解冻资金 amount={} {}", amount, account.toString());
        return account;
    }


    /**
     * 解冻金额+减款
     *
     * @param accountId 用户编号
     * @param amount    解冻和减款金额
     * @param requestNo 流水号
     * @return 账户
     */
    @Transactional(rollbackFor = Exception.class)
    public Account unpendingDebit(String accountId, BigDecimal amount, String requestNo) {
        if (StringUtils.isEmpty(requestNo)) {
            throw new IllegalArgumentException();
        }

        var account = this.checkAccountArgs(accountId, amount);
        if (drop(amount)) {
            return account;
        }

        account.unfreeze(amount);
        var statement = account.debit(amount, requestNo);
        this.accountDao.save(account);
        this.accountStatementDao.save(statement);
        log.info("账户解冻并减去资金 {} ", statement);
        return account;
    }

    /**
     * 根据用户编号编号获取账户信息
     *
     * @param accountId 账户编号
     * @return
     */
    private Account getByAccountId_IsPessimist(String accountId) {
        return this.getDataById(accountId);
    }

    private Account checkAccountArgs(String accountId, BigDecimal amount) {
        if (StringUtils.isEmpty(accountId)
                || amount.compareTo(BigDecimal.ZERO) < 0) {
            log.error("参数错误 accountId={} money={}", accountId, amount);
            throw new IllegalArgumentException();
        }
        return this.getByAccountId_IsPessimist(accountId);
    }

    /**
     * 每次从数据库获取account后都必须执行一次，防止 todayIncome, todayExpend 计算错误
     * TODO 测试用例检测
     */
    private void checkToday(Account account) {
        if (account != null && !DateUtils.isSameDayWithToday(account.getEditAt())) {
            account.setTodayExpend(BigDecimal.ZERO);
            account.setTodayIncome(BigDecimal.ZERO);
        }
    }

    public Account getDataById(String id) {
        return this.accountDao.findById(id).get();
    }
}
