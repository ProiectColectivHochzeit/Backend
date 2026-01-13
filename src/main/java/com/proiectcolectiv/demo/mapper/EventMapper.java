package com.proiectcolectiv.demo.mapper;

import com.proiectcolectiv.demo.dto.Event.EventResponseDTO;
import com.proiectcolectiv.demo.model.Event;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EventMapper {

    @Mapping(target = "organizerID", ignore = true)
    EventResponseDTO eventToEventResponseDTO(Event event);

    Event eventResponseDTOToEvent(EventResponseDTO eventResponseDTO);
}
