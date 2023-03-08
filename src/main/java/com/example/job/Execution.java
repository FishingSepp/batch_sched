package com.example.job;

import jakarta.persistence.*;

@Entity
@Table(name = "execution", schema  = "jobapi")
public class Execution {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "execution_id")
    private Long execution_id;

    @Column(name = "job_id", insertable = false, updatable = false)
    private Long job_id;

    @Column(name = "success")
    private Boolean success;

    @Column(name = "exit_code")
    private Integer exit_code;

    @Column(name = "output")
    private String output;

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

    public Execution(boolean success, int exit_code, String output, Job job) {
        this.success = success;
        this.exit_code = exit_code;
        this.output = output;
        this.job = job;
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
