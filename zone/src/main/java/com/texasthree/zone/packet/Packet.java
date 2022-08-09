package com.texasthree.zone.packet;

import com.texasthree.zone.utility.JwtUtils;
import com.texasthree.zone.utility.StringUtils;

import java.util.Date;
import java.util.Objects;

/**
 * 消息包
 */
public class Packet {

    private String token;

    private String name;

    private String payload;

    public Packet() {
    }

    public Packet(String name, String payload, String token) {
        this.name = name;
        this.payload = payload;
        this.token = token;
    }

    @Override
    public String toString() {
        return encode(name, payload);
    }

    private static String encode(String name, String payload) {
        return String.format("%02d%s%s", 2 + name.length(), name, payload);
    }


//    public static String convertAsString(Object obj) {
//        var payload = JSONUtils.toString(obj);
//        return encode(StringUtils.getLastName(obj.getClass()), payload);
//    }
//
//    public static Packet parse(String data) {
//        var length = Integer.valueOf(data.substring(0, 2));
//        var name = data.substring(2, length);
//        var payload = data.substring(length);
//        return new Packet(name, payload);
//    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    /**
     * Token过期时间
     */
    private static final long TTL = 30 * 60 * 1000;

    public static String encode(String id) {
        if (StringUtils.isEmpty(id)) {
            throw new IllegalArgumentException(
                    new StringBuilder("构造TokenPayload参数异常")
                            .append("id=").append(id)
                            .toString());
        }
        return JwtUtils.create(id, "ledger", TTL, null);
    }

    public String parse() {
        Objects.requireNonNull(token);

        var claims = JwtUtils.parse(token);
        if (claims == null || claims.getExpiration() == null) {
            throw new PacketException("token验证失败");
        }
        if (claims.getExpiration().compareTo(new Date()) < 0) {
            throw new PacketException("token过期");
        }

        if (StringUtils.isEmpty(claims.getId()) ) {
            throw new IllegalArgumentException(
                    new StringBuilder("解析Token错误")
                            .append("id=").append(claims.getId())
                            .toString());
        }
        return claims.getId();
    }

}
