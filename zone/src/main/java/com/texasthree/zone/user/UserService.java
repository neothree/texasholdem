package com.texasthree.zone.user;

import com.texasthree.account.AccountService;
import com.texasthree.utility.utlis.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * @author: neo
 * @create: 2022-08-14 10:14
 */
@Component
public class UserService {

    protected final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserDataDao userDataDao;

    private final AccountService accountService;

    @Autowired
    public UserService(UserDataDao userDataDao, AccountService accountService) {
        this.userDataDao = userDataDao;
        this.accountService = accountService;
    }

    @Transactional(rollbackFor = Exception.class)
    public User user(String username, String name, boolean real, String cluId) {
        var account = this.accountService.account(name + "账户", false);
        var data = new UserData(username, name, real, cluId, account.getId());
        this.userDataDao.save(data);
        log.info("创建玩家 {} {}", username, username);
        return new User(data);
    }

    @Transactional(rollbackFor = Exception.class)
    public User balance(String id, BigDecimal amount) {
        log.info("修改玩家余额 {} {}", id, amount);
        var data = this.userDataDao.findById(id).get();
        if (amount.compareTo(BigDecimal.ZERO) > 0) {
            this.accountService.credit(data.getAccountId(), amount, StringUtils.get10UUID());
        } else {
            this.accountService.debit(data.getAccountId(), amount.abs(), StringUtils.get10UUID());
        }
        return new User(data);
    }

    @Transactional(rollbackFor = Exception.class)
    public void club(String id, String clubId) {
        var data = this.userDataDao.findById(id).get();
        data.setClubId(clubId);
        this.userDataDao.save(data);
        log.info("修改玩家俱乐部 id={} clubId={}", id, clubId);
    }

    public User getDataById(String id) {
        return new User(this.userDataDao.findById(id).get());
    }

    public User getDataByUsername(String username) {
        var data = this.userDataDao.findByUsername(username).orElse(null);
        if (data == null) {
            return null;
        }
        return new User(data);
    }
}
