package com.proiectcolectiv.demo.service.impl;

import com.proiectcolectiv.demo.exception.eventParticipation.EventParticipationNotFound;
import com.proiectcolectiv.demo.model.EventParticipation;
import com.proiectcolectiv.demo.repository.EventParticipationRepository;
import com.proiectcolectiv.demo.service.EventParticipationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EventParticipationServiceImpl implements EventParticipationService {

    private final EventParticipationRepository eventParticipationRepository;

    @Override
    public List<EventParticipation> getAllEventParticipationByUserId(UUID userId) throws EventParticipationNotFound {
        return eventParticipationRepository.findAllByUserId(userId);
    }

    @Override
    public EventParticipation createEventParticipation(EventParticipation eventParticipation) {
        return eventParticipationRepository.save(eventParticipation);
    }

}
