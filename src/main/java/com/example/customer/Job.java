package com.example.customer;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import jakarta.persistence.*;

@Entity
public class Job {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    private Boolean status;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalDateTime nextDate;
    private Boolean repeat;

    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL)
    private List<Execution> history = new ArrayList<>();


    public Job(long id, String name, String description, boolean status, LocalDateTime startDate,
               LocalDateTime endDate, LocalDateTime nextDate, boolean repeat) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
        this.startDate = startDate;
        this.endDate = endDate;
        this.nextDate = nextDate;
        this.repeat = repeat;
        this.history = new ArrayList<>();
    }

    public Job() {

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public LocalDateTime getNextDate() {
        return nextDate;
    }

    public void setNextDate(LocalDateTime nextDate) {
        this.nextDate = nextDate;
    }

    public boolean isRepeat() {
        return repeat;
    }

    public void setRepeat(boolean repeat) {
        this.repeat = repeat;
    }

    public List<Execution> getHistory() {
        return history;
    }

    public void setHistory(List<Execution> history) {
        this.history = history;
    }

    @Override
    public String toString() {
        return "Job{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}