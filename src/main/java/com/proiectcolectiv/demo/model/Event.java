package com.proiectcolectiv.demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Data
@Table(name = "events")
@NoArgsConstructor
@AllArgsConstructor
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column
    @Size(max = 100, message = "Events name must be less than 100 characters")
    private String name;

    @Column(nullable = false)
    @FutureOrPresent
    private LocalDate startingDate;

    @Column(nullable = false)
    @FutureOrPresent
    private LocalDate endDate;

    @Column(nullable = false)
    @Size(max = 100, message = "Location's name must be less than 100 characters")
    private String location;
}
