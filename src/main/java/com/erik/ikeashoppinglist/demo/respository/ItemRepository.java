package com.erik.ikeashoppinglist.demo.respository;

import com.erik.ikeashoppinglist.demo.entity.Item;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ItemRepository extends SearchableRepository<Item, Integer>{
    @Query("SELECT i FROM Item i where i.itemIdentification = :itemIdentification")
    List<Item> findItemByItemIdentification(@Param("itemIdentification") String itemIdentification);
}
