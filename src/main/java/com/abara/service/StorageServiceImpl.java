package com.abara.service;

import com.abara.model.CustomerImage;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Component
public class StorageServiceImpl implements StorageService {

    private static final String IMAGE_STORE_PATH = "images/";

    @Override
    public String storeImage(MultipartFile file) throws IOException {

        String uuid = UUID.randomUUID().toString();

        File fileDirectory = new File(IMAGE_STORE_PATH + uuid);
        if (!fileDirectory.exists()) {
            fileDirectory.mkdirs();
        }

        if (file.isEmpty()) {
            throw new FileNotFoundException("File is empty");
        }

//        String fileName = file.getOriginalFilename();
//        String ext = fileName.substring(fileName.lastIndexOf('.') + 1);
        Path path = Paths.get(IMAGE_STORE_PATH + uuid + "/" + file.getOriginalFilename());
        Files.write(path, file.getBytes());
        return uuid;
    }

    @Override
    public void removeImage(String uuid) throws IOException {
        Path path = Paths.get(IMAGE_STORE_PATH + uuid);
        deleteDirectoryStream(path);
    }

    @Override
    public CustomerImage getImage(String uuid) throws IOException {
        File folder = new File(IMAGE_STORE_PATH + uuid);
        if (folder.exists()) {
            Optional<Path> path = Arrays.stream(Objects.requireNonNull(folder.listFiles()))
                    .findFirst()
                    .map(File::toPath);
            if (path.isPresent()) {
                Path imagePath = path.get();
                String type = Files.probeContentType(imagePath);
                byte[] data = Files.readAllBytes(imagePath);
                return new CustomerImage(type, data);
            }
        }
        return null;
    }

    void deleteDirectoryStream(Path path) throws IOException {
        Files.walk(path)
                .sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .forEach(File::delete);
    }
}
