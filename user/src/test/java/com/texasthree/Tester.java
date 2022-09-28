package com.texasthree;

import com.texasthree.account.AccountException;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author: neo
 * @create: 2022-08-20 14:12
 */
@Service
@SpringBootApplication(scanBasePackages = "com.texasthree")
public class Tester {

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
