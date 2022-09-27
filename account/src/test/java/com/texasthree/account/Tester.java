package com.texasthree.account;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Service;

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
}
