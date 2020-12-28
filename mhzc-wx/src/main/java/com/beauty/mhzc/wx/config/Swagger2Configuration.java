package com.beauty.mhzc.wx.config;

import com.github.xiaoymin.swaggerbootstrapui.annotations.EnableSwaggerBootstrapUI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * swagger在线文档配置<br>
 * 项目启动后可通过地址：http://host:ip/swagger-ui.html 查看在线文档
 */

@Configuration
@EnableSwagger2
@EnableSwaggerBootstrapUI
public class Swagger2Configuration {
    @Bean
    public Docket wxDocket() {

        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("wx")
                .apiInfo(wxInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.beauty.mhzc.wx.controller"))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo wxInfo() {
        return new ApiInfoBuilder()
                .title("mhzc-wx API")
                .description("WX-API")
                .version("1.0")
                .build();
    }
}
