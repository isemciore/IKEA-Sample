package com.erik.ikeashoppinglist.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import java.util.*;

@Entity(name = "ShoppingList")
@Table(name = "shopping_list")
public class ShoppingList {

    @Id
    @GeneratedValue
    private Integer id;
    private String name;

    @Override
    public String toString() {
        return "ShoppingList{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", items=" + items +
                '}';
    }

    @JsonManagedReference
    @OneToMany(mappedBy = "shoppingList",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.EAGER)
    private List<ShoppingListItem> items = new ArrayList<>();

    public List<ShoppingListItem> getItems() {
        return items;
    }

    public void addItem(Item item){
        ShoppingListItem shoppingListItem = new ShoppingListItem(this, item);
        items.add(shoppingListItem);
        item.getShoppingLists().add(shoppingListItem);
    }

    public void removeItem(Item item){
        for (Iterator<ShoppingListItem> it = items.iterator();  it.hasNext(); ){
            ShoppingListItem shoppingListItem = it.next();

            if (shoppingListItem.getShoppingList().equals(this) && shoppingListItem.getItem().equals(item)){
                it.remove();
                shoppingListItem.getItem().getShoppingLists().remove(shoppingListItem);
                shoppingListItem.setItem(null);
                shoppingListItem.setShoppingList(null);
            }
        }
    }

    @JsonIgnore
    @ManyToOne(fetch=FetchType.LAZY, cascade = {CascadeType.ALL})
    private Customer customer;

    //@ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.ALL})
    //@JoinTable(name="shopping_list_item",
    //        joinColumns = { @JoinColumn(name="shopping_list_id")},
    //        inverseJoinColumns = {@JoinColumn(name="item_id")})
    //private Set<Item> items = new HashSet<>();

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public ShoppingList() {
    }

    public ShoppingList(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o){
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ShoppingList other = (ShoppingList) o;
        return Objects.equals(id, other.id);
    }

    @Override
    public int hashCode(){
        return Objects.hash(id);
    }

}
