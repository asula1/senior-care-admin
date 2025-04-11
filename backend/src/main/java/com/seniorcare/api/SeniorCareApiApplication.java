package com.seniorcare.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SeniorCareApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(SeniorCareApiApplication.class, args);
    }
}
