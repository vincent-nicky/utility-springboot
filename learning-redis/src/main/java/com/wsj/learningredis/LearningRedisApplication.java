package com.wsj.learningredis;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@MapperScan("com.wsj.learningredis.mapper")
@SpringBootApplication
public class LearningRedisApplication {

    public static void main(String[] args) {
        SpringApplication.run(LearningRedisApplication.class, args);
    }

}
