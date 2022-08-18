package com.github.ivanfomkin.bookshop.service;

import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;

public interface ResourceStorageService {
    String saveNewBookImage(MultipartFile file, String slug) throws IOException;

    Path getBookPathByHash(String hash);

    MediaType getBookFileMime(String hash);

    byte[] getBookFileByteArray(String hash) throws IOException;
}
