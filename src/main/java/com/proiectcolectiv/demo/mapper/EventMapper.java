package com.proiectcolectiv.demo.mapper;

import com.proiectcolectiv.demo.dto.Event.EventResponseDTO;
import com.proiectcolectiv.demo.model.Event;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EventMapper {

    EventResponseDTO eventToEventResponseDTO(Event event);

    Event eventResponseDTOToEvent(EventResponseDTO eventResponseDTO);
}
