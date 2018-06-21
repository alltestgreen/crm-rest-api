package com.abara.controller;

import com.abara.entity.Customer;
import com.abara.entity.CustomerImage;
import com.abara.model.CustomerDetails;
import com.abara.service.CustomerService;
import com.abara.validation.EntityValidator;
import com.abara.validation.ValidationResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.security.Principal;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/customer")
public class CustomerController {

    private static final Logger LOG = LoggerFactory.getLogger(CustomerController.class);

    @Autowired
    private CustomerService customerService;

    @Autowired
    private EntityValidator entityValidator;

    @GetMapping("/list")
    public Map<Long, String> listCustomers() {
        LOG.debug("Retrieving all Customer Details");
        return customerService.listAllCustomer();
    }

    @GetMapping("/details/{customerId}")
    public ResponseEntity<CustomerDetails> details(@PathVariable Long customerId) {
        LOG.debug("Getting details of Customer by id: " + customerId);

        Optional<Customer> customerOptional = customerService.findById(customerId);
        if (customerOptional.isPresent()) {
            Customer customer = customerOptional.get();
            URI imageURI = null;
            if (customer.getImage() != null) {
                imageURI = ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/customer/image/{id}").buildAndExpand(customer.getId()).toUri();
            }
            CustomerDetails customerDetails = new CustomerDetails(customer.getId(), customer.getName(), customer.getSurname(), imageURI, customer.getCreatedBy(), customer.getModifiedBy());
            return ResponseEntity.ok(customerDetails);
        }
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/create")
    public ResponseEntity<ValidationResult> create(Principal principal, @RequestBody Customer customer) {
        LOG.debug("Creating Customer: " + customer);

        ValidationResult validationResult = entityValidator.validate(customer);
        if (validationResult.hasErrors()) {
            return ResponseEntity.badRequest().body(validationResult);
        }

        customer.setCreatedBy(principal.getName());
        Customer newCustomer = customerService.save(customer);

        URI location = ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/customer/details/{id}").buildAndExpand(newCustomer.getId()).toUri();
        return ResponseEntity.created(location).build();
    }

    @PutMapping("/update")
    public ResponseEntity<ValidationResult> update(Principal principal, @RequestBody Customer customer) {
        LOG.debug("Updating Customer: " + customer);

        Optional<Customer> existingCustomerOptional = customerService.findById(customer.getId());
        if (!existingCustomerOptional.isPresent()) return ResponseEntity.noContent().build();

        Customer existingCustomer = existingCustomerOptional.get();

        existingCustomer.setName(customer.getName());
        existingCustomer.setSurname(customer.getSurname());
        if (customer.getImage() != null) {
            existingCustomer.setImage(customer.getImage());
        }

        existingCustomer.setModifiedBy(principal.getName());

        ValidationResult validationResult = entityValidator.validate(existingCustomer);
        if (validationResult.hasErrors()) {
            return ResponseEntity.badRequest().body(validationResult);
        }

        customerService.save(existingCustomer);

        URI location = ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/customer/details/{id}").buildAndExpand(existingCustomer.getId()).toUri();
        return ResponseEntity.ok().location(location).build();
    }

    @PostMapping("/delete/{customerId}")
    public ResponseEntity<Void> delete(@PathVariable Long customerId) {
        LOG.debug("Deleting Customer by ID: " + customerId);

        Optional<Customer> existingCustomer = customerService.findById(customerId);
        if (!existingCustomer.isPresent()) return ResponseEntity.noContent().build();
        customerService.delete(customerId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/image/upload/{customerId}")
    public ResponseEntity<ValidationResult> uploadImage(@PathVariable Long customerId, @RequestParam("file") MultipartFile file) {
        LOG.debug("Uploading Customer Image by ID: {}", customerId);

        Optional<Customer> existingCustomer = customerService.findById(customerId);
        if (!existingCustomer.isPresent()) return ResponseEntity.noContent().build();
        Customer customer = existingCustomer.get();

        try {
            CustomerImage customerImage = new CustomerImage(file.getOriginalFilename(), file.getContentType(), file.getBytes());

            ValidationResult validationResult = entityValidator.validate(customerImage);
            if (validationResult.hasErrors()) {
                return ResponseEntity.badRequest().body(validationResult);
            }

            customer.setImage(customerImage);
            customerService.save(customer);
        } catch (IOException e) {
            LOG.error("Unable to upload image:" + e, e);
        }

        URI location = ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/customer/image/{id}").buildAndExpand(customer.getId()).toUri();
        return ResponseEntity.created(location).build();
    }

    @GetMapping("/image/{customerId}")
    public ResponseEntity<byte[]> image(@PathVariable Long customerId) {
        LOG.debug("Getting Customer Image by ID: {}", customerId);

        Optional<Customer> customerOptional = customerService.findById(customerId);
        if (customerOptional.isPresent()) {
            Customer customer = customerOptional.get();
            if (customer.getImage() != null) {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.valueOf(customer.getImage().getType()));
                headers.setCacheControl(CacheControl.noCache().getHeaderValue());
                return new ResponseEntity<>(customer.getImage().getData(), headers, HttpStatus.OK);
            }
        }
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/image/delete/{customerId}")
    public ResponseEntity<Void> deleteImage(@PathVariable Long customerId) {
        LOG.debug("Deleting Customer Image by ID: {}", customerId);

        Optional<Customer> existingCustomer = customerService.findById(customerId);
        if (!existingCustomer.isPresent()) return ResponseEntity.noContent().build();

        Customer customer = existingCustomer.get();
        if (customer.getImage() != null) {
            customer.setImage(null);
            customerService.save(customer);
        }
        return ResponseEntity.ok().build();
    }

}
