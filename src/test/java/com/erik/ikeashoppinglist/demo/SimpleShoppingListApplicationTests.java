package com.erik.ikeashoppinglist.demo;

import com.erik.ikeashoppinglist.demo.entity.Customer;
import com.erik.ikeashoppinglist.demo.entity.Item;
import com.erik.ikeashoppinglist.demo.entity.ShoppingList;
import com.erik.ikeashoppinglist.demo.respository.CustomerRepository;
import com.erik.ikeashoppinglist.demo.respository.ItemRepository;
import com.erik.ikeashoppinglist.demo.respository.ShoppingListRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.transaction.Transactional;


@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class SimpleShoppingListApplicationTests {
    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    ShoppingListRepository shoppingListRepository;

    private Customer customerAlice;
    private Customer customerBob;

    private ShoppingList shoppingListDiningRoom;
    private ShoppingList shoppingListBedRoom;

    private Item itemSpork;
    private Item itemChopStick;

    @Before
    public void init(){
        customerAlice = new Customer("Alice");
        customerBob = new Customer("Bob");

        customerRepository.save(customerAlice);
        customerRepository.save(customerBob);

        shoppingListDiningRoom = new ShoppingList("DiningRoom", customerAlice);
        shoppingListBedRoom = new ShoppingList("BedRoom", customerAlice);

        shoppingListRepository.save(shoppingListDiningRoom);
        shoppingListRepository.save(shoppingListBedRoom);

        itemSpork = new Item("123-123-11", "Spork");
        itemChopStick = new Item("123-123-99", "ChopStick");

        itemRepository.save(itemSpork);
        itemRepository.save(itemChopStick);

        shoppingListDiningRoom.addItem(itemSpork, 5);

        customerRepository.flush();
        shoppingListRepository.flush();
        itemRepository.flush();
    }

    @Test
    public void internalSearchTest(){
        
    }

    @Test
    public void contextLoads() {
    }

}


