package com.texasthree.room.net;

/**
 * @author: neo
 * @create: 2021-07-09 16:25
 */
public class Message {
    public final String uid;
    public final String name;
    public final String data;

    public Message(String uid, String name, String data) {
        this.uid = uid;
        this.name = name;
        this.data = data;
    }
}
