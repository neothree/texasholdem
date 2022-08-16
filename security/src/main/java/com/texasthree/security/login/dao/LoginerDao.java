package com.texasthree.security.login.dao;


import com.texasthree.security.login.entity.Loginer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LoginerDao extends JpaRepository<Loginer, String> {

    Optional<Loginer> findByUsername(String username);
}
