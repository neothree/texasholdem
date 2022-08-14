package com.texasholdem.security.login.entity;

/**
 * @author: neo
 * @create: 2022-06-28 00:05
 */
public class LoginerRole {
    private String id;
    /**
     * 角色ID
     */
    private String roleId;
    /**
     * 操作员ID
     */
    private String loginerId;

    @Override
    public String toString() {
        return new StringBuilder()
                .append("roleId=").append(roleId)
                .append(", loginerId=").append(loginerId)
                .toString();
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getLoginerId() {
        return loginerId;
    }

    public void setLoginerId(String loginerId) {
        this.loginerId = loginerId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
