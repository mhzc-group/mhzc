package com.beauty.mhzc.wx;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.beauty.mhzc.db", "com.beauty.mhzc.wx"})
public class MhzcWxApplication {

    public static void main(String[] args) {
        SpringApplication.run(MhzcWxApplication.class, args);
    }

}
