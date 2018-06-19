package com.abara.service;

import com.abara.model.Customer;
import com.abara.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerServiceImpl implements CustomerService {

    private CustomerRepository customerRepository;

    public CustomerServiceImpl(CustomerRepository CustomerRepository) {
        this.customerRepository = CustomerRepository;
    }

    @Override
    public Customer save(Customer Customer) {
        return customerRepository.save(Customer);
    }

    @Override
    public Iterable<Customer> list() {
        return customerRepository.findAll();
    }

    @Override
    public List<String> listAllCustomer() {
        return customerRepository.listAllCustomer();
    }

    @Override
    public Optional<Customer> findById(Long id) {
        return customerRepository.findById(id);
    }

    @Override
    public void delete(Customer customer) {
        customerRepository.delete(customer);
    }

}
