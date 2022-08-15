package com.texasthree.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


/**
 * @author: neo
 * @create: 2021-09-25 18:21
 */
@Service
@SpringBootApplication
public class Tester {
    private static Logger log = LoggerFactory.getLogger(Tester.class);

    public static void main(String[] args) {
        SpringApplication.run(Tester.class, args);
    }

}
