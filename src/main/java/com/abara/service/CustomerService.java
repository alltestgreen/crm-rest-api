package com.abara.service;

import com.abara.entity.Customer;
import com.abara.entity.CustomerImage;
import com.abara.model.CustomerDetails;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

public interface CustomerService {

    Long create(Customer customer, String createdBy);

    Map<Long, String> list();

    CustomerDetails getDetailsById(Long id, URI uri);

    Long update(Customer customer, String updatedBy);

    void delete(Long id);

    Long uploadImage(Long id, MultipartFile file) throws IOException;

    CustomerImage getImageById(Long id);

    void deleteImage(Long id);
}
