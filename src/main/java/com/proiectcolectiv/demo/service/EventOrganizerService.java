package com.proiectcolectiv.demo.service;

import com.proiectcolectiv.demo.exception.eventOrganizer.EventOrganizerNotFoundException;
import com.proiectcolectiv.demo.model.EventOrganizer;

import java.util.List;
import java.util.UUID;

public interface EventOrganizerService {
    List<EventOrganizer> getAllEventOrganizerByUserId(UUID userId) throws EventOrganizerNotFoundException;
}
