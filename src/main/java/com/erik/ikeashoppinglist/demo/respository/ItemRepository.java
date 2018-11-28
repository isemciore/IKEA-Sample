package com.erik.ikeashoppinglist.demo.respository;

import com.erik.ikeashoppinglist.demo.entity.Customer;
import com.erik.ikeashoppinglist.demo.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ItemRepository extends SearchableRepository<Item, Integer>{
//JpaRepository<Item, Integer>, JpaSpecificationExecutor<Item> {
    @Query("SELECT i FROM Item i where i.itemIdentification = :itemIdentification")
    public List<Item> findItemByItemIdentification(@Param("itemIdentification") String itemIdentification);
}
