package com.texasthree.club;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author: neo
 * @create: 2022-09-22 13:12
 */
@Entity
public class Club {
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
     * 名称
     */
    @Column(unique = true, nullable = false, updatable = false)
    private String name;
    /**
     * 头像
     */
    private String avatar;
    /**
     * 创建人
     */
    @Column(nullable = false, updatable = false)
    private String creator;
    /**
     * 创建时间
     */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createAt;
    /**
     * 俱乐部成员数量上限
     */
    private int capacity = 100;
    /**
     * 基金账户id - 牌局抽水
     */
    @Column(nullable = false, updatable = false)
    private String fundId;
    /**
     * 余额账户id
     */
    @Column(nullable = false, updatable = false)
    private String balanceId;

    public Club() {
    }

    public Club(String creator, String name, String balanceId, String fundId) {
        this.creator = creator;
        this.name = name;
        this.createAt = LocalDateTime.now();
        this.balanceId = balanceId;
        this.fundId = fundId;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public LocalDateTime getCreateAt() {
        return createAt;
    }

    public void setCreateAt(LocalDateTime createAt) {
        this.createAt = createAt;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public String getFundId() {
        return fundId;
    }

    public void setFundId(String fundId) {
        this.fundId = fundId;
    }

    public String getBalanceId() {
        return balanceId;
    }

    public void setBalanceId(String balanceId) {
        this.balanceId = balanceId;
    }

    @Override
    public String toString() {
        return new StringBuilder()
                .append(", id=").append(id)
                .append(", name=").append(name)
                .append(", creator=").append(creator)
                .append(", createAt=").append(createAt)
                .toString();
    }
}
