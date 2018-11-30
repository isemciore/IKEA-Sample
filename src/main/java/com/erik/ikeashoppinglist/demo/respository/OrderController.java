package com.erik.ikeashoppinglist.demo.respository;


import com.erik.ikeashoppinglist.demo.entity.Customer;
import com.erik.ikeashoppinglist.demo.entity.Item;
import com.erik.ikeashoppinglist.demo.entity.ShoppingList;
import com.erik.ikeashoppinglist.demo.entity.ShoppingListItem;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.annotation.*;

import javax.management.openmbean.InvalidKeyException;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@Api(value="ShoppingCart", tags = {"OrderControllerTag"})
public class OrderController {

    private static final Logger LOG = LoggerFactory.getLogger(OrderController.class);

    private final CustomerRepository customerRepository;

    private final ItemRepository itemRepository;

    private final ShoppingListRepository shoppingListRepository;

    @Autowired
    public OrderController(CustomerRepository customerRepository, ItemRepository itemRepository,
                           ShoppingListRepository shoppingListRepository) {
        this.customerRepository = customerRepository;
        this.itemRepository = itemRepository;
        this.shoppingListRepository = shoppingListRepository;
    }

    private <T> Specification<T> getSearchSpecification(String search){
        SearchSpecificationBuilder<T> builder = new SearchSpecificationBuilder<>();
        //%21: !, %26: &, %7C: |
        // matcher splits at the symbols |&,!
        // example: VARNAME:VALUE!VARNAME2<VALUE2
        // Searching for entity matching name VARNAME containing VALUE and VARNAME2 with value less than VALUE2
        // base pattern("(\\w+?)(:|<|>)(\\w+?)(,|!|&|\\|)"
        Pattern pattern = Pattern.compile("(\\w+?)([:<>])(\\w+?)([,!&|])", Pattern.UNICODE_CHARACTER_CLASS);
        Matcher matcher = pattern.matcher(search + ",");
        while (matcher.find()) {
            builder.with(matcher.group(1), matcher.group(2), matcher.group(3), matcher.group(4));
        }

        return builder.build();
    }

    private <T, K> List<T> searchForWord(SearchableRepository<T, K> repository, String search){
        Specification<T> spec = getSearchSpecification(search);
        List<T> searchResult;
        if (spec == null){
            searchResult = new ArrayList<>();
        } else {
            try {
                searchResult = repository.findAll(spec);
            } catch (InvalidDataAccessApiUsageException e) {
                searchResult = new ArrayList<>();
            }
        }
        return searchResult;
    }

    @ApiOperation(value="Views a list of all Customers or search")
    @GetMapping("/customer")
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
        List<Customer> searchResult = customerRepository.findCustomerByName(customer.getName());
        if (!searchResult.isEmpty()) {
            return new ResponseEntity<>("Customer already exist", HttpStatus.BAD_REQUEST);
        }

        customerRepository.save(customer);

