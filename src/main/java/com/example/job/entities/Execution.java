package com.example.job.entities;


import jakarta.persistence.*;

@Entity
@Table(name = "execution", schema  = "jobapi")
public class Execution {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "execution_id")
    private Long eid;
    private Boolean success;
    private Integer exitCode;
    private String output;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id", referencedColumnName = "job_id")
    private Job job;

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

    @Override
    public String toString() {
        return "Execution{" +
                "eid=" + eid +
                ", success=" + success +
                ", exitCode=" + exitCode +
                ", output='" + output + '\'' +
                ", job=" + job +
                '}';
    }
}
