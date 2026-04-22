package com.eventhub.backend.event.dto.response;

import java.io.Serializable;
import java.time.LocalDateTime;

public class EventResponse implements Serializable {

    private Long id;
    private String title;
    private String location;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Double price;

    private Integer availableSeats;
    private Integer totalSeats; // 🔥 ADDED

    public EventResponse(Long id,
                         String title,
                         String location,
                         LocalDateTime startTime,
                         LocalDateTime endTime,
                         Double price,
                         Integer availableSeats,
                         Integer totalSeats) {

        this.id = id;
        this.title = title;
        this.location = location;
        this.startTime = startTime;
        this.endTime = endTime;
        this.price = price;
        this.availableSeats = availableSeats;
        this.totalSeats = totalSeats;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getLocation() { return location; }
    public LocalDateTime getStartTime() { return startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public Double getPrice() { return price; }

    public Integer getAvailableSeats() { return availableSeats; }
    public Integer getTotalSeats() { return totalSeats; } // 🔥 ADDED
}