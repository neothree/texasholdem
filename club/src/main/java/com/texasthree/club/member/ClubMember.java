package com.texasthree.club.member;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * @author: neo
 * @create: 2022-09-22 13:24
 */
@Entity
@Table
public class ClubMember {
    /**
     * 主键ID
     */
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    private String id;
    /**
     * 版本
     */
    @Version
    private int version = 0;

    @Column(nullable = false, updatable = false)
    private String clubId;

    @Column(nullable = false, updatable = false)
    private String uid;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createAt;

    public ClubMember() {
    }

    public ClubMember(String clubId, String uid) {
        this.clubId = clubId;
        this.uid = uid;
        this.createAt = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public LocalDateTime getCreateAt() {
        return createAt;
    }

    public void setCreateAt(LocalDateTime createAt) {
        this.createAt = createAt;
    }

    public String getClubId() {
        return clubId;
    }

    public void setClubId(String clubId) {
        this.clubId = clubId;
    }

    @Override
    public String toString() {
        return new StringBuilder()
                .append(", id=").append(id)
                .append(", uid=").append(uid)
                .append(", createAt=").append(createAt)
                .toString();
    }
}
