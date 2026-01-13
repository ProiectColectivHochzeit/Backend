package com.proiectcolectiv.demo.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

public interface CloudinaryService {
    public Map<?, ?> upload(MultipartFile file) throws IOException;
    public Map<?, ?> delete(String publicId) throws IOException;

}
