package com.erik.ikeashoppinglist.demo;

import com.erik.ikeashoppinglist.demo.entity.Customer;
import com.erik.ikeashoppinglist.demo.entity.Item;
import com.erik.ikeashoppinglist.demo.entity.ShoppingList;
import com.erik.ikeashoppinglist.demo.misc_com_fmt.SearchCriteria;
import com.erik.ikeashoppinglist.demo.respository.CustomerRepository;
import com.erik.ikeashoppinglist.demo.respository.ItemRepository;
import com.erik.ikeashoppinglist.demo.respository.SearchSpecification;
import com.erik.ikeashoppinglist.demo.respository.ShoppingListRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class SimpleShoppingListApplicationTests {
    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    ShoppingListRepository shoppingListRepository;

    @Autowired
    private MockMvc mvc;

    private Customer customerAlice;
    private Customer customerBob;

    private ShoppingList shoppingListDiningRoom;
    private ShoppingList shoppingListBedRoom;

    private Item itemSpork;
    private Item itemChopStick;

    private String serverAddress = "";

    private static final Logger LOG = LoggerFactory.getLogger(SimpleShoppingListApplicationTests.class);

    public static String superSecretToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsImF1dGgiOlt7ImF1dG" +
            "hvcml0eSI6IlJPTEVfQURNSU4ifV0sImlhdCI6MTU0MzUyNTEwNSwiZXhwIjoxODU5MTQ0MzA1fQ." +
            "zQ-FvqAlwZPN8F7RJqkTUDq9_ZB7hSsnPbTAfzNy5kU";

    @Before
    public void init(){
        itemRepository.deleteAll();
        customerRepository.deleteAll();
        shoppingListRepository.deleteAll();
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
        // 1 Search term
        SearchCriteria spork_search = new SearchCriteria("name",":","Spork", ",");
        SearchSpecification<Item> spec1 = new SearchSpecification<Item>(spork_search);
        List<Item> results = itemRepository.findAll(spec1);

        assertThat(itemSpork).isIn(results);
        assertThat(itemChopStick).isNotIn(results);

        // 2 Search terms
        SearchCriteria chopstick_search = new SearchCriteria(
                "name",":", "ChopStick", "|");
        SearchSpecification<Item> spec2 = new SearchSpecification<Item>(chopstick_search);

        Specification<Item> orSearchItem = spec2.or(spec1);

        List<Item> or_search_results = itemRepository.findAll(orSearchItem);

        assertThat(itemSpork).isIn(or_search_results);
        assertThat(itemChopStick).isIn(or_search_results);
    }



    // REST API JSON HELPER STUFF
    private ObjectMapper mapper = new ObjectMapper();

    <T> T fromJsonResult(MvcResult result, Class<T> tClass) throws Exception {
        return this.mapper.readValue(result.getResponse().getContentAsString(), tClass);
    }

    private ResultActions invokeAllCustomer(String param) throws Exception {
        return mvc.perform(get(serverAddress+"/customer/" + param).header("Authorization", "Bearer " + superSecretToken).accept(MediaType.APPLICATION_JSON));
    }

    private ResultActions getShoppingList(String param) throws Exception {
        return mvc.perform(get(serverAddress+"/shopping_list/" + param).header("Authorization", "Bearer " + superSecretToken).accept(MediaType.APPLICATION_JSON));
    }
    private ResultActions deleteShoppingList(String param) throws Exception {
        return this.mvc.perform(MockMvcRequestBuilders
                .delete(serverAddress+"/shopping_list/{id}",String.valueOf(param)).header("Authorization", "Bearer " + superSecretToken)
                .contentType(MediaType.APPLICATION_JSON));
    }

    private ResultActions getItem(String param) throws Exception {
        return mvc.perform(get(serverAddress+"/item/" + param).accept(MediaType.APPLICATION_JSON));
    }

    @Test
    public void start_with_two_customer() throws Exception {
        MvcResult result = invokeAllCustomer("")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("Alice")))
                .andReturn();

        // Customer[] customers = fromJsonResult(result, Customer[].class);
        // LOG.debug("Customer's id: {}", customers[0].getId());
    }

    @Test
    public void search_via_api() throws Exception {
        MvcResult single_result = invokeAllCustomer("?search=name:Alice")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Alice")))
                .andReturn();

        MvcResult double_result = invokeAllCustomer("?search=name:Alice|name:Bob")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("Alice")))
                .andExpect(jsonPath("$[1].name", is("Bob")))
                .andReturn();
    }

    @Test
    @DirtiesContext
    public void externalDeleteShoppingList() throws Exception {
        ResultActions shopping_list_results = getShoppingList("");
        ShoppingList[] shoppingLists = fromJsonResult(shopping_list_results.andReturn(), ShoppingList[].class);
        int shopping_list_id = shoppingLists[0].getId();

        MvcResult shopping_list_result1 = getShoppingList("")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andReturn();

        MvcResult delete_res = deleteShoppingList(String.valueOf(shopping_list_id)).andReturn();
        LOG.debug(delete_res.toString());
        // No clue what this broken, manual test shows all is fine...

        MvcResult temp = getShoppingList("").andReturn();
        ShoppingList[] shopping_list = fromJsonResult(temp, ShoppingList[].class);
        LOG.debug(Arrays.toString(shopping_list));

        MvcResult shopping_list_result = getShoppingList("")  // One should have been deleted here
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andReturn();

        MvcResult result = invokeAllCustomer("")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("Alice")))
                .andReturn();
        MvcResult item = invokeAllCustomer("")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$.name", is("Spork")))
                .andReturn();
    }
    // Some stuff to test:
    // Stuff to implement -> 0 will remove relationship to item
    // Will not allow negative or number larger than 10
    // Update Item value without creating new item in shopping list

    @Test
    public void contextLoads() {
    }

}


