package com.texasthree.security.login.dao;


import com.texasthree.security.login.entity.Loginer;

import java.util.List;

public interface LoginDao {

    List<Loginer> getLoginersByRoleId(String roleId);

}
