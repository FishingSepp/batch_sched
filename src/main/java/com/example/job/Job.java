package com.example.job;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;


@Entity
@Table(name = "job")
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "job_id")
    private Long job_id;

    @NotBlank(message = "Name is required")
    @Size(max = 50, message = "Name must be at most 50 characters")
    private String name;

    @NotBlank(message = "Name is required")
    @Size(max = 200, message = "Description must be at most 200 characters")
    private String description;

    private Boolean repeated;
    private Boolean status;
    private LocalDateTime start_date;
    private LocalDateTime end_date;
    private LocalDateTime next_date;

    @JsonIgnore
    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL)
    private List<Execution> history = new ArrayList<>();


    public Job(long job_id, String name, String description, boolean status, LocalDateTime start_date,
               LocalDateTime end_date, LocalDateTime next_date, boolean repeated) {
        this.job_id = job_id;
        this.name = name;
        this.description = description;
        this.status = status;
        this.start_date = start_date;
        this.end_date = end_date;
        this.next_date = next_date;
        this.repeated = repeated;
        this.history = new ArrayList<>();
    }

    public Job() {

    }

    public long getJob_id() {
        return job_id;
    }

    public void setJob_id(long id) {
        this.job_id = id;
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

    public LocalDateTime getStart_date() {
        return start_date;
    }

    public void setStart_date(LocalDateTime startDate) {
        this.start_date = startDate;
    }

    public LocalDateTime getEnd_date() {
        return end_date;
    }

    public void setEnd_date(LocalDateTime endDate) {
        this.end_date = endDate;
    }

    public LocalDateTime getNext_date() {
        return next_date;
    }

    public void setNext_date(LocalDateTime nextDate) {
        this.next_date = nextDate;
    }

    public boolean isRepeated() {
        return repeated;
    }

    public void setRepeated(boolean repeated) {
        this.repeated = repeated;
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
                "jid=" + job_id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", startDate=" + start_date +
                ", endDate=" + end_date +
                ", nextDate=" + next_date +
                ", repeated=" + repeated +
                ", history=" + history +
                '}';
    }
}
