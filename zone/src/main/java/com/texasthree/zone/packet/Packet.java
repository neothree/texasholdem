package com.texasthree.zone.packet;


import com.texasthree.zone.utility.JSONUtils;
import com.texasthree.zone.utility.StringUtils;

/**
 * 消息包
 */
public class Packet {

    private String name;

    private String payload;

    public Packet() {
    }

    public Packet(String name, String payload) {
        this.name = name;
        this.payload = payload;
    }

    @Override
    public String toString() {
        return encode(name, payload);
    }

    private static String encode(String name, String payload) {
        return String.format("%02d%s%s", 2 + name.length(), name, payload);
    }


    public static String convertAsString(Object obj) {
        var payload = JSONUtils.toString(obj);
        return encode(StringUtils.getLastName(obj.getClass()), payload);
    }

    public static Packet parse(String data) {
        var length = Integer.valueOf(data.substring(0, 2));
        var name = data.substring(2, length);
        var payload = data.substring(length);
        return new Packet(name, payload);
    }

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
}
