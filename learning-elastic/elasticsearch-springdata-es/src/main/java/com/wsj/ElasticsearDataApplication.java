package com.wsj;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@MapperScan("com.wsj.dao.mapper")
public class ElasticsearDataApplication {
    public static void main(String[] args) {
        SpringApplication.run(ElasticsearDataApplication.class, args);
    }
}