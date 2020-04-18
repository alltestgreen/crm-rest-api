package com.abara.service;

import com.abara.entity.Customer;
import com.abara.model.CustomerDetails;
import com.abara.model.CustomerImage;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.util.List;

public interface CustomerService {

    Long create(Customer customer, String createdBy);

    List<CustomerDetails> list();

    CustomerDetails getDetailsById(Long id, URI imageURI);

    Long update(Customer customer, String updatedBy);

    void delete(Long id);

    String uploadImage(Long id, MultipartFile file) throws IOException;

    CustomerImage getImageById(Long id) throws IOException;

    void deleteImage(Long id) throws IOException;
}
