package com.texasthree.zone.club;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

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

    @Override
    public String toString() {
        return this.name + ":" + this.id;
    }
}
