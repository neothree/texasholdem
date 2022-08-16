package com.texasthree.zone;

import com.texasthree.security.login.enums.LoginApp;
import com.texasthree.security.login.service.LoginerService;
import com.texasthree.zone.user.User;
import com.texasthree.zone.user.UserService;
import com.texasthree.zone.utility.StringUtils;
import org.springframework.stereotype.Service;

/**
 * @author: neo
 * @create: 2022-08-16 10:53
 */
@Service
public class ZoneService {

    private final UserService userService;

    private final LoginerService loginerService;

    public ZoneService(UserService userService,
                       LoginerService loginerService) {
        this.userService = userService;
        this.loginerService = loginerService;
    }

    public User createUser(String username, String password) {
        var name = StringUtils.getChineseName();
        this.loginerService.loginer(username, password, LoginApp.USER);
        return this.userService.user(username, name);
    }
}
