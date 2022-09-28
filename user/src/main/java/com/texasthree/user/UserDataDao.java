package com.texasthree.user;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserDataDao extends JpaRepository<UserData, String> {

    Optional<UserData> findByUsername(String username);
}
