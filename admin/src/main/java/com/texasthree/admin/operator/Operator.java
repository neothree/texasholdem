package com.texasthree.admin.operator;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Version;
import java.time.LocalDateTime;

/**
 * @author: neo
 * @create: 2022-09-27 18:16
 */
public class Operator {
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
     * 登录名
     */
    @Column(nullable = false, updatable = false)
    private String username;
    /**
     * 状态
     */
    @Column(nullable = false)
    private String status;
    /**
     * 创建时间
     */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createAt;

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreateAt() {
        return createAt;
    }

    public void setCreateAt(LocalDateTime createAt) {
        this.createAt = createAt;
    }
}
