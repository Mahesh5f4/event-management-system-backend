package com.eventhub.backend.event.controller;

import com.eventhub.backend.common.response.ApiResponse;
import com.eventhub.backend.event.dto.request.EventRequest;
import com.eventhub.backend.event.dto.response.EventResponse;
import com.eventhub.backend.event.service.EventService;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/events")
public class EventController {

    private final EventService service;

    public EventController(EventService service) {
        this.service = service;
    }

    // 🔥 CREATE
    @PostMapping
    public ApiResponse<EventResponse> createEvent(@RequestBody EventRequest req) {
        return new ApiResponse<>(
                true,
                service.createEvent(req),
                "Event created successfully"
        );
    }

    // 🔥 GET ALL
    @GetMapping
    public ApiResponse<Page<EventResponse>> getEvents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        return new ApiResponse<>(
                true,
                service.getEvents(page, size),
                "Events fetched successfully"
        );
    }

    // 🔥 GET BY ID
    @GetMapping("/{id}")
    public ApiResponse<EventResponse> getEventById(@PathVariable Long id) {
        return new ApiResponse<>(
                true,
                service.getEventById(id),
                "Event fetched successfully"
        );
    }

    // 🔥 UPDATE
    @PatchMapping("/{id}")
    public ApiResponse<EventResponse> updateEvent(
            @PathVariable Long id,
            @RequestBody EventRequest req
    ) {
        return new ApiResponse<>(
                true,
                service.updateEvent(id, req),
                "Event updated successfully"
        );
    }

    // 🔥 DELETE
    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteEvent(@PathVariable Long id) {
        service.deleteEvent(id);
        return new ApiResponse<>(
                true,
                null,
                "Event deleted successfully"
        );
    }
}