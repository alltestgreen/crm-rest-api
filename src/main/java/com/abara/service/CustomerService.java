package com.abara.service;

import com.abara.entity.Customer;

import java.util.Map;
import java.util.Optional;

public interface CustomerService {

    Customer save(Customer customer);

    Map<Long, String> listAllCustomer();

    Optional<Customer> findById(Long id);

    void delete(Long id);

}
