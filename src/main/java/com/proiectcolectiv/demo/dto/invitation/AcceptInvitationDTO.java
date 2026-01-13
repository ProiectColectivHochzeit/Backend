package com.proiectcolectiv.demo.dto.invitation;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Data
@RequiredArgsConstructor
public class AcceptInvitationDTO {
    private UUID invitationId;
    private UUID invitedUserId;
}
