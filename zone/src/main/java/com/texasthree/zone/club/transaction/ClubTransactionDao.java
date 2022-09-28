package com.texasthree.zone.club.transaction;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author: neo
 * @create: 2022-09-22 13:12
 */
@Repository
public interface ClubTransactionDao extends JpaRepository<ClubTransaction, String> {

}
