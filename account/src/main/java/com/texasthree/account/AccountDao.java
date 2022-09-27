package com.texasthree.account;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author: neo
 * @create: 2022-09-26 23:06
 */
@Repository
interface AccountDao extends JpaRepository<Account, String> {
}
