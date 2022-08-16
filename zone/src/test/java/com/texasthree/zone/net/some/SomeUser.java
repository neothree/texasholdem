package com.texasthree.zone.net.some;

import com.texasthree.zone.user.User;
import com.texasthree.zone.user.UserData;

/**
 * @author: neo
 * @create: 2022-08-09 12:41
 */
public class SomeUser extends User {
    public String say;

    public SomeUser(UserData data) {
        super(data);
    }
}
