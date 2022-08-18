package com.github.ivanfomkin.bookshop.service.impl;

import com.github.ivanfomkin.bookshop.service.ResourceStorageService;
import com.github.ivanfomkin.bookshop.repository.BookFileRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@Service
public class ResourceStorageServiceLocalFileImpl implements ResourceStorageService {
    @Value("${bookshop.upload.path}")
    private String uploadPath;

    private final BookFileRepository bookFileRepository;
    @Value("${bookshop.download.path}")
    private String downloadPath;

    public ResourceStorageServiceLocalFileImpl(BookFileRepository bookFileRepository) {
        this.bookFileRepository = bookFileRepository;
    }

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
            resourceUri = "/book-covers/" + filename;
            file.transferTo(dstPath);
        }
        return resourceUri;
    }

    @Override
    public Path getBookPathByHash(String hash) {
        var bookFileFromDb = bookFileRepository.findBookFileEntityByHash(hash);
        return Paths.get(bookFileFromDb.getPath());
    }

    @Override
    public MediaType getBookFileMime(String hash) {
        var bookFileFromDb = bookFileRepository.findBookFileEntityByHash(hash);
        var mimeType = URLConnection.guessContentTypeFromName(Paths.get(bookFileFromDb.getPath()).getFileName().toString());
        return mimeType == null ? MediaType.APPLICATION_OCTET_STREAM : MediaType.parseMediaType(mimeType);
    }

    @Override
    public byte[] getBookFileByteArray(String hash) throws IOException {
        var bookFileFromDb = bookFileRepository.findBookFileEntityByHash(hash);
        Path path = Paths.get(downloadPath, bookFileFromDb.getPath());
        return Files.readAllBytes(path);
    }
}
