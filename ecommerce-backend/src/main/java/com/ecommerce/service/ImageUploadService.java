package com.ecommerce.service;

import com.cloudinary.Cloudinary;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class ImageUploadService {

    private final Cloudinary cloudinary;

    public String uploadImage(
            MultipartFile file
    ) throws IOException {

        Map uploadResult =
                cloudinary.uploader().upload(
                        file.getBytes(),
                        Map.of()
                );

        return uploadResult
                .get("secure_url")
                .toString();
    }
}