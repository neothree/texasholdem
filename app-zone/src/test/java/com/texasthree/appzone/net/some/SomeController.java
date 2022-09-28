package com.texasthree.appzone.net.some;

import com.texasthree.appzone.User;
import com.texasthree.appzone.net.Command;
import com.texasthree.appzone.net.CommandController;

/**
 * @author: neo
 * @create: 2022-08-09 12:04
 */
@CommandController
public class SomeController {

    @Command
    public static void command(SomeCommand data, User user) {
        var u = (SomeUser) user;
        u.say = data.content;
    }
}
