package com.texasthree.zone.token;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.UUID;

/**
 * @author: neo
 * @create: 2022-08-14 10:01
 */
@Service
public class TokenService {
    /**
     * 12 小时过期 单位：毫秒
     */
    private final static int EXPIRE = 3600 * 12 * 1000;

    /**
     *  根据请求头的token查询数据库对应的token信息
     * @param token
     * @return
     */
    public Token queryByToken(String token) {
//        return this.getOne(new QueryWrapper<Token>().eq("token", token));
        return null;
    }

    public Token createToken(long uid) {
        // 得到当前时间
        LocalDateTime now = LocalDateTime.now();
        // 根据过期时间加上当前时间，得到token的有效期
        long indate = LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli() + EXPIRE;
        var tokenExpireDateTime = LocalDateTime.ofInstant(new Date(indate).toInstant(), ZoneId.systemDefault());
        // 生成token
        var token = generateToken();
        // 创建实体对象
//        Token tokenEntity = Token.builder().expireTime(tokenExpireDateTime).userId(uid).token(token).updateTime(now).build();
//        return tokenEntity;
        return null;
    }

    /**
     *  生成token
     * @return
     */
    private String generateToken() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public void expireToken(long userId) {
        // 获取当前时间
        LocalDateTime now = LocalDateTime.now();
//        Token tokenEntity = Token.builder().userId(userId).expireTime(now).updateTime(now).build();
//        this.saveOrUpdate(tokenEntity);
    }
}
