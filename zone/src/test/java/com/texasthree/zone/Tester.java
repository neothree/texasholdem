package com.texasthree.zone;

import com.texasthree.account.AccountException;
import com.texasthree.utility.utlis.StringUtils;
import com.texasthree.user.UserData;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author: neo
 * @create: 2022-08-20 14:12
 */
public class Tester {

    public static User createUser() {
        var data = new UserData(StringUtils.get10UUID(), StringUtils.get10UUID(), true, StringUtils.get10UUID(), StringUtils.get10UUID());
        data.setId(StringUtils.get10UUID());
        return new User(data);
    }

    public static User createRobot() {
        var data = new UserData(StringUtils.get10UUID(), StringUtils.get10UUID(), false, StringUtils.get10UUID(), StringUtils.get10UUID());
        data.setId(StringUtils.get10UUID());
        return new User(data);
    }


    public static void assertException(Runnable func, Class biz) throws Exception {
        try {
            func.run();
            assertTrue(false);
        } catch (Exception e) {
            assertEquals(biz, e.getClass());
        }
    }

    public static void assertException(Runnable func, AccountException biz) throws Exception {
        try {
            func.run();
            assertTrue(false);
        } catch (AccountException e) {
            assertEquals(biz.getCode(), e.getCode());
        }
    }
}
