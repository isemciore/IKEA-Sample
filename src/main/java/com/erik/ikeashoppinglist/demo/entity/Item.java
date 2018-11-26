package com.erik.ikeashoppinglist.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.NaturalIdCache;

import javax.persistence.*;
import javax.validation.constraints.Pattern;
import java.util.*;

@Entity(name = "Item")
@Table(name = "item")
@NaturalIdCache
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Item {
    private final static String item_id_pattern = "^\\d{3}-\\d{3}-\\d{2}$";

    @Id
    @GeneratedValue
    private Integer id; //Internally use only for DB

    // "Searchable number"
    @NaturalId
    @Pattern(regexp = item_id_pattern)
    private String itemIdentification;

    private String name;

    @JsonIgnore
    @OneToMany(
            mappedBy = "item",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private List<ShoppingListItem> shoppingLists = new ArrayList<>();
    //@ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.ALL}, mappedBy = "items")
    //private Set<ShoppingList>  shoppingLists = new HashSet<>();


    public Item() {
    }

    public Item(@Pattern(regexp = item_id_pattern) String itemIdentification, String name) {
        super();
        this.itemIdentification = itemIdentification;
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getItemIdentification() {
        return itemIdentification;
    }

    public void setItemIdentification(String itemIdentification) {
        this.itemIdentification = itemIdentification;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ShoppingListItem> getShoppingLists() {
        return shoppingLists;
    }

    //public void setShoppingLists(List<ShoppingListItem> shoppingLists) {
    //    this.shoppingLists = shoppingLists;
    //}

    @Override
    public String toString() {
        return "Item{" +
                "id=" + id +
                ", itemIdentification='" + itemIdentification + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o){
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Item other = (Item) o;
        return Objects.equals(itemIdentification, other.itemIdentification);
    }

    @Override
    public int hashCode(){
        return Objects.hash(itemIdentification);
    }
}
