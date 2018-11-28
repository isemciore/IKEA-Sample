package com.erik.ikeashoppinglist.demo.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import io.swagger.annotations.ApiModel;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Objects;

@Entity(name = "ShoppingListItem")
@Table(name = "shopping_list_item")
@ApiModel(description = "Keep tracks of the relation between Shopping list and Item")
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
    @NotNull(message="Maximum of 10 of one type of item is allowed")
    private int amount;

    public ShoppingListItem() {
    }

    public ShoppingListItem(ShoppingList shoppingList, Item item, int amount) {
        super();
        if (amount < 0 || amount > 10){
            throw new IllegalArgumentException("Expected a value between 0 and 10");
        }

        this.shoppingList = shoppingList;
        this.item = item;
        this.id = new ShoppingListItemId(shoppingList.getId(), item.getId());
        this.amount = amount;
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
