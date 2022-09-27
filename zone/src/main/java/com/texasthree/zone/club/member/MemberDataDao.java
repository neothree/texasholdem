package com.texasthree.zone.club.member;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author: neo
 * @create: 2022-09-22 13:12
 */
@Repository
public interface MemberDataDao extends JpaRepository<MemberData, String> {
    Optional<MemberData> findByClubIdAndUid(String clubId, String uid);
}
