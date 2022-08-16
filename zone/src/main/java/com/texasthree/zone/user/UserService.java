package com.texasthree.zone.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author: neo
 * @create: 2022-08-14 10:14
 */
@Component
public class UserService {

    protected final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserDataDao userDataDao;

    @Autowired
    public UserService(UserDataDao userDataDao) {
        this.userDataDao = userDataDao;
    }

    public User user(String username, String name) {
        var data = new UserData(username, name);
        this.userDataDao.save(data);
        log.info("创建玩家 {} {}", username, username);
        return new User(data);
    }


    public User getDataByUsername(String username) {
        var data = this.userDataDao.findByUsername(username).orElse(null);
        if (data == null) {
            return null;
        }
        return new User(data);
    }
}
