package com.texasholdem.security.login.entity;


import com.texasholdem.utility.utlis.StringUtils;

import java.time.LocalDateTime;

/**
 * 基础实体
 */
public class Entity<T> {
    /**
     * 版本
     */
    private int version = 0;
    /**
     * 主键ID
     */
    private T id;

    /**
     * 名称
     */
    private String name;
    /**
     * 状态
     */
    private String status;
    /**
     * 创建者
     */
    private String creater;
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    /**
     * 描述
     */
    private String remark;

    public LocalDateTime getCreateTime() {
        if (this.createTime == null) {
            this.createTime = LocalDateTime.now();
        }
        return this.createTime;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public T getId() {
        return id;
    }

    public void setId(T id) {
        this.id = id;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreater() {
        return creater;
    }

    public void setCreater(String creater) {
        this.creater = creater;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Entity) || StringUtils.isEmpty(id)) {
            return false;
        }
        return this.id.equals(((Entity) o).getId());
    }
}

