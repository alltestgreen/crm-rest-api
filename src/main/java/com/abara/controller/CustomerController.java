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
import java.util.List;

import static com.abara.controller.CustomerController.API_CUSTOMER_PATH;

@RestController
@RequestMapping(API_CUSTOMER_PATH)
public class CustomerController {

    private static final Logger LOG = LoggerFactory.getLogger(CustomerController.class);

    static final String API_CUSTOMER_PATH = "/api/customers";
    static final String API_CUSTOMER_IMAGE_PATH = API_CUSTOMER_PATH + "/image";

    @Autowired
    private CustomerService customerService;

    @Autowired
    private EntityValidator entityValidator;

    @PostMapping
    public ResponseEntity<ValidationResult> create(Principal principal, @RequestBody Customer customer) {
        LOG.debug("Creating Customer: " + customer);

        Long id = customerService.create(customer, principal.getName());

        return ResponseEntity.created(buildResourceUri(API_CUSTOMER_PATH, id)).build();
    }

    @GetMapping
    public List<CustomerDetails> list() {
        LOG.debug("Retrieving all Customer Details");

        return customerService.list();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerDetails> details(@PathVariable Long id) {
        LOG.debug("Getting details of Customer by id: " + id);

        CustomerDetails customerDetails = customerService.getDetailsById(id, buildResourceUri(API_CUSTOMER_IMAGE_PATH, id));
        return ResponseEntity.ok(customerDetails);
    }


    @PutMapping
    public ResponseEntity<ValidationResult> update(Principal principal, @RequestBody Customer customer) {
        LOG.debug("Updating Customer: " + customer);

        Long id = customerService.update(customer, principal.getName());
        return ResponseEntity.ok().location(buildResourceUri(API_CUSTOMER_PATH, id)).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        LOG.debug("Deleting Customer by ID: " + id);

        customerService.delete(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/image/{id}")
    public ResponseEntity<ValidationResult> uploadImage(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        LOG.debug("Uploading Customer Image by ID: {}", id);

        try {
            Long imageId = customerService.uploadImage(id, file);
            return ResponseEntity.created(buildResourceUri(API_CUSTOMER_IMAGE_PATH, id)).build();
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

    @DeleteMapping("/image/{id}")
    public ResponseEntity<Void> deleteImage(@PathVariable Long id) {
        LOG.debug("Deleting Customer Image by ID: {}", id);

        customerService.deleteImage(id);
        return ResponseEntity.ok().build();
    }

    private URI buildResourceUri(String path, Long id) {
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(path + "/{id}")
                .buildAndExpand(id)
                .toUri();
    }
}
