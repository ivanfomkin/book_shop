package com.github.ivanfomkin.bookshop.service.impl;

import com.cloudinary.Cloudinary;
import com.github.ivanfomkin.bookshop.repository.BookFileRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Primary
@Slf4j
@Service
public class ResourceStorageServiceCloudinaryImpl extends ResourceStorageServiceLocalFileImpl {

    private final Cloudinary cloudinary;

    public ResourceStorageServiceCloudinaryImpl(BookFileRepository bookFileRepository, Cloudinary cloudinary) {
        super(bookFileRepository);
        this.cloudinary = cloudinary;
    }

    @Override
    public String saveNewEntityImage(MultipartFile file, String slug) throws IOException {
        var cloudinaryUploadResult = cloudinary.uploader().upload(file.getBytes(), Map.of());
        return cloudinaryUploadResult.get("url").toString();
    }
}
