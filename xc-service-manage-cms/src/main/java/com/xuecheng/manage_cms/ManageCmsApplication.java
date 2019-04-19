package com.xuecheng.manage_cms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
// 扫面实体类
@EntityScan("com.xuecheng.framework.domain.cms")
// 扫描接口
@ComponentScan(basePackages = {"com.xuecheng.api"})
// 扫描common下所有类
@ComponentScan(basePackages = {"com.xuecheng.framework"})
// 扫描自身所有类
@ComponentScan(basePackages = {"com.xuecheng.manage_cms"})
public class ManageCmsApplication {
    public static void main(String[] args) {
        SpringApplication.run(ManageCmsApplication.class, args);
    }

    /**
     * 配置OkHttp
     *
     * @return
     */
    @Bean
    public static RestTemplate restTemplate() {
        return new RestTemplate(new OkHttp3ClientHttpRequestFactory());
    }
}
