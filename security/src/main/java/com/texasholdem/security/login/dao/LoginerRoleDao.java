package com.texasholdem.security.login.dao;


import com.texasholdem.security.login.entity.LoginerRole;

import java.util.List;

public interface LoginerRoleDao {

    List<LoginerRole> getAll(String loginerId);
}
