package com.proiectcolectiv.demo.controller;

import com.proiectcolectiv.demo.dto.photo.PhotoResponseDTO;
import com.proiectcolectiv.demo.service.PhotoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/photos")
@RequiredArgsConstructor
public class PhotoController {

    private final PhotoService photoService;

    @PostMapping
    public ResponseEntity<?> uploadPhoto(
            @RequestParam("file") MultipartFile file,
            @RequestParam("eventId") String eventId,
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        try {
            UUID userId = extractUserIdFromToken(authorization);
            if (userId == null) {
                return ResponseEntity.status(401).body(Map.of("error", "Unauthorized: Invalid or missing token"));
            }

            UUID eventUUID = UUID.fromString(eventId);
            PhotoResponseDTO photo = photoService.uploadPhoto(file, eventUUID, userId);
            
            return ResponseEntity.ok(Map.of(
                    "id", photo.getId().toString(),
                    "url", photo.getUrl(),
                    "publicId", photo.getPublicId(),
                    "uploaderName", photo.getUploaderName(),
                    "uploadedAt", photo.getUploadedAt().toString()
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (IOException e) {
            return ResponseEntity.status(500).body(Map.of("error", "Upload failed", "details", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/event/{eventId}")
    public ResponseEntity<List<PhotoResponseDTO>> getPhotosByEventId(@PathVariable String eventId) {
        try {
            UUID eventUUID = UUID.fromString(eventId);
            List<PhotoResponseDTO> photos = photoService.getPhotosByEventId(eventUUID);
            return ResponseEntity.ok(photos);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @DeleteMapping("/{publicId}")
    public ResponseEntity<?> deletePhoto(
            @PathVariable String publicId,
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        try {
            UUID userId = extractUserIdFromToken(authorization);
            if (userId == null) {
                return ResponseEntity.status(401).body(Map.of("error", "Unauthorized: Invalid or missing token"));
            }

            photoService.deletePhoto(publicId, userId);
            return ResponseEntity.ok(Map.of("message", "Photo deleted successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (IOException e) {
            return ResponseEntity.status(500).body(Map.of("error", "Delete failed", "details", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    private UUID extractUserIdFromToken(String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return null;
        }

        try {
            String token = authorization.substring(7); // Remove "Bearer " prefix
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                return null;
            }

            // Decode the payload (second part)
            String payload = new String(Base64.getUrlDecoder().decode(parts[1]));
            // Parse JSON to get "sub" claim
            // Simple JSON parsing for "sub" field
            int subIndex = payload.indexOf("\"sub\":\"");
            if (subIndex == -1) {
                return null;
            }
            int startIndex = subIndex + 7;
            int endIndex = payload.indexOf("\"", startIndex);
            if (endIndex == -1) {
                return null;
            }
            String userIdString = payload.substring(startIndex, endIndex);
            return UUID.fromString(userIdString);
        } catch (Exception e) {
            return null;
        }
    }
}
