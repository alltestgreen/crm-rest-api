package com.abara.service;

import com.abara.entity.Customer;
import com.abara.entity.CustomerImage;
import com.abara.model.CustomerDetails;
import com.abara.repository.CustomerRepository;
import com.abara.validation.EntityValidator;
import com.abara.validation.ValidationException;
import com.abara.validation.ValidationResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private EntityValidator entityValidator;

    @Override
    public Long create(Customer customer, String createdBy) {
        validate(customer);

        customer.setCreatedBy(createdBy);
        Customer savedCustomer = customerRepository.save(customer);
        return savedCustomer.getId();
    }

    @Override
    public List<CustomerDetails> list() {
        return StreamSupport.stream(customerRepository.findAll().spliterator(), false)
                .map(c -> CustomerDetails.fromCustomer(c, null))
                .collect(Collectors.toList());
    }

    @Override
    public CustomerDetails getDetailsById(Long id, URI uri) {
        Customer customer = getCustomerById(id);

        return CustomerDetails.fromCustomer(customer, uri);
    }

    @Override
    public Long update(Customer customer, String updatedBy) {
        Customer existingCustomer = getCustomerById(customer.getId());

        existingCustomer.setName(customer.getName());
        existingCustomer.setSurname(customer.getSurname());
        if (customer.getImage() != null) {
            existingCustomer.setImage(customer.getImage());
        }

        existingCustomer.setModifiedBy(updatedBy);

        validate(existingCustomer);

        Customer updatedCustomer = customerRepository.save(existingCustomer);
        return updatedCustomer.getId();
    }

    @Override
    public void delete(Long id) {
        Customer customer = getCustomerById(id);

        customerRepository.deleteById(customer.getId());
    }

    @Override
    public Long uploadImage(Long id, MultipartFile file) throws IOException {
        Customer customer = getCustomerById(id);
        CustomerImage customerImage = new CustomerImage(file.getOriginalFilename(), file.getContentType(), file.getBytes());

        validate(customerImage);

        customer.setImage(customerImage);

        Customer savedCustomer = customerRepository.save(customer);
        return savedCustomer.getId();
    }

    @Override
    public CustomerImage getImageById(Long id) {
        Customer customer = getCustomerById(id);

        if (customer.getImage() == null)
            throw new EntityNotFoundException("Could not find Customer Image by ID: " + id);
        return customer.getImage();
    }

    @Override
    public void deleteImage(Long id) {
        Customer customer = getCustomerById(id);
        if (customer.getImage() != null) {
            customer.setImage(null);
            customerRepository.save(customer);
        }
    }

    private void validate(Object obj) {
        Optional<ValidationResult> validationResult = entityValidator.validate(obj);
        if (validationResult.isPresent()) throw new ValidationException(validationResult.get());
    }

    private Customer getCustomerById(Long id) {
        Optional<Customer> customerOptional = customerRepository.findById(id);
        if (!customerOptional.isPresent()) throw new EntityNotFoundException("Could not find Customer by ID: " + id);
        return customerOptional.get();
    }

}
