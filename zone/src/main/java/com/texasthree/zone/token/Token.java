package com.texasthree.zone.token;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author: neo
 * @create: 2022-08-14 09:58
 */
public class Token implements Serializable {

    private static final long serialVersionUID = 5584132314624077161L;

    public Token() {
    }

    public Token(long uid, String token, LocalDateTime expireAt, LocalDateTime updateAt) {
        this.uid = uid;
        this.token = token;
        this.expireAt = expireAt;
        this.updateAt = updateAt;
    }

    /**
     * 用户ID
     */
    private long uid;
    /**
     * token
     */
    private String token;
    /**
     * 过期时间
     */
    private LocalDateTime expireAt;
    /**
     * 修改时间
     */
    private LocalDateTime updateAt;

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public LocalDateTime getExpireAt() {
        return expireAt;
    }

    public void setExpireAt(LocalDateTime expireAt) {
        this.expireAt = expireAt;
    }

    public LocalDateTime getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(LocalDateTime updateAt) {
        this.updateAt = updateAt;
    }
}
