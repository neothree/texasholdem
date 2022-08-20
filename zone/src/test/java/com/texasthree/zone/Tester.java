package com.texasthree.zone;

import com.texasthree.zone.user.User;
import com.texasthree.zone.user.UserData;
import com.texasthree.zone.utility.StringUtils;

/**
 * @author: neo
 * @create: 2022-08-20 14:12
 */
public class Tester {

    public static User createUser() {
        var data = new UserData(StringUtils.get10UUID(), StringUtils.get10UUID());
        data.setId(StringUtils.get10UUID());
        return new User(data);
    }
}
