package com.abara.controller;

import com.abara.model.Customer;
import com.abara.service.CustomerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/customer")
public class CustomerController {

    private CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping("/create")
    public ResponseEntity<Void> create(@RequestBody Customer customer) {
        if (customer == null) return ResponseEntity.noContent().build();
        Customer newCustomer = customerService.save(customer);
        URI location = ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/customer/details/{id}").buildAndExpand(newCustomer.getId()).toUri();
        return ResponseEntity.created(location).build();
    }

    @GetMapping(value = {"/list"})
    public List<String> listNames() {
        return customerService.listAllCustomer();
    }

    @GetMapping(value = {"/listHTML"})
    public String listHTML() {
        StringBuilder sb = new StringBuilder("<html><body><table border=1>");
        sb.append("<tr><td>Id</td><td>Name</td><td>Surname</td></tr>");
        for (Customer customer : customerService.list()) {
            sb.append("<tr>");
            sb.append("<td>").append(customer.getId()).append("</td>");
            sb.append("<td>").append(customer.getName()).append("</td>");
            sb.append("<td>").append(customer.getSurname()).append("</td>");
            sb.append("</tr>");
        }
        return sb.append("<table></body></html>").toString();
    }

    @GetMapping(value = {"/details/{customerId}"})
    public Optional<Customer> details(@PathVariable Long customerId) {
        return customerService.findById(customerId);
    }

    @PutMapping(value = "/update")
    public ResponseEntity<Void> update(@RequestBody Customer customer) {
        if (customer == null) return ResponseEntity.noContent().build();
        Optional<Customer> existingCustomer = customerService.findById(customer.getId());
        if (!existingCustomer.isPresent()) return ResponseEntity.badRequest().build();
        customerService.save(customer);
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/delete")
    public ResponseEntity<Void> delete(@RequestBody Customer customer) {
        if (customer == null) return ResponseEntity.noContent().build();
        Optional<Customer> existingCustomer = customerService.findById(customer.getId());
        if (!existingCustomer.isPresent()) return ResponseEntity.badRequest().build();
        customerService.delete(customer);
        return ResponseEntity.ok().build();
    }

}
