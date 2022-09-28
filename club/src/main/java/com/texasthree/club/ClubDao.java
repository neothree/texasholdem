package com.texasthree.club;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author: neo
 * @create: 2022-09-22 13:12
 */
@Repository
public interface ClubDao extends JpaRepository<Club, String> {

    Optional<Club> findByName(String name);
}
