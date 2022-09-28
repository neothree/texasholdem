package com.texasthree.club.transaction;

import com.texasthree.utility.utlis.StringUtils;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * @author: neo
 * @create: 2022-09-23 10:48
 */
@Entity
public class ClubTransaction {
    /**
     * 主键ID
     */
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    private String id;
    /**
     * 版本
     */
    @Version
    private int version = 0;
    /**
     * 俱乐部
     */
    @Column(nullable = false, updatable = false)
    private String clubId;
    /**
     * 金额
     */
    @Column(nullable = false, updatable = false)
    private BigDecimal amount;
    /**
     * 成员
     */
    private String member;
    /**
     * 创建人
     */
    @Column(nullable = false, updatable = false)
    private String creator;
    /**
     * 交易类型
     */
    @Column(nullable = false, updatable = false)
    private String type;
    /**
     * 状态
     */
    private String status;

    public ClubTransaction() {
    }

    public ClubTransaction(String clubId, BigDecimal amount, CTType type, String creator) {
        if (StringUtils.isEmpty(clubId, creator)
                || type == null
                || amount == null || amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("俱乐部交易参数错误");
        }
        this.clubId = clubId;
        this.amount = amount;
        this.type = type.name();
        this.creator = creator;
        this.status = Status.WAITING.name();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getClubId() {
        return clubId;
    }

    public void setClubId(String clubId) {
        this.clubId = clubId;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMember() {
        return member;
    }

    public void setMember(String member) {
        this.member = member;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
