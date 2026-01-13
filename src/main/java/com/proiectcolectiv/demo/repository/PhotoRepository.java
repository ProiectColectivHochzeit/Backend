package com.proiectcolectiv.demo.repository;

import com.proiectcolectiv.demo.model.Photo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PhotoRepository extends JpaRepository<Photo, UUID> {
    List<Photo> findByEventId(UUID eventId);
}
