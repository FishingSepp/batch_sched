package com.example.job;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = {"com.example"})
@EnableScheduling
public class JobApplication {

    public static void main(String[] args) {

        SpringApplication.run(JobApplication.class, args);

    }

}
