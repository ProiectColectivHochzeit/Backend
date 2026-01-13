package com.proiectcolectiv.demo.dto.Event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventResponseDTO {
    private UUID id;
    private String name;
    private LocalDate startingDate;
    private LocalDate endDate;
    private String location;
    private String organizerID;
}
