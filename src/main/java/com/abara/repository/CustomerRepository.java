package com.abara.repository;

import com.abara.model.Customer;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CustomerRepository extends CrudRepository<Customer, Long> {

    @Query(value = "SELECT CONCAT(NAME, ' ', SURNAME) AS FULLNAME FROM CUSTOMER", nativeQuery = true)
    List<String> listAllCustomer();
}
