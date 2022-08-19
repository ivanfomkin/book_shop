package com.github.ivanfomkin.bookshop.service;

import com.github.ivanfomkin.bookshop.entity.book.BookEntity;
import com.github.ivanfomkin.bookshop.entity.enums.BookFiletype;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;

public interface ResourceStorageService {
    String saveNewBookImage(MultipartFile file, String slug) throws IOException;

    Path getBookPathByHash(String hash);

    MediaType getBookFileMime(String hash);

    byte[] getBookFileByteArray(String hash) throws IOException;

    void saveBookFile(MultipartFile file, BookEntity book, BookFiletype epub) throws IOException;
}
