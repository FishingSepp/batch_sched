package com.example.customer;


import jakarta.persistence.*;

@Entity
@IdClass(ExecutionId.class)
public class Execution {

    private Boolean success;
    private Integer exitCode;
    private String output;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id")
    private Job job;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long executionId;

    public Execution() {
    }

    public Execution(boolean success, int exitCode, String output, Job job) {
        this.success = success;
        this.exitCode = exitCode;
        this.output = output;
        this.job = job;
    }

    public boolean isSuccess() {
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
}
