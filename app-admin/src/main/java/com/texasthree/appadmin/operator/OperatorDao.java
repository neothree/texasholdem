package com.texasthree.appadmin.operator;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author: neo
 * @create: 2022-09-27 18:16
 */
@Repository
public interface OperatorDao extends JpaRepository<Operator, String> {
    Optional<Operator> findByUsername(String username);
}
