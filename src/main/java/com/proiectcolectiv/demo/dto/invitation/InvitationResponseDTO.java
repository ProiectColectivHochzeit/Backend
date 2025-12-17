package com.proiectcolectiv.demo.dto.invitation;

import com.proiectcolectiv.demo.model.Event;
import com.proiectcolectiv.demo.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvitationResponseDTO {
    private UUID id;
    private Event event;
    private User user;
    private String guestEmail;
    private String status;
}
