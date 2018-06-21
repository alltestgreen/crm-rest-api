package com.abara.service;

import com.abara.entity.Customer;
import com.abara.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Override
    public Customer save(Customer customer) {
        return customerRepository.save(customer);
    }

    @Override
    public Map<Long, String> listAllCustomer() {
        Map<Long, String> customerMap = new HashMap<>();
        customerRepository.listAllCustomer().forEach(customer -> customerMap.put(((BigInteger) customer[0]).longValue(), (String) customer[1]));
        return customerMap;
    }

    @Override
    public Optional<Customer> findById(Long id) {
        return customerRepository.findById(id);
    }

    @Override
    public void delete(Long id) {
        customerRepository.deleteById(id);
    }

}
