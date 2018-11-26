package com.erik.ikeashoppinglist.demo.entity;


import com.erik.ikeashoppinglist.demo.respository.ShoppingListRepository;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.Iterator;
import java.util.List;

@Entity
public class Customer {
    @Id
    @GeneratedValue
    private Integer id;

    @Size(min=3)
    private String name;

    @OneToMany(mappedBy="customer", fetch = FetchType.EAGER)
    private List<ShoppingList> shoppingLists;

    private Boolean hidden = false;

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

    public List<ShoppingList> getShoppingLists() {
        return shoppingLists;
    }

    public void addShoppingList(ShoppingList shoppingList) {
        this.shoppingLists.add(shoppingList);
    }

    public Customer(@Size(min = 3) String name) {
        super();
        this.name = name;
    }

    protected Customer() {
    }

    public Boolean getHidden() {
        return hidden;
    }
}
