package com.example.job.domain;

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

// lower case table names is configured by the msql server and will be auto changed to lower case
// what is the norm here?
@Entity
@Table(name = "job")
public class Job {

    //Long to enable null for jobid, which is the case until the job is created
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    // @size not needed here. used by hibernate validator and throws javax.validation.ConstraintViolationException
    // but without making use of that and with restrictions in frontend its redundant here
    // was a wrong approach that brought me here
    @Column(length = 100)
    @Size(max = 100, message = "Name must be at most 50 characters")
    @NotBlank(message = "Name is required")
    private String name;

    @Column(length = 1000)
    @Size(max = 1000, message = "Description must be at most 200 characters")
    @NotBlank(message = "Name is required")
    private String description;

    @Column(name = "command")
    private String command;
    private Boolean status;

    @Column(name = "startDate")
    private LocalDateTime startDate;

    @Column(name = "endDate")
    private LocalDateTime endDate;

    @Column(name = "cronExpression")
    private String cronExpression;

    @JsonIgnore
    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL)
    private List<Execution> history;

    //initiate history here, or having to check for null everytime and then initiate?
    //won't stay empty usually, so initiated here
    public Job(long id, String name, String description, String command, boolean status, LocalDateTime startDate,
               LocalDateTime endDate, String cronExpression) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.command = command;
        this.status = status;
        this.startDate = startDate;
        this.endDate = endDate;
        this.cronExpression = cronExpression;
        this.history = new ArrayList<>();
    }

    public Job() {

    }

    public String getCronExpression() {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
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

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
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
                ", status=" + status +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", history=" + history +
                '}';
    }
}
