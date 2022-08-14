package com.texasholdem.security.login.dao;


import com.texasholdem.security.login.entity.Loginer;

import java.util.List;

public interface LoginDao {

    List<Loginer> getLoginersByRoleId(String roleId);

}
