package com.github.ivanfomkin.bookshop.config;

import com.cloudinary.Cloudinary;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class CloudinaryConfig {
    @Value("${bookshop.cloudinary.apiSecret}")
    private String apiSecret;
    @Value("${bookshop.cloudinary.apiKey}")
    private String apiKey;
    @Value("${bookshop.cloudinary.cloudName}")
    private String cloudName;

    @Bean
    public Cloudinary cloudinary() {
        return new Cloudinary(Map.of(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret
        ));
    }
}
