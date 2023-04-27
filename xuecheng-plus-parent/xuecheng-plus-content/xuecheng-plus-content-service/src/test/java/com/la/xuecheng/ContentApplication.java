package com.la.xuecheng;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author LA
 * @createDate 2023-03-30-14:21
 * @description
 */
@SpringBootApplication
@EnableFeignClients(basePackages = {"com.la.xuecheng.content.feignClient"})
public class ContentApplication {

    public static void main(String[] args) {
        SpringApplication.run(ContentApplication.class, args);
    }
}
