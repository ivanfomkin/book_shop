package com.github.ivanfomkin.bookshop.service.impl;

import com.github.ivanfomkin.bookshop.entity.book.BookEntity;
import com.github.ivanfomkin.bookshop.entity.book.file.BookFileEntity;
import com.github.ivanfomkin.bookshop.entity.enums.BookFiletype;
import com.github.ivanfomkin.bookshop.repository.BookFileRepository;
import com.github.ivanfomkin.bookshop.service.ResourceStorageService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Slf4j
@Service
public class ResourceStorageServiceLocalFileImpl implements ResourceStorageService {
    @Value("${bookshop.upload.path}")
    private String uploadPath;

    private final BookFileRepository bookFileRepository;
    @Value("${bookshop.download.path}")
    private String bookFilesPath;

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
        Path path = Paths.get(bookFilesPath, bookFileFromDb.getPath());
        return Files.readAllBytes(path);
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void saveBookFile(MultipartFile file, BookEntity book, BookFiletype filetype) throws IOException {
        if (file != null && !file.isEmpty()) {
            if (!new File(bookFilesPath).exists()) {
                Path dstDir = Files.createDirectories(Path.of(bookFilesPath));
                log.info("Created new directory: {}", dstDir);
            }
            var filename = FilenameUtils.removeExtension(file.getOriginalFilename()) + filetype.getFileExtension();
            var dstPath = Paths.get(bookFilesPath, filename);
            file.transferTo(dstPath);
            BookFileEntity bookFileEntity = bookFileRepository.findBookFileEntityByBookAndType(book, filetype);
            if (bookFileEntity == null) {
                bookFileEntity = new BookFileEntity();
            }
            bookFileEntity.setHash(UUID.randomUUID().toString().replace("-", ""));
            bookFileEntity.setBook(book);
            bookFileEntity.setPath(filename);
            bookFileEntity.setTypeId(filetype.getFileTypeId());
            bookFileRepository.save(bookFileEntity);
        }

    }
}
