package com.example.idempotentapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class IdempotentApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(IdempotentApiApplication.class, args);
    }
}
