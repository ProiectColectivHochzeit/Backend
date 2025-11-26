package com.proiectcolectiv.demo.service.impl;

import com.proiectcolectiv.demo.exception.eventOrganizer.EventOrganizerNotFoundException;
import com.proiectcolectiv.demo.model.EventOrganizer;
import com.proiectcolectiv.demo.repository.EventOrganizerRepository;
import com.proiectcolectiv.demo.service.EventOrganizerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EventOrganizerServiceImpl implements EventOrganizerService {

    private final EventOrganizerRepository eventOrganizerRepository;

    @Override
    public List<EventOrganizer> getAllEventOrganizerByUserId(UUID userId) throws EventOrganizerNotFoundException {
        List<EventOrganizer> eventOrganizerList =  eventOrganizerRepository.findAllByUserId(userId);
        if (eventOrganizerList.isEmpty()) {
            throw new EventOrganizerNotFoundException();
        }
        return eventOrganizerList;
    }

    @Override
    public EventOrganizer createEventOrganizer(EventOrganizer eventOrganizer) {
        return eventOrganizerRepository.save(eventOrganizer);
    }
}
