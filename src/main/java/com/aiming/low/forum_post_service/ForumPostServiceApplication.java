package com.aiming.low.forum_post_service;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients
@MapperScan("com.aiming.low.forum_post_service.dao")
public class ForumPostServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ForumPostServiceApplication.class, args);
    }

}
