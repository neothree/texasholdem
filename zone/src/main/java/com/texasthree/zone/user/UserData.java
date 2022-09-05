package com.texasthree.zone.user;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * @author: neo
 * @create: 2022-08-16 10:18
 */
@Entity
@Table(name = "user_data_info")
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
    @Column(nullable = false, updatable = false)
    private String username;

    /**
     * 名称
     */
    @Column(nullable = false, updatable = false)
    private String name;

    @Column(nullable = false, updatable = false)
    private boolean real;

    /**
     * 头像
     */
    private String avatar;

    public UserData() {
    }

    public UserData(String username, String name, boolean real) {
        this.username = username;
        this.name = name;
        this.real = real;
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

    @Override
    public String toString() {
        return this.name + ":" + this.id;
    }
}
