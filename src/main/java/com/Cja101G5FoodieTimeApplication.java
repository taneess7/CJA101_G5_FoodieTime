package com;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class Cja101G5FoodieTimeApplication {

    public static void main(String[] args) {
        SpringApplication.run(Cja101G5FoodieTimeApplication.class, args);
    }

}
