package com.wsj.learningredis;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
@MapperScan("com.wsj.learningredis.mapper")
public class LearningRedisApplication {

    public static void main(String[] args) {
        SpringApplication.run(LearningRedisApplication.class, args);
    }

}
