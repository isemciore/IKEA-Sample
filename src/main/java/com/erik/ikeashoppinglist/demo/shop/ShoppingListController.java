package com.erik.ikeashoppinglist.demo.shop;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ShoppingListController {
    @GetMapping("/customers")
    public String helloWorld(){
        return "Hello World";
    }

}
