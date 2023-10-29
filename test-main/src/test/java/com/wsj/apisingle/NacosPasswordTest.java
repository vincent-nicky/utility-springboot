package com.wsj.apisingle;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootTest
class NacosPasswordTest {

    @Test
    void nacosdEncode() {
        System.out.println(new BCryptPasswordEncoder().encode("nacos"));
    }
}
