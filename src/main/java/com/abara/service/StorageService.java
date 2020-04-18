package com.abara.service;

import com.abara.model.CustomerImage;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface StorageService {
    String storeImage(MultipartFile file) throws IOException;

    void removeImage(String location) throws IOException;

    CustomerImage getImage(String uuid) throws IOException;
}
