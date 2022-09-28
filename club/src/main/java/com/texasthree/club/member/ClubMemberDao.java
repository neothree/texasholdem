package com.texasthree.club.member;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author: neo
 * @create: 2022-09-22 13:12
 */
@Repository
public interface ClubMemberDao extends JpaRepository<ClubMember, String> {
    Optional<ClubMember> findByClubIdAndUid(String clubId, String uid);

    Page<ClubMember> findAllByClubId(String clubId, Pageable pageable);
}
