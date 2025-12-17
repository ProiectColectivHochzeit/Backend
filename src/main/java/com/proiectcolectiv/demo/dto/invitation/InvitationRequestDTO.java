package com.proiectcolectiv.demo.dto.invitation;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvitationRequestDTO {

    @NotBlank
    private String eventId;

    private String currentUserId;

    @NotBlank(message = "Guest email is required")
    @Email(message = "Guest email must be valid")
    @Size(max = 254, message = "Guest email must be less than 254 characters")
    private String guestEmail;



}
