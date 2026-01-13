package com.proiectcolectiv.demo.repository;


import com.proiectcolectiv.demo.model.Invitation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface InvitationRepository extends JpaRepository<Invitation, UUID> {
}
