package com.texasthree.account;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author: neo
 * @create: 2020-07-27 10:54
 */
@Service
@SpringBootApplication(scanBasePackages = "com.texasthree")
public class Tester {
    public static void main(String[] args) {
        SpringApplication.run(Tester.class, args);
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
