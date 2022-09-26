package com.texasthree.zone.club.member;

/**
 * 俱乐部成员
 *
 * @author: neo
 * @create: 2022-09-22 13:24
 */
public class Member {

    private final MemberData data;

    public Member(MemberData data) {
        this.data = data;
    }

    public String getId() {
        return this.data.getId();
    }

    public String getUid() {
        return this.data.getUid();
    }

    @Override
    public String toString() {
        return this.data.toString();
    }
}
