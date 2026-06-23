package com.practice.realtimeplatform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class RealtimePlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(RealtimePlatformApplication.class, args);
    }

}
