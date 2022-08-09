package com.texasthree.zone.net.some;

import com.texasthree.zone.entity.User;
import com.texasthree.zone.net.Command;
import com.texasthree.zone.net.Controller;

/**
 * @author: neo
 * @create: 2022-08-09 12:04
 */
@Controller
public class SomeController {

    @Command
    public static void command(SomeCommand data, User user) {
        var u = (SomeUser) user;
        u.say = data.content;
    }
}