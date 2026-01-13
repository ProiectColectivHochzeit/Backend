package com.proiectcolectiv.demo.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.proiectcolectiv.demo.service.CloudinaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CloudinaryServiceImpl implements CloudinaryService {

    private final Cloudinary cloudinary;



    @Override
    public Map<?, ?> upload(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is required");
        }
        return cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap("resource_type", "image"));
    }


    @Override
    public Map<?, ?> delete(String publicId) throws IOException {
        if (publicId == null || publicId.isBlank()) {
            throw new IllegalArgumentException("publicId is required");
        }
        return cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
    }
}
