package com.example.harbour_swer;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

@SpringBootApplication
public class HarbourSwerApplication {

    public static void main(String[] args) {
        System.out.println("Starting application");
        ApplicationContext context = new AnnotationConfigApplicationContext(LibraryConfig.class);
        DataInitializer dataInitializer = context.getBean(DataInitializer.class);
        dataInitializer.initData();

        //SpringApplication.run(HarbourSwerApplication.class, args);
    }

}
