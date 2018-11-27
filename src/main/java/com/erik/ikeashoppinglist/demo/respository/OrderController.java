package com.erik.ikeashoppinglist.demo.respository;


import com.erik.ikeashoppinglist.demo.entity.Customer;
import com.erik.ikeashoppinglist.demo.entity.Item;
import com.erik.ikeashoppinglist.demo.entity.ShoppingList;
import com.erik.ikeashoppinglist.demo.entity.ShoppingListItem;
import com.erik.ikeashoppinglist.demo.misc_com_fmt.Amount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.annotation.*;

import javax.management.openmbean.InvalidKeyException;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
public class OrderController {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ShoppingListRepository shoppingListRepository;

    @GetMapping("/shopping_list")
    public List<ShoppingList> retrieveAllShoppingList(){
        List<ShoppingList> shoppingLists= shoppingListRepository.findAll();
        return shoppingLists;
    }


    @GetMapping("/customer")
    public List<Customer> retrieveAllCustomer(){
        List<Customer> customers_data = customerRepository.findAll();
        return customers_data;
    }

    @PostMapping("/customer")
    public ResponseEntity<Object> createStudent(@Valid @RequestBody Customer customer){
        List<Customer> search_result = customerRepository.findCustomerByName(customer.getName());
        if (search_result.size() > 0) {
            return new ResponseEntity<>("Customer already exist", HttpStatus.BAD_REQUEST);
        }

        Customer newStudent = customerRepository.save(customer);

        return new ResponseEntity<>("Updated table", HttpStatus.CREATED);
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


    @PostMapping("/customer/{id}")
    public ResponseEntity<Object> createNewShoppingList(@RequestBody ShoppingList shoppingList,
                                                        @PathVariable int id){
        Optional<Customer> customer_lookup = customerRepository.findById(id);
        if (!customer_lookup.isPresent()){
            throw new InvalidKeyException("id: " + id);
        }

        Customer customer = customer_lookup.get();

        ShoppingList newShoppingList = shoppingListRepository.save(shoppingList);

        customer.addShoppingList(newShoppingList);

        shoppingListRepository.flush();

        return new ResponseEntity<>("Updated table", HttpStatus.CREATED);

    }

    @GetMapping("shopping_list/{id}")
    public ShoppingList getShoppingList(@PathVariable int id){
        Optional<ShoppingList> shoppingList = shoppingListRepository.findById(id);
        if (!shoppingList.isPresent()){
            throw new InvalidKeyException("id: " + id);
        }

        return shoppingList.get();
    }


    @PostMapping("/item")
    public ResponseEntity<Object> createNewShoppingList(@RequestBody Item item){
        String item_id = item.getItemIdentification();  //Sanity check it

        List<Item> search_result = itemRepository.findItemByItemIdentification(item.getItemIdentification());
        if (search_result.size() > 0) {
            return new ResponseEntity<>("Customer already exist", HttpStatus.BAD_REQUEST);
        }
        try {
            Item newItem = itemRepository.save(item);
        } catch (TransactionSystemException e){
            return new ResponseEntity<>("Bad Input", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("Updated table", HttpStatus.CREATED);
    }

    @PostMapping(value = {"/shopping_list/{shopping_id}/item/{item_id}"})
    public ResponseEntity<Object> updateShoppingList(@RequestBody Amount amount,
                                                     @PathVariable int shopping_id,
                                                     @PathVariable int item_id){

        Optional<ShoppingList> shoppingList_lookup = shoppingListRepository.findById(shopping_id);
        if (!shoppingList_lookup.isPresent()){
            throw new InvalidKeyException("shopping id: " + shopping_id);
        }
        Optional<Item> item_lookup = itemRepository.findById(item_id);
        if (!item_lookup.isPresent()){
            throw new InvalidKeyException("shopping id: " + shopping_id);
        }

        List<ShoppingListItem> ShoppingListItem = shoppingList_lookup.get().getItems();
        for (ShoppingListItem sli: ShoppingListItem){
            if (sli.getItem() == item_lookup.get()){
                sli.setAmount(amount.getAmount());
                shoppingListRepository.flush();
                return new ResponseEntity<>("Updated table", HttpStatus.CREATED);
            }
        }

        shoppingList_lookup.get().addItem(item_lookup.get(), amount.getAmount());
        shoppingListRepository.flush();
        return new ResponseEntity<>("Updated table", HttpStatus.CREATED);
    }

    @GetMapping("/item")
    public List<Item> retrieveAllItem(){
        List<Item> item_data= itemRepository.findAll();
        return item_data;
    }


}
