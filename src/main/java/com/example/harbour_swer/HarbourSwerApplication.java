package com.example.harbour_swer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class HarbourSwerApplication {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(HarbourSwerApplication.class);
        app.setAdditionalProfiles("local");
        app.run(args);
    }

}
