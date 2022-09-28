package com.texasthree.zone.club;

import com.texasthree.account.AccountException;
import com.texasthree.account.AccountService;
import com.texasthree.utility.utlis.StringUtils;
import com.texasthree.zone.club.member.MemberData;
import com.texasthree.zone.club.member.MemberDataDao;
import com.texasthree.zone.club.transaction.CTType;
import com.texasthree.zone.club.transaction.ClubTransaction;
import com.texasthree.zone.club.transaction.ClubTransactionDao;
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
public interface ClubService {

    /**
     * 创建俱乐部
     *
     * @param creator 创始人
     * @param name    名称
     * @return
     */
    Club club(String creator, String name);

    /**
     * 添加俱乐部成员
     *
     * @param id   俱乐部id
     * @param user 新成员
     */
    void addMember(String id, User user);

    /**
     * 修改基金
     *
     * @param id     俱乐部
     * @param amount 金额
     * @return
     */
    Club fund(String id, BigDecimal amount);

    /**
     * 基金转入到余额
     *
     * @param id      俱乐部
     * @param amount  金额
     * @param creator 创建人
     */
    ClubTransaction fundToBalance(String id, BigDecimal amount, String creator);

    ClubTransaction getTrxById(String id);

    /**
     * 发放余额给成员
     *
     * @param id      俱乐部id
     * @param member  成员uid
     * @param amount  金额
     * @param creator 请求创建人
     */
    ClubTransaction balanceToMember(String id, String member, BigDecimal amount, String creator);

    /**
     * 成员捐献给余额
     *
     * @param id     俱乐部
     * @param member 成员
     * @param amount 金额
     */
    void memberToBalance(String id, String member, BigDecimal amount);

    /**
     * 获取俱乐部
     */
    Club getClubById(String id);

    MemberData getDataByClubIdAndUid(String clubId, String uid);

    /**
     * 平台俱乐部
     */
    Club platform();
}
