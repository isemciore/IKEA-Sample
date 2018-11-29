package com.erik.ikeashoppinglist.demo.entity;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class ShoppingListItemId implements Serializable {
    @Column(name = "shopping_list_id")
    private long shoppingListId;

    @Column(name = "itemId")
    private long itemId;

    public ShoppingListItemId() {
    }

    public ShoppingListItemId(long shoppingListId, long itemId) {
        this.shoppingListId = shoppingListId;
        this.itemId = itemId;
    }

    public long getShoppingListId() {
        return shoppingListId;
    }

    public long getItemId() {
        return itemId;
    }

    @Override
    public boolean equals(Object o){
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ShoppingListItemId other = (ShoppingListItemId) o;
        return Objects.equals(shoppingListId, other.shoppingListId) && Objects.equals(itemId, other.itemId);
    }

    @Override
    public int hashCode(){
        return Objects.hash(shoppingListId, itemId);
    }
}
