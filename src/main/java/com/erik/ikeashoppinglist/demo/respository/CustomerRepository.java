package com.erik.ikeashoppinglist.demo.respository;

import com.erik.ikeashoppinglist.demo.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {

    // @Query("SELECT p FROM Partner p JOIN FETCH p.tenants WHERE p.id=(:id)")
    //    public Partner findByIdFetchTenant(@Param("id") Long id);
    @Query("SELECT c FROM Customer c where c.name = :name")
    public List<Customer> findCustomerByName(@Param("name") String name);

}
