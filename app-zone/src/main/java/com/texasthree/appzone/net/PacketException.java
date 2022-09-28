package com.texasthree.appzone.net;

/**
 * @author: neo
 * @create: 2022-08-09 10:24
 */
public class PacketException extends RuntimeException {

    public static final PacketException TOKEN_EXPIRE = new PacketException("Token 过期");

    PacketException(String msg) {
        super(msg);
    }
    public PacketException newInstance(String msg) {
        return new PacketException(msg);
    }
}
