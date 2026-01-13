package com.proiectcolectiv.demo.service;

import com.proiectcolectiv.demo.dto.photo.PhotoResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface PhotoService {
    PhotoResponseDTO uploadPhoto(MultipartFile file, UUID eventId, UUID userId) throws IOException;
    List<PhotoResponseDTO> getPhotosByEventId(UUID eventId);
    void deletePhoto(String publicId, UUID userId) throws IOException;
}
