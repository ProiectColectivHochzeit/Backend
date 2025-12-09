// Java
package com.proiectcolectiv.demo.controller;

import com.proiectcolectiv.demo.service.CloudinaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/photos")
@RequiredArgsConstructor
public class PhotoController {

    private final CloudinaryService cloudinaryService;


    @PostMapping
    public ResponseEntity<?> uploadPhoto(@RequestParam("file") MultipartFile file,
                                         @RequestParam(value = "eventId", required = false) String eventId) {
        try {
            Map<?, ?> result = cloudinaryService.upload(file);
            return ResponseEntity.ok(Map.of(
                    "url", result.get("secure_url"),
                    "publicId", result.get("public_id"),
                    "raw", result
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (IOException e) {
            return ResponseEntity.status(500).body(Map.of("error", "Upload failed", "details", e.getMessage()));
        }
    }

    @DeleteMapping("/{publicId}")
    public ResponseEntity<?> deletePhoto(@PathVariable String publicId) {
        try {
            Map<?, ?> result = cloudinaryService.delete(publicId);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (IOException e) {
            return ResponseEntity.status(500).body(Map.of("error", "Delete failed", "details", e.getMessage()));
        }
    }
}
