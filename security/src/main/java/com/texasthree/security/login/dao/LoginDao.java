package com.texasthree.security.login.dao;


import com.texasthree.security.login.entity.Loginer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoginDao extends JpaRepository<Loginer, Integer> {
    Loginer findDataByUsername(String username);
}
