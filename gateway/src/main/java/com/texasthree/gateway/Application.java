package com.texasthree.gateway;

import org.apache.log4j.BasicConfigurator;

/**
 * @author: neo
 * @create: 2021-07-20 16:50
 */
public class Application {
    public static void main(String[] args) throws Exception {
        BasicConfigurator.configure();

        var gateway = new Gateway();
        gateway.start("com.texasthree.gateway.controller");
    }
}
