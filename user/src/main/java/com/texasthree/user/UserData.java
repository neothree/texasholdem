package com.texasthree.user;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * @author: neo
 * @create: 2022-08-16 10:18
 */
@Entity
public class UserData {
    /**
     * 版本
     */
    @Version
    private int version = 0;
    /**
     * 主键ID
     */
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    private String id;
    /**
     * 登录账户
     */
    @Column(unique = true, nullable = false, updatable = false)
    private String username;
    /**
     * 名称
     */
    @Column(unique = true, nullable = false, updatable = false)
    private String name;
    /**
     * 是否是真实玩家
     */
    @Column(nullable = false, updatable = false)
    private boolean real;
    /**
     * 创建时间
     */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createAt;
    /**
     * 头像
     */
    private String avatar;
    /**
     * 俱乐部
     */
    private String clubId;
    /**
     * 账户
     */
    @Column(nullable = false, updatable = false)
    private String accountId;


    public UserData() {
    }

    public UserData(String username, String name, boolean real, String clubId, String accountId) {
        this.username = username;
        this.name = name;
        this.real = real;
        this.createAt = LocalDateTime.now();
        this.clubId = clubId;
        this.accountId = accountId;
    }


    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public boolean isReal() {
        return real;
    }

    public void setReal(boolean real) {
        this.real = real;
    }

    public LocalDateTime getCreateAt() {
        return createAt;
    }

    public void setCreateAt(LocalDateTime createAt) {
        this.createAt = createAt;
    }

    public String getClubId() {
        return clubId;
    }

    public void setClubId(String clubId) {
        this.clubId = clubId;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    @Override
    public String toString() {
        return this.name + ":" + this.id;
    }
}
