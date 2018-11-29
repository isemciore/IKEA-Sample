package com.erik.ikeashoppinglist.demo.respository;


import com.erik.ikeashoppinglist.demo.entity.Customer;
import com.erik.ikeashoppinglist.demo.entity.Item;
import com.erik.ikeashoppinglist.demo.entity.ShoppingList;
import com.erik.ikeashoppinglist.demo.entity.ShoppingListItem;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;

import javax.management.openmbean.InvalidKeyException;
import javax.validation.Valid;
import java.util.ArrayList;
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

    private <T, K> List<T> searchForWord(SearchableRepository<T, K> repository, String search){
        Specification<T> spec = getSearchSpecification(search);
        List<T> search_result;
        if (spec == null){
            search_result = new ArrayList<>();
        } else {
            try {
                search_result = repository.findAll(spec);
            } catch (InvalidDataAccessApiUsageException e) {
                search_result = new ArrayList<>();
            }
        }
        return search_result;
    }

    @ApiOperation(value="Views a list of all Customers or search")
    @GetMapping("/customer") // Todo templify this somehow...
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public List<Customer> retrieveAllCustomer(@ApiParam(value="Format: VAR(:<>)VALUE(,!?&) \nExample: name:Erik&id<20", name="search")
                                                  @RequestParam(value = "search", required = false) String search){
        List<Customer> customers_data;
        if(search == null){
            customers_data = customerRepository.findAll();

        }else{
            customers_data = searchForWord(customerRepository, search);
        }
        return customers_data;
    }

    @ApiOperation(value="Add a Customer to our DB")
    @PostMapping("/customer")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
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
    @PreAuthorize("hasRole('ROLE_ADMIN')")
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
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_CLIENT')")
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
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_CLIENT')")
    public List<ShoppingList> retrieveAllShoppingList(){
        List<ShoppingList> shoppingLists= shoppingListRepository.findAll();
        return shoppingLists;
    }

    @ApiOperation(value="Views a specific Shopping List")
    @GetMapping("shopping_list/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_CLIENT')")
    public ShoppingList getShoppingList(@PathVariable int id){
        Optional<ShoppingList> shoppingList = shoppingListRepository.findById(id);
        if (!shoppingList.isPresent()){
            throw new InvalidKeyException("id: " + id);
        }

        return shoppingList.get();
    }


    @DeleteMapping(value = {"/shopping_list/{shopping_id}"})
    @ApiOperation(value="Remove a shopping list form our DB")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_CLIENT')")
    public ResponseEntity<Object> removeShoppingList(@PathVariable int shopping_id){

        Optional<ShoppingList> shoppingList_lookup = shoppingListRepository.findById(shopping_id);
        if (!shoppingList_lookup.isPresent()){
            return new ResponseEntity<>("Failed to find key", HttpStatus.BAD_REQUEST);
        }

        shoppingListRepository.delete(shoppingList_lookup.get());
        return new ResponseEntity<>("Updated table and removed shopping list id: " +
                String.valueOf(shopping_id), HttpStatus.OK);
    }

    @PostMapping(value = {"/shopping_list/{shopping_id}/item/{item_id}"})
    @ApiOperation(value="Add Item to a Shopping List")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_CLIENT')")
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
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_CLIENT')")
    public List<Item> retrieveAllItem(@ApiParam(value="Format: VAR(:<>)VALUE(,!?&) \nExample: name:Spoon&id<20", name="search")
                                          @RequestParam(value="search", required = false) String search){
        List<Item> item_data;
        if(search == null){
            item_data = itemRepository.findAll();

        }else{
            item_data = searchForWord(itemRepository, search);
        }
        return item_data;
    }

    @PostMapping("/item")
    @ApiOperation(value="Add new Item/Article to our Inventory")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
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
