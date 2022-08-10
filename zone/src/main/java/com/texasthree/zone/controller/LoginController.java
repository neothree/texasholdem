package com.texasthree.zone.controller;

import com.texasthree.zone.entity.User;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * 账户登录
 *
 * @author: neo
 * @create: 2022-08-10 10:00
 */
@RestController
public class LoginController {

    private Map<String, User> users = new HashMap<>();

    @RequestMapping(value = "/login", method = POST)
    public String login(@RequestParam String username,
                        @RequestParam String password) {
        if (!users.containsKey(username)) {
            users.put(username, new User());
        }
        var user = users.get(username);
        return user.getToken();
    }
}
