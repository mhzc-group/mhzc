package com.beauty.mhzc.wx;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication(scanBasePackages = {"com.beauty.mhzc.db"})
@MapperScan("com.beauty.mhzc.db.dao")
@EnableTransactionManagement
@EnableScheduling
@EnableSwagger2
public class MhzcWxApplication {

    public static void main(String[] args) {
        SpringApplication.run(MhzcWxApplication.class, args);
    }

}
