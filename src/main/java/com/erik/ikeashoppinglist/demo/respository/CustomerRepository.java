package com.erik.ikeashoppinglist.demo.respository;

import com.erik.ikeashoppinglist.demo.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {

    // @Query("SELECT p FROM Partner p JOIN FETCH p.tenants WHERE p.id=(:id)")
    //    public Partner findByIdFetchTenant(@Param("id") Long id);
}
