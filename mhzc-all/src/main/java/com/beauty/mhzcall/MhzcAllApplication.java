package com.beauty.mhzcall;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.annotation.CrossOrigin;

@SpringBootApplication(scanBasePackages = {"com.beauty.mhzc"})
@MapperScan("dao")
@EnableTransactionManagement
@EnableScheduling
@CrossOrigin
public class MhzcAllApplication {

    public static void main(String[] args) {
        SpringApplication.run(MhzcAllApplication.class, args);
    }

}
