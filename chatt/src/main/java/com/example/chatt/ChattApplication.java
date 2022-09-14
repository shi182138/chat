package com.example.chatt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication
@EnableWebMvc
public class ChattApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChattApplication.class, args);
    }

}
