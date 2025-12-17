package com.proiectcolectiv.demo.dto.user;

import com.proiectcolectiv.demo.model.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@AllArgsConstructor
@Data
public class InvitedUserResponseDTO {
    private UUID id;
    private String name;
    private String email;
    private Status status;

}
