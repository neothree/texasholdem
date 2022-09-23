package com.texasthree.zone.club;

import com.texasthree.zone.club.member.Member;

import java.util.ArrayList;
import java.util.List;

/**
 * 俱乐部
 *
 * @author: neo
 * @create: 2022-09-22 13:12
 */
public class Club {

    private final ClubData data;

    private List<Member> list = new ArrayList<>();

    public Club(ClubData data) {
        this.data = data;
    }

    public void addMember(Member member) {
        this.list.add(member);
    }

    public String getId() {
        return this.data.getId();
    }

    @Override
    public String toString() {
        return this.data.toString();
    }
}
