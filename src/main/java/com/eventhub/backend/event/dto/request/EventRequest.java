package com.eventhub.backend.event.dto.request;

import java.time.LocalDateTime;

public class EventRequest {

    private String title;
    private String description;
    private String location;

    // 🔥 NEW FIELDS
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private Double price;

    private Integer totalSeats;

    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getLocation() { return location; }

    public LocalDateTime getStartTime() { return startTime; }
    public LocalDateTime getEndTime() { return endTime; }

    public Double getPrice() { return price; }

    public Integer getTotalSeats() { return totalSeats; }

    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setLocation(String location) { this.location = location; }

    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }

    public void setPrice(Double price) { this.price = price; }

    public void setTotalSeats(Integer totalSeats) { this.totalSeats = totalSeats; }
}