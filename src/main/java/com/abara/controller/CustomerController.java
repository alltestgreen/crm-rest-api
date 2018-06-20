package com.abara.controller;

import com.abara.entity.Customer;
import com.abara.entity.CustomerImage;
import com.abara.model.CustomerDetails;
import com.abara.service.CustomerService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/customer")
public class CustomerController {

    private CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping("/list")
    public List<String> listNames() {
        return customerService.listAllCustomer();
    }

    @GetMapping("/listHTML")
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

    @GetMapping("/details/{customerId}")
    public ResponseEntity<CustomerDetails> details(HttpServletRequest request, @PathVariable Long customerId) throws MalformedURLException {
        Optional<Customer> customerOptional = customerService.findById(customerId);
        if (customerOptional.isPresent()) {
            Customer customer = customerOptional.get();
            URL url = new URL(request.getRequestURL().toString());
            String imageURL = url.getProtocol() + "://" + url.getHost() + ":" + url.getPort() + "/api/customer/image/" + customer.getId();
            CustomerDetails customerDetails = new CustomerDetails(customer.getName(), customer.getSurname(), imageURL, customer.getCreatedBy(), customer.getModifiedBy());
            return ResponseEntity.ok(customerDetails);
        }
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/create")
    public ResponseEntity<Void> create(Principal principal, @RequestBody Customer customer) {
        if (customer == null) return ResponseEntity.noContent().build();
        customer.setCreatedBy(principal.getName());
        Customer newCustomer = customerService.save(customer);
        URI location = ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/customer/details/{id}").buildAndExpand(newCustomer.getId()).toUri();
        return ResponseEntity.created(location).build();
    }

    @PutMapping("/update")
    public ResponseEntity<Void> update(Principal principal, @RequestBody Customer customer) {
        if (customer == null) return ResponseEntity.noContent().build();
        Optional<Customer> existingCustomerOptional = customerService.findById(customer.getId());
        if (!existingCustomerOptional.isPresent()) return ResponseEntity.noContent().build();

        Customer existingCustomer = existingCustomerOptional.get();

        existingCustomer.setName(customer.getName());
        existingCustomer.setSurname(customer.getSurname());
        existingCustomer.setImage(customer.getImage());

        existingCustomer.setModifiedBy(principal.getName());

        customerService.save(existingCustomer);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/delete/{customerId}")
    public ResponseEntity<Void> delete(@PathVariable Long customerId) {
        if (customerId == null) return ResponseEntity.noContent().build();
        Optional<Customer> existingCustomer = customerService.findById(customerId);
        if (!existingCustomer.isPresent()) return ResponseEntity.noContent().build();
        customerService.delete(customerId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/uploadImage/{customerId}")
    public ResponseEntity<Void> uploadImage(@PathVariable Long customerId, @RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) return ResponseEntity.noContent().build();
        if (customerId == null) return ResponseEntity.noContent().build();
        Optional<Customer> existingCustomer = customerService.findById(customerId);
        if (!existingCustomer.isPresent()) return ResponseEntity.noContent().build();
        Customer customer = existingCustomer.get();

        try {
            System.out.println("Customer exist:" + customer);
            CustomerImage customerImage = new CustomerImage(file.getOriginalFilename(), file.getContentType(), file.getBytes());
            customer.setImage(customerImage);
            customerService.save(customer);
        } catch (IOException e) {
            System.out.println("Unable to upload image " + e);
        }

        URI location = ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/customer/image/{id}").buildAndExpand(customer.getId()).toUri();
        return ResponseEntity.created(location).build();
    }

    @GetMapping("/image/{customerId}")
    public ResponseEntity<byte[]> image(@PathVariable Long customerId) {
        Optional<Customer> customerOptional = customerService.findById(customerId);
        if (customerOptional.isPresent()) {
            Customer customer = customerOptional.get();
            if (customer.getImage() != null) {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.valueOf(customer.getImage().getType()));
                headers.setCacheControl(CacheControl.noCache().getHeaderValue());
                return new ResponseEntity<>(customer.getImage().getImage(), headers, HttpStatus.OK);
            }
        }
        return ResponseEntity.noContent().build();
    }

}
