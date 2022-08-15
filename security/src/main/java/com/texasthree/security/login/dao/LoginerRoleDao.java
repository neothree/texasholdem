package com.texasthree.security.login.dao;


import com.texasthree.security.login.entity.LoginerRole;

import java.util.List;

public interface LoginerRoleDao {

    List<LoginerRole> getAll(String loginerId);
}
