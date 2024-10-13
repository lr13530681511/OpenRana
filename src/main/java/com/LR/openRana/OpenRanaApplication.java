package com.LR.openRana;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;


@SpringBootApplication
@EnableDiscoveryClient
public class OpenRanaApplication {

    public static void main(String[] args) {
        SpringApplication.run(OpenRanaApplication.class, args);
    }

}
