package com.erik.ikeashoppinglist.demo;

import com.erik.ikeashoppinglist.demo.authentication.entity.Role;
import com.erik.ikeashoppinglist.demo.authentication.entity.User;
import com.erik.ikeashoppinglist.demo.authentication.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.ArrayList;
import java.util.Arrays;

@SpringBootApplication
public class SimpleShoppingListApplication implements CommandLineRunner {

    private final UserService userService;

    @Autowired
    public SimpleShoppingListApplication(UserService userService){
        this.userService = userService;
    }

    public static void main(String[] args) {
        SpringApplication.run(SimpleShoppingListApplication.class, args);
    }

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Override
    public void run(String... params) {
        User admin = new User();
        admin.setUsername("admin");
        admin.setPassword("admin");
        admin.setRoles(new ArrayList<>(Arrays.asList(Role.ROLE_ADMIN)));

        userService.signup(admin);

        User client = new User();
        client.setUsername("client");
        client.setPassword("client");
        client.setRoles(new ArrayList<>(Arrays.asList(Role.ROLE_CLIENT)));

        userService.signup(client);
    }

}