        return new ResponseEntity<>("Updated Customer Table", HttpStatus.CREATED);
    }


    @ApiOperation(value="View a specific Customer <Todo>")
    @GetMapping("/customer/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Customer retrieveCustomer(@PathVariable int id){
        Optional<Customer> user = customerRepository.findById(id);
        if (!user.isPresent()){
            throw new InvalidKeyException("id: " + id);
        }

        return user.get();
    }

    @ApiOperation(value="Add a new Shopping list for a customer")
    @PostMapping("/customer/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_CLIENT')")
    public ResponseEntity<Object> createNewShoppingList(@RequestBody ShoppingList shoppingList,
                                                        @PathVariable int id){
        Optional<Customer> customerLookup = customerRepository.findById(id);
        if (!customerLookup.isPresent()){
            throw new InvalidKeyException("id: " + id);
        }

        Customer customer = customerLookup.get();

        ShoppingList newShoppingList = shoppingListRepository.save(shoppingList);

        customer.addShoppingList(newShoppingList);

        shoppingListRepository.flush();

        return new ResponseEntity<>("Added new ShoppingList for a Customer", HttpStatus.CREATED);

    }

    @ApiOperation(value="Views all Shopping List")
    @ApiResponses(value = {
            @ApiResponse(code=200, message = "Here is the lists!"),
            @ApiResponse(code=401, message = "\"security\"")
    })
    @GetMapping("/shopping_list")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_CLIENT')")
    public List<ShoppingList> retrieveAllShoppingList(){
        return shoppingListRepository.findAll();
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


    @DeleteMapping(value = {"/shopping_list/{shoppingId}"})
    @ApiOperation(value="Remove a shopping list form our DB")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_CLIENT')")
    public ResponseEntity<Object> removeShoppingList(@PathVariable int shoppingId){

        Optional<ShoppingList> shoppingListLookup = shoppingListRepository.findById(shoppingId);
        if (!shoppingListLookup.isPresent()){
            return new ResponseEntity<>("Failed to find key", HttpStatus.BAD_REQUEST);
        }

        shoppingListRepository.deleteById(shoppingId);

        return new ResponseEntity<>("Updated table and removed shopping list id: " +  shoppingId, HttpStatus.OK);
    }

    @PostMapping(value = {"/shopping_list/{shoppingId}/item/{itemId}"})
    @ApiOperation(value="Add Item to a Shopping List")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_CLIENT')")
    public ResponseEntity<Object> updateShoppingList(@RequestParam(value = "amount", required = false) int amount,
                                                     @PathVariable int shoppingId,
                                                     @PathVariable int itemId){

        Optional<ShoppingList> shoppingListLookup = shoppingListRepository.findById(shoppingId);
        if (!shoppingListLookup.isPresent()){
            return new ResponseEntity<>("Invalid ShoppingList Id", HttpStatus.BAD_REQUEST);
        }
        Optional<Item> itemLookup = itemRepository.findById(itemId);
        if (!itemLookup.isPresent()){
            return new ResponseEntity<>("Invalid item Id", HttpStatus.BAD_REQUEST);
        }

        if (amount == 0){
            shoppingListLookup.get().removeItem(itemLookup.get());
            shoppingListRepository.flush();
            return new ResponseEntity<>("Removed item to a ShoppingList", HttpStatus.CREATED);
        }

        List<ShoppingListItem> shoppingListItems = shoppingListLookup.get().getItems();
        for (ShoppingListItem sli: shoppingListItems){
            if (sli.getItem() == itemLookup.get()){
                sli.setAmount(amount);
                shoppingListRepository.flush();
                return new ResponseEntity<>("Added item to a ShoppingList", HttpStatus.CREATED);
            }
        }

        shoppingListLookup.get().addItem(itemLookup.get(), amount);
        shoppingListRepository.flush();
        return new ResponseEntity<>("Updated table", HttpStatus.CREATED);
    }


    @GetMapping("/item")
    @ApiOperation(value="View a specific item or search")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_CLIENT')")
    public List<Item> retrieveAllItem(@ApiParam(value="Format: VAR(:<>)VALUE(,!?&) \nExample: name:Spoon&id<20", name="search")
                                          @RequestParam(value="search", required = false) String search){
        List<Item> itemData;
        if(search == null){
            itemData = itemRepository.findAll();

        }else{
            itemData = searchForWord(itemRepository, search);
        }
        return itemData;
    }

    @PostMapping("/item")
    @ApiOperation(value="Add new Item/Article to our Inventory")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Object> createNewShoppingList(@RequestBody Item item){

        List<Item> searchResult = itemRepository.findItemByItemIdentification(item.getItemIdentification());
        if (!searchResult.isEmpty()) {
            return new ResponseEntity<>("Customer already exist", HttpStatus.BAD_REQUEST);
        }
        try {
            itemRepository.save(item);
        } catch (TransactionSystemException e){
            return new ResponseEntity<>("Bad Input", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("Created a new Item", HttpStatus.CREATED);
    }


}
