package com.example.job.entities;

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
    private Long jid;

    @NotBlank(message = "Name is required")
    @Size(max = 50, message = "Name must be at most 50 characters")
    private String name;

    @NotBlank(message = "Name is required")
    @Size(max = 200, message = "Description must be at most 200 characters")
    private String description;

    private Boolean repeated;
    private Boolean status;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalDateTime nextDate;

    @JsonIgnore
    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL)
    private List<Execution> history = new ArrayList<>();


    public Job(long jid, String name, String description, boolean status, LocalDateTime startDate,
               LocalDateTime endDate, LocalDateTime nextDate, boolean repeated) {
        this.jid = jid;
        this.name = name;
        this.description = description;
        this.status = status;
        this.startDate = startDate;
        this.endDate = endDate;
        this.nextDate = nextDate;
        this.repeated = repeated;
        this.history = new ArrayList<>();
    }

    public Job() {

    }

    public long getJid() {
        return jid;
    }

    public void setJid(long id) {
        this.jid = id;
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

    public boolean isRepeated() {
        return repeated;
    }

    public void setRepeated(boolean repeat) {
        this.repeated = repeat;
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
                "jid=" + jid +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", nextDate=" + nextDate +
                ", repeat=" + repeated +
                ", history=" + history +
                '}';
    }
}
