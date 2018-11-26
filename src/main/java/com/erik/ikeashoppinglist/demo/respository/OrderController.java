package com.erik.ikeashoppinglist.demo.respository;


import com.erik.ikeashoppinglist.demo.entity.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.management.openmbean.InvalidKeyException;
import java.util.List;
import java.util.Optional;

@RestController
public class OrderController {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ShoppingListRepository shoppingListRepository;


    @GetMapping("/customer")
    public List<Customer> retrieveAllStudents(){
        List<Customer> customers_data = customerRepository.findAll();
        return customers_data;
    }

    @GetMapping("/customer/{id}")
    public Customer retrieveUser(@PathVariable int id){
        Optional<Customer> user = customerRepository.findById(id);
        if (!user.isPresent()){
            throw new InvalidKeyException("id: " + id);
        }

        Customer customer = user.get();
        return customer;
    }

    // Add customer

    // Add shopping list WITH customer

    //Add item to shopping list

    //Delete List

    //Delete CUstomer

    //Delete Item

    //Update Item with Count

    //Authorization done somewhere else with token sent here

}
