package com.example.job;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "execution")
public class Execution {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    private Boolean success;
    private Integer exitCode;

    //default value would create the table with varchar255 might be too short for the output
    @Column(name = "output", length = 10000)
    private String output;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    //FetchType.LAZY could be better? but running into errors with it
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "jobId", referencedColumnName = "id")
    private Job job;

    public Execution() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long eid) {
        this.id = eid;
    }

    public Execution(boolean success, int exitCode, String output, LocalDateTime startTime, LocalDateTime endTime, Job job) {
        this.success = success;
        this.exitCode = exitCode;
        this.output = output;
        this.job = job;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public void setJob(Job job) {
        this.job = job;
    }

    public Job getJob() {
        return job;
    }

    public boolean getSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getExitCode() {
        return exitCode;
    }

    public void setExitCode(int exitCode) {
        this.exitCode = exitCode;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return "Execution{" +
                "id=" + id +
                ", success=" + success +
                ", exitCode=" + exitCode +
                ", output='" + output + '\'' +
                ", job=" + job +
                '}';
    }
}
