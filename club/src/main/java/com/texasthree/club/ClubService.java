package com.texasthree.club;

import com.texasthree.dao.Pagination;
import com.texasthree.club.member.ClubMember;
import com.texasthree.club.transaction.ClubTransaction;

import java.math.BigDecimal;

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
     * 俱乐部分页
     */
    Pagination<Club> clubPage(Pagination p);

    /**
     * 添加俱乐部成员
     *
     * @param id   俱乐部id
     * @param uid 新成员
     */
    void member(String id, String uid);

    /**
     * 成员分页
     */
    Pagination<ClubMember> memberPage(String clubId, Pagination p);

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
    ClubTransaction memberToBalance(String id, String member, BigDecimal amount);

    /**
     * 获取俱乐部
     */
    Club getClubById(String id);

    ClubMember getDataByClubIdAndUid(String clubId, String uid);

    /**
     * 平台俱乐部
     */
    Club platform();
}
