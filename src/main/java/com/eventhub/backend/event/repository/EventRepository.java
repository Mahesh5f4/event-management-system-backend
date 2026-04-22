package com.eventhub.backend.event.repository;

import com.eventhub.backend.event.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Long> {
}