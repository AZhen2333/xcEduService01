package com.xuecheng.center;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * @Author Czz
 * @Description 启动类
 * @Date 2019-04-21 11:04
 * @Version 1.0
 */
@EnableEurekaServer // 标识这是一个Eureka服务
@SpringBootApplication
public class GovernCenterApplication {

    public static void main(String[] args) {
        SpringApplication.run(GovernCenterApplication.class, args);
    }
}
