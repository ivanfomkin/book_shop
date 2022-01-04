package com.example.MyBookShopApp.data.impl;

import com.example.MyBookShopApp.data.ResourceStorage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@Service
public class ResourceStorageImpl implements ResourceStorage {
    @Value("${bookshop.upload.path}")
    private String uploadPath;

    @Override
    public String saveNewBookImage(MultipartFile file, String slug) throws IOException {
        String resourceUri = null;
        if (file != null && !file.isEmpty()) {
            if (!new File(uploadPath).exists()) {
                Path dstDir = Files.createDirectories(Path.of(uploadPath));
                log.info("Created new directory: {}", dstDir);
            }
            var filename = slug + "." + FilenameUtils.getExtension(file.getOriginalFilename());
            var dstPath = Paths.get(uploadPath, filename);
            resourceUri = "/book-covers/" + filename; // TODO: 04.01.2022 Вынести в конфиг book-covers
            file.transferTo(dstPath);
        }
        return resourceUri;
    }
}
