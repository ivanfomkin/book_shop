package com.example.MyBookShopApp.data;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ResourceStorage {
    String saveNewBookImage(MultipartFile file, String slug) throws IOException;
}
