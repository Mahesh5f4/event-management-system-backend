package com.eventhub.backend.event.service;

import com.eventhub.backend.common.exception.BadRequestException;
import com.eventhub.backend.common.exception.ResourceNotFoundException;
import com.eventhub.backend.event.dto.request.EventRequest;
import com.eventhub.backend.event.dto.response.EventResponse;
import com.eventhub.backend.event.entity.Event;
import com.eventhub.backend.event.repository.EventRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

@Service
public class EventService {

    private final EventRepository repo;

    public EventService(EventRepository repo) {
        this.repo = repo;
    }

    @CacheEvict(value = "eventsPage", allEntries = true)
    public EventResponse createEvent(EventRequest req) {
        validateRequest(req);

        Event event = new Event();
        mapRequestToEntity(req, event);
        event.setAvailableSeats(req.getTotalSeats());

        Event saved = repo.save(event);
        return mapToResponse(saved);
    }

    // changed: removed @Cacheable for now
    public Page<EventResponse> getEvents(int page, int size) {
        System.out.println("DB HIT: getEvents");
        Pageable pageable = PageRequest.of(page, size, Sort.by("startTime").ascending());
        return repo.findAll(pageable).map(this::mapToResponse);
    }

    @Cacheable(value = "eventById", key = "#id")
    public EventResponse getEventById(Long id) {
        System.out.println("DB HIT: " + id);
        Event event = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));
        return mapToResponse(event);
    }

    @Caching(evict = {
            @CacheEvict(value = "eventById", key = "#id"),
            @CacheEvict(value = "eventsPage", allEntries = true)
    })
    public EventResponse updateEvent(Long id, EventRequest req) {

        if (isEmptyUpdate(req)) {
            throw new BadRequestException("At least one field must be provided for update");
        }

        Event event = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

        if (req.getTitle() != null) event.setTitle(req.getTitle());
        if (req.getDescription() != null) event.setDescription(req.getDescription());
        if (req.getLocation() != null) event.setLocation(req.getLocation());
        if (req.getStartTime() != null) event.setStartTime(req.getStartTime());
        if (req.getEndTime() != null) event.setEndTime(req.getEndTime());

        if (req.getPrice() != null) {
            if (req.getPrice() < 0) {
                throw new BadRequestException("Price must be valid");
            }
            event.setPrice(req.getPrice());
        }

        if (req.getTotalSeats() != null) {
            int oldTotal = event.getTotalSeats();
            int newTotal = req.getTotalSeats();

            int booked = oldTotal - event.getAvailableSeats();

            if (newTotal < booked) {
                throw new BadRequestException("Cannot reduce seats below booked");
            }

            int diff = newTotal - oldTotal;
            event.setTotalSeats(newTotal);
            event.setAvailableSeats(event.getAvailableSeats() + diff);
        }

        if (event.getStartTime() != null && event.getEndTime() != null) {
            if (event.getEndTime().isBefore(event.getStartTime())) {
                throw new BadRequestException("End time must be after start time");
            }
        }

        Event updated = repo.save(event);
        return mapToResponse(updated);
    }

    @Caching(evict = {
            @CacheEvict(value = "eventById", key = "#id"),
            @CacheEvict(value = "eventsPage", allEntries = true)
    })
    public void deleteEvent(Long id) {
        Event event = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

        repo.delete(event);
    }

    private void validateRequest(EventRequest req) {
        if (req.getStartTime() == null || req.getEndTime() == null) {
            throw new BadRequestException("Start time and End time are required");
        }

        if (req.getEndTime().isBefore(req.getStartTime())) {
            throw new BadRequestException("End time must be after start time");
        }

        if (req.getPrice() == null || req.getPrice() < 0) {
            throw new BadRequestException("Price must be valid");
        }

        if (req.getTotalSeats() == null || req.getTotalSeats() <= 0) {
            throw new BadRequestException("Total seats must be greater than 0");
        }
    }

    private boolean isEmptyUpdate(EventRequest req) {
        return req.getTitle() == null &&
                req.getDescription() == null &&
                req.getLocation() == null &&
                req.getStartTime() == null &&
                req.getEndTime() == null &&
                req.getPrice() == null &&
                req.getTotalSeats() == null;
    }

    private void mapRequestToEntity(EventRequest req, Event event) {
        event.setTitle(req.getTitle());
        event.setDescription(req.getDescription());
        event.setLocation(req.getLocation());
        event.setStartTime(req.getStartTime());
        event.setEndTime(req.getEndTime());
        event.setPrice(req.getPrice());
        event.setTotalSeats(req.getTotalSeats());
    }

    private EventResponse mapToResponse(Event e) {
        return new EventResponse(
                e.getId(),
                e.getTitle(),
                e.getLocation(),
                e.getStartTime(),
                e.getEndTime(),
                e.getPrice(),
                e.getAvailableSeats(),
                e.getTotalSeats() // 🔥 THIS LINE FIXES EVERYTHING
        );
    }
    }
