package com.proiectcolectiv.demo.repository;

import com.proiectcolectiv.demo.model.EventParticipation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface EventParticipationRepository extends JpaRepository<EventParticipation, UUID> {
    @Query("SELECT ep FROM EventParticipation ep WHERE ep.user.id = :userId")
    List<EventParticipation> findAllByUserId(@Param("userId") UUID userId);

}
