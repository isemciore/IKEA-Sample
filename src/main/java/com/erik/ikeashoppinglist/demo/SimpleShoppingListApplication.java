package com.erik.ikeashoppinglist.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class SimpleShoppingListApplication {

    public static void main(String[] args) {
        ApplicationContext applicationContext = SpringApplication.run(SimpleShoppingListApplication.class, args);
    }
}
