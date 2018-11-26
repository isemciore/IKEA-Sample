package com.erik.ikeashoppinglist.demo.shop;

import org.springframework.data.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

@Resository
public class CustomerRepository extends JpaRepository<Customer, Integer> {
}
