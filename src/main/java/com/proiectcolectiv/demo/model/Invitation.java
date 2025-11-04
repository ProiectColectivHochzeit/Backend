package com.proiectcolectiv.demo.model;

import com.proiectcolectiv.demo.model.enums.Status;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Data
@Table(name = "invivtations")
@NoArgsConstructor
@AllArgsConstructor
public class Invitation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "guest_email", nullable = false)
    @NotBlank(message = "Guest email is required")
    @Email(message = "Guest email must be valid")
    @Size(max = 254, message = "Guest email must be less than 254 characters")
    private String guestEmail;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;
}
