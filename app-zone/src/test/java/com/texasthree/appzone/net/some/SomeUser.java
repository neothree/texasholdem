package com.texasthree.appzone.net.some;

import com.texasthree.appzone.User;
import com.texasthree.user.UserData;

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
