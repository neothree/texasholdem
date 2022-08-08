package com.texasthree.zone;

import com.texasthree.zone.net.LifeCircle;
import org.apache.log4j.BasicConfigurator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;

@SpringBootApplication
public class Application {

    public static void main(String[] args) throws Exception {
        BasicConfigurator.configure();
        SpringApplication.run(Application.class, args);
    }

    @Autowired
    private LifeCircle lifeCircle;

    @PreDestroy
    public void onExit() {
        this.lifeCircle.exit();
    }

    @Component
    public class InitBean implements ApplicationListener<ContextRefreshedEvent> {

        @Override
        public void onApplicationEvent(ContextRefreshedEvent event) {
            lifeCircle.start();
        }
    }
}
