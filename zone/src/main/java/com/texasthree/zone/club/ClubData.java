package com.texasthree.zone.club;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author: neo
 * @create: 2022-09-22 13:12
 */
@Entity
public class ClubData {
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
    @Column(nullable = false, updatable = false)
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
     * 基金 - 牌局抽水
     */
    private BigDecimal fund = BigDecimal.ZERO;
    /**
     * 余额
     */
    private BigDecimal balance = BigDecimal.ZERO;

    public ClubData() {
    }

    public ClubData(String creator, String name) {
        this.creator = creator;
        this.name = name;
        this.createAt = LocalDateTime.now();
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

    public BigDecimal getFund() {
        return fund;
    }

    public void setFund(BigDecimal fund) {
        this.fund = fund;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
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
