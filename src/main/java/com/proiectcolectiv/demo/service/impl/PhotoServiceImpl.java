package com.proiectcolectiv.demo.service.impl;

import com.proiectcolectiv.demo.dto.photo.PhotoResponseDTO;
import com.proiectcolectiv.demo.model.Event;
import com.proiectcolectiv.demo.model.Photo;
import com.proiectcolectiv.demo.model.User;
import com.proiectcolectiv.demo.repository.EventRepository;
import com.proiectcolectiv.demo.repository.PhotoRepository;
import com.proiectcolectiv.demo.repository.UserRepository;
import com.proiectcolectiv.demo.service.CloudinaryService;
import com.proiectcolectiv.demo.service.PhotoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class PhotoServiceImpl implements PhotoService {

    private final CloudinaryService cloudinaryService;
    private final PhotoRepository photoRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Override
    public PhotoResponseDTO uploadPhoto(MultipartFile file, UUID eventId, UUID userId) throws IOException {
        log.info("Uploading photo for event: {} by user: {}", eventId, userId);

        // Upload to Cloudinary
        Map<?, ?> cloudinaryResult = cloudinaryService.upload(file);
        String url = (String) cloudinaryResult.get("secure_url");
        String publicId = (String) cloudinaryResult.get("public_id");

        // Get user and event
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found with id: " + eventId));

        // Create and save photo entity
        Photo photo = new Photo();
        photo.setUser(user);
        photo.setEvent(event);
        photo.setUrl(url);
        photo.setPublicId(publicId);
        photo.setUploadedAt(LocalDate.now());

        Photo savedPhoto = photoRepository.save(photo);
        log.info("Photo saved with id: {}", savedPhoto.getId());

        // Convert to DTO
        return toDTO(savedPhoto);
    }

    @Override
    public List<PhotoResponseDTO> getPhotosByEventId(UUID eventId) {
        log.info("Fetching photos for event: {}", eventId);
        List<Photo> photos = photoRepository.findByEventId(eventId);
        return photos.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void deletePhoto(String publicId, UUID userId) throws IOException {
        log.info("Deleting photo with publicId: {} by user: {}", publicId, userId);
        
        // Find photo by publicId
        Photo photo = photoRepository.findAll().stream()
                .filter(p -> p.getPublicId().equals(publicId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Photo not found with publicId: " + publicId));

        // Verify user owns the photo
        if (!photo.getUser().getId().equals(userId)) {
            throw new RuntimeException("User not authorized to delete this photo");
        }

        // Delete from Cloudinary
        cloudinaryService.delete(publicId);

        // Delete from database
        photoRepository.delete(photo);
        log.info("Photo deleted successfully");
    }

    private PhotoResponseDTO toDTO(Photo photo) {
        String uploaderName = photo.getUser().getFirstName() != null && photo.getUser().getLastName() != null
                ? photo.getUser().getFirstName() + " " + photo.getUser().getLastName()
                : photo.getUser().getEmail();

        return new PhotoResponseDTO(
                photo.getId(),
                photo.getUrl(),
                photo.getPublicId(),
                uploaderName,
                photo.getUploadedAt()
        );
    }
}
