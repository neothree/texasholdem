package com.texasthree.zone.user;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserDataDao extends JpaRepository<UserData, Integer> {

    Optional<UserData> findByUsername(String username);
}
