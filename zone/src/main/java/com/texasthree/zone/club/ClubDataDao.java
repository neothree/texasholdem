package com.texasthree.zone.club;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author: neo
 * @create: 2022-09-22 13:12
 */
@Repository
public interface ClubDataDao extends JpaRepository<ClubData, String> {

    Optional<ClubData> findByName(String name);
}
