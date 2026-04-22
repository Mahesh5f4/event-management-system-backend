package com.eventhub.backend.event.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "events", indexes = {
        @Index(name = "idx_start_time", columnList = "startTime")
})
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String description;

    private String location;

    // 🔥 CORRECT MODEL
    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Double price;

    private Integer totalSeats;

    private Integer availableSeats;

    public Event() {}

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getLocation() { return location; }

    public LocalDateTime getStartTime() { return startTime; }
    public LocalDateTime getEndTime() { return endTime; }

    public Double getPrice() { return price; }

    public Integer getTotalSeats() { return totalSeats; }
    public Integer getAvailableSeats() { return availableSeats; }

    public void setId(Long id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setLocation(String location) { this.location = location; }

    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }

    public void setPrice(Double price) { this.price = price; }

    public void setTotalSeats(Integer totalSeats) { this.totalSeats = totalSeats; }
    public void setAvailableSeats(Integer availableSeats) { this.availableSeats = availableSeats; }
}