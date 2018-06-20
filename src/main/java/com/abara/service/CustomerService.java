package com.abara.service;

import com.abara.model.Customer;

import java.util.List;
import java.util.Optional;

public interface CustomerService {

    Customer save(Customer customer);

    Iterable<Customer> list();

    List<String> listAllCustomer();

    Optional<Customer> findById(Long id);

    void delete(Long id);

}
