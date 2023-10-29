package com.wsj.learningthreadpool;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.wsj.learningthreadpool.dao.mapper")
public class LearningThreadPoolApplication {

    public static void main(String[] args) {
        SpringApplication.run(LearningThreadPoolApplication.class, args);
    }

}
