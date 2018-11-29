package com.erik.ikeashoppinglist.demo.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.List;

@Entity
public class Customer {
    @Id
    @GeneratedValue
    private Integer id;

    @Size(min=3)
    @Column(unique = true, nullable = false)
    private String name;

    @OneToMany(mappedBy="customer", cascade = {CascadeType.ALL},
            fetch = FetchType.LAZY)
    @JsonIgnore
    private List<ShoppingList> shoppingLists;

    private Boolean hidden = false; // Hide instead of delete

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
