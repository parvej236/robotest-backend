package com.robotest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class RobotestBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(RobotestBackendApplication.class, args);
    }
}
