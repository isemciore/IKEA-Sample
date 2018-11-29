package com.erik.ikeashoppinglist.demo.respository;

import com.erik.ikeashoppinglist.demo.entity.Customer;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerRepository extends SearchableRepository<Customer, Integer>{
    @Query("SELECT c FROM Customer c where c.name = :name")
    List<Customer> findCustomerByName(@Param("name") String name);

}
