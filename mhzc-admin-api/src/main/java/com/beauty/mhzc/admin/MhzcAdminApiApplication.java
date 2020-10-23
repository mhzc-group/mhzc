package com.beauty.mhzc.admin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication(scanBasePackages = {"com.beauty.mhzc.db", "com.beauty.mhzc.core",
        "com.beauty.mhzc.admin"})
@MapperScan("com.beauty.mhzc.db.dao")
@EnableTransactionManagement
@EnableScheduling
public class MhzcAdminApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(MhzcAdminApiApplication.class, args);
    }

}
