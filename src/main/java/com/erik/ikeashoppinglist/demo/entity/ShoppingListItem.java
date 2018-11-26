package com.erik.ikeashoppinglist.demo.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;
import java.util.Objects;

@Entity(name = "ShoppingListItem")
@Table(name = "shopping_list_item")
public class ShoppingListItem {

    @Id
    @EmbeddedId
    private ShoppingListItemId id;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("shoppingListId")
    private ShoppingList shoppingList;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("itemId")
    private Item item;

    @Column(name="amount")
    private Integer amount;

    public ShoppingListItem() {
    }

    public ShoppingListItem(ShoppingList shoppingList, Item item) {
        super();
        this.shoppingList = shoppingList;
        this.item = item;
        this.id = new ShoppingListItemId(shoppingList.getId(), item.getId());
    }

    public ShoppingListItemId getId() {
        return id;
    }

    public void setId(ShoppingListItemId id) {
        this.id = id;
    }

    public ShoppingList getShoppingList() {
        return shoppingList;
    }

    public void setShoppingList(ShoppingList shoppingList) {
        this.shoppingList = shoppingList;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    @Override
    public boolean equals(Object o){
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ShoppingListItem other = (ShoppingListItem) o;
        return Objects.equals(shoppingList, other.shoppingList) && Objects.equals(item, other.item);
    }

    @Override
    public int hashCode(){
        return Objects.hash(shoppingList, item);
    }
}
