package com.wsj.learningredis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class LearningRedisApplication {

    public static void main(String[] args) {
        SpringApplication.run(LearningRedisApplication.class, args);
    }

}
