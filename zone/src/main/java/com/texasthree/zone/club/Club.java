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

    private ClubData data;

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

    public String getName() {
        return this.data.getName();
    }

    public String getCreator() {
        return this.data.getCreator();
    }

    public String getAvatar() {
        return this.data.getAvatar();
    }

    public int getCapacity() {
        return this.data.getCapacity();
    }

    public int getBalance() {
        return this.data.getBalance();
    }

    public int getFund() {
        return this.data.getFund();
    }

    void setData(ClubData data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return this.data.toString();
    }
}
