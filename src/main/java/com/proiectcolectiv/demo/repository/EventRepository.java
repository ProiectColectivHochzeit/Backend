package com.proiectcolectiv.demo.repository;

import com.proiectcolectiv.demo.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.UUID;

public interface EventRepository extends JpaRepository<Event, UUID> {
}
