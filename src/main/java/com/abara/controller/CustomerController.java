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

@RestController
@RequestMapping("/api/customer")
public class CustomerController {

    private static final Logger LOG = LoggerFactory.getLogger(CustomerController.class);

    @Autowired
    private CustomerService customerService;

    @Autowired
    private EntityValidator entityValidator;

    @PostMapping("/create")
    public ResponseEntity<ValidationResult> create(Principal principal, @RequestBody Customer customer) {
        LOG.debug("Creating Customer: " + customer);

        Long id = customerService.create(customer, principal.getName());

        return ResponseEntity.created(buildResourceUrl(id)).build();
    }

    @GetMapping("/list")
    public Map<Long, String> list() {
        LOG.debug("Retrieving all Customer Details");

        return customerService.list();
    }

    @GetMapping("/details/{id}")
    public ResponseEntity<CustomerDetails> details(@PathVariable Long id) {
        LOG.debug("Getting details of Customer by id: " + id);

        CustomerDetails customerDetails = customerService.getDetailsById(id, buildImageResourceUrl(id));
        return ResponseEntity.ok(customerDetails);
    }


    @PutMapping("/update")
    public ResponseEntity<ValidationResult> update(Principal principal, @RequestBody Customer customer) {
        LOG.debug("Updating Customer: " + customer);

        Long id = customerService.update(customer, principal.getName());
        return ResponseEntity.ok().location(buildResourceUrl(id)).build();
    }

    @PostMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        LOG.debug("Deleting Customer by ID: " + id);

        customerService.delete(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/image/upload/{id}")
    public ResponseEntity<ValidationResult> uploadImage(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        LOG.debug("Uploading Customer Image by ID: {}", id);

        try {
            Long imageId = customerService.uploadImage(id, file);
            return ResponseEntity.created(buildImageResourceUrl(imageId)).build();
        } catch (IOException e) {
            LOG.error("Unable to upload image:" + e, e);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/image/{id}")
    public ResponseEntity<byte[]> image(@PathVariable Long id) {
        LOG.debug("Getting Customer Image by ID: {}", id);

        CustomerImage customerImage = customerService.getImageById(id);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf(customerImage.getType()));
        headers.setCacheControl(CacheControl.noCache().getHeaderValue());
        return new ResponseEntity<>(customerImage.getData(), headers, HttpStatus.OK);
    }

    @PostMapping("/image/delete/{id}")
    public ResponseEntity<Void> deleteImage(@PathVariable Long id) {
        LOG.debug("Deleting Customer Image by ID: {}", id);

        customerService.deleteImage(id);
        return ResponseEntity.ok().build();
    }

    private URI buildResourceUrl(Long id) {
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/customer/details/{id}")
                .buildAndExpand(id)
                .toUri();
    }

    private URI buildImageResourceUrl(Long id) {
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/customer/image/{id}")
                .buildAndExpand(id)
                .toUri();
    }
}
