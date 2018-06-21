package com.abara.repository;

import com.abara.entity.Customer;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CustomerRepository extends CrudRepository<Customer, Long> {

    @Query("SELECT id, CONCAT(name, ' ', surname) FROM Customer")
    List<Object[]> listAllCustomer();
}
