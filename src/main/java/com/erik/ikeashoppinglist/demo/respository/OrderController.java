package com.erik.ikeashoppinglist.demo.respository;


import com.erik.ikeashoppinglist.demo.entity.Customer;
import com.erik.ikeashoppinglist.demo.entity.Item;
import com.erik.ikeashoppinglist.demo.entity.ShoppingList;
import com.erik.ikeashoppinglist.demo.entity.ShoppingListItem;
import com.erik.ikeashoppinglist.demo.misc_com_fmt.Amount;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.annotation.*;

import javax.management.openmbean.InvalidKeyException;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@Api(value="ShoppingCart", description = "Operation related to the shopping cart")
public class OrderController {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ShoppingListRepository shoppingListRepository;

    private <T> Specification<T> getSearchSpecification(String search){
        SearchSpecificationBuilder<T> builder = new SearchSpecificationBuilder<T>();
        //%21: !, %26: &, %7C: |
        // matcher splits at the symbols |&,!
        //example: VARNAME:VALUE!VARNAME2<VALUE2
        //Searching for entity matching name VARNAME containing VALUE and VARNAME2 with value less than VALUE2
        Pattern pattern = Pattern.compile("(\\w+?)(:|<|>)(\\w+?)(,|!|&|\\|)", Pattern.UNICODE_CHARACTER_CLASS);
        Matcher matcher = pattern.matcher(search + ",");
        while (matcher.find()) {
            builder.with(matcher.group(1), matcher.group(2), matcher.group(3), matcher.group(4));
        }

        return builder.build();
    }

    @ApiOperation(value="Views a list of all Customers or search")
    @GetMapping("/customer") // Todo templify this somehow...
    public List<Customer> retrieveAllCustomer(@RequestParam(value = "search", required = false) String search){
        List<Customer> customers_data;
        if(search == null){
            customers_data = customerRepository.findAll();

        }else{
            Specification<Customer> spec = getSearchSpecification(search);
            customers_data = customerRepository.findAll(spec);
        }
        return customers_data;
    }

    @ApiOperation(value="Add a Customer to our DB")
    @PostMapping("/customer")
    public ResponseEntity<Object> createCustomer(@Valid @RequestBody Customer customer){
        List<Customer> search_result = customerRepository.findCustomerByName(customer.getName());
        if (search_result.size() > 0) {
            return new ResponseEntity<>("Customer already exist", HttpStatus.BAD_REQUEST);
        }

        Customer newStudent = customerRepository.save(customer);

        return new ResponseEntity<>("Updated table", HttpStatus.CREATED);
    }


    @ApiOperation(value="View a specific Customer <Todo>")
    @GetMapping("/customer/{id}")
    public Customer retrieveCustomer(@PathVariable int id){
        Optional<Customer> user = customerRepository.findById(id);
        if (!user.isPresent()){
            throw new InvalidKeyException("id: " + id);
        }

        Customer customer = user.get();
        return customer;
    }

    @ApiOperation(value="Add a new Shopping list for a customer")
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

    @ApiOperation(value="Views all Shopping List")
    @ApiResponses(value = {
            @ApiResponse(code=200, message = "Here is the lists!"),
            @ApiResponse(code=401, message = "\"security\"")
    })
    @GetMapping("/shopping_list")
    public List<ShoppingList> retrieveAllShoppingList(){
        List<ShoppingList> shoppingLists= shoppingListRepository.findAll();
        return shoppingLists;
    }

    @ApiOperation(value="Views a specific Shopping List")
    @GetMapping("shopping_list/{id}")
    public ShoppingList getShoppingList(@PathVariable int id){
        Optional<ShoppingList> shoppingList = shoppingListRepository.findById(id);
        if (!shoppingList.isPresent()){
            throw new InvalidKeyException("id: " + id);
        }

        return shoppingList.get();
    }


    @DeleteMapping(value = {"/shopping_list/{shopping_id}"})
    @ApiOperation(value="Remove a shopping list form our DB")
    public ResponseEntity<Object> removeShoppingList(@PathVariable int shopping_id){

        Optional<ShoppingList> shoppingList_lookup = shoppingListRepository.findById(shopping_id);
        if (!shoppingList_lookup.isPresent()){
            throw new InvalidKeyException("shopping id: " + shopping_id);
        }

        shoppingListRepository.delete(shoppingList_lookup.get());
        return new ResponseEntity<>("Updated table", HttpStatus.OK);
    }

    @PostMapping(value = {"/shopping_list/{shopping_id}/item/{item_id}"})
    @ApiOperation(value="Add Item to an Inventory")
    public ResponseEntity<Object> updateShoppingList(@RequestParam(value = "amount", required = false) int amount,
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

        if (amount == 0){
            shoppingList_lookup.get().removeItem(item_lookup.get());
            shoppingListRepository.flush();
            return new ResponseEntity<>("Updated table", HttpStatus.CREATED);
        }

        List<ShoppingListItem> ShoppingListItem = shoppingList_lookup.get().getItems();
        for (ShoppingListItem sli: ShoppingListItem){
            if (sli.getItem() == item_lookup.get()){
                sli.setAmount(amount);
                shoppingListRepository.flush();
                return new ResponseEntity<>("Updated table", HttpStatus.CREATED);
            }
        }

        shoppingList_lookup.get().addItem(item_lookup.get(), amount);
        shoppingListRepository.flush();
        return new ResponseEntity<>("Updated table", HttpStatus.CREATED);
    }


    @GetMapping("/item")
    @ApiOperation(value="View a specific item or search")
    public List<Item> retrieveAllItem(@RequestParam(value="search", required = false) String search){
        List<Item> item_data;
        if(search == null){
            item_data = itemRepository.findAll();

        }else{
            Specification<Item> spec = getSearchSpecification(search);
            item_data =  itemRepository.findAll(spec);
        }
        return item_data;
    }

    @PostMapping("/item")
    @ApiOperation(value="Add new Item/Article to our Inventory")
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



}
