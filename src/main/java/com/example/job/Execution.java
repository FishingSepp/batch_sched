package com.example.job;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "execution", schema  = "jobapi")
public class Execution {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "execution_id")
    private Long execution_id;

    @Column(name = "job_id", insertable = false, updatable = false)
    private Long job_id;

    private Boolean success;
    private Integer exit_code;
    private String output;

    private LocalDateTime start_time;
    private LocalDateTime end_time;

    //FetchType.LAZY would be better but running into errors with it
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "job_id", referencedColumnName = "job_id")
    private Job job;

    public Execution() {
    }

    public Long getExecution_id() {
        return execution_id;
    }

    public void setExecution_id(Long eid) {
        this.execution_id = eid;
    }

    public Execution(boolean success, int exit_code, String output, LocalDateTime start_time, LocalDateTime end_time, Job job) {
        this.success = success;
        this.exit_code = exit_code;
        this.output = output;
        this.job = job;
        this.start_time = start_time;
        this.end_time = end_time;
    }

    public Long getJobId() {
        return job_id;
    }

    public void setJobId(Long job_id) {
        this.job_id = job_id;
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

    public int getExit_code() {
        return exit_code;
    }

    public void setExit_code(int exit_code) {
        this.exit_code = exit_code;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public LocalDateTime getStart_time() {
        return start_time;
    }

    public void setStart_time(LocalDateTime start_time) {
        this.start_time = start_time;
    }

    public LocalDateTime getEnd_time() {
        return end_time;
    }

    public void setEnd_time(LocalDateTime end_time) {
        this.end_time = end_time;
    }

    @Override
    public String toString() {
        return "Execution{" +
                "eid=" + execution_id +
                ", success=" + success +
                ", exit_code=" + exit_code +
                ", output='" + output + '\'' +
                ", job=" + job +
                '}';
    }
}
