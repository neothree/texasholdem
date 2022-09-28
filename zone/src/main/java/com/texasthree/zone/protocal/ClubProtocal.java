package com.texasthree.zone.protocal;

import com.texasthree.club.Club;

/**
 * @author: neo
 * @create: 2022-09-26 15:37
 */
public class ClubProtocal {

    public static class ClubData {
        public String id;
        public String name;
        public String creator;
        public String creatorName;
        public String avatar;
        public int num;
        public int capacity;
        public int fund;
        public int balance;

        public ClubData(Club club) {
            this.id = club.getId();
            this.name = club.getName();
            this.creator = club.getCreator();
            this.creatorName = "张三";
            this.avatar = club.getAvatar();
            this.balance = -1;
            this.fund = -1;
            this.capacity = club.getCapacity();
            this.num = 10;
        }
    }

    public static class Member {
        public String uid;
        public String name;
        public String avatar;

        public Member(com.texasthree.club.member.ClubMember v) {
            this.uid = v.getUid();
            this.name = "张三";
        }
    }
}
