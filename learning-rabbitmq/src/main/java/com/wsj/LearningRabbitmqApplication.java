package com.wsj;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.wsj.mapper")
@SpringBootApplication
public class LearningRabbitmqApplication {

    public static void main(String[] args) {
        SpringApplication.run(LearningRabbitmqApplication.class, args);
    }

}
