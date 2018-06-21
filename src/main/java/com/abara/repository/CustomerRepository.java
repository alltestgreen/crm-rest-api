package com.abara.repository;

import com.abara.entity.Customer;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CustomerRepository extends CrudRepository<Customer, Long> {

    @Query(value = "SELECT ID, CONCAT(NAME, ' ', SURNAME) AS FULL_NAME FROM CUSTOMER", nativeQuery = true)
    List<Object[]> listAllCustomer();
}
