package com.example.job;

import java.io.Serializable;
import java.util.Objects;
import jakarta.persistence.*;

public class ExecutionId implements Serializable {

    @Id
    private Long job;
    @Id
    private Long executionId;

    public ExecutionId() {
    }

    public ExecutionId(Long job, Long executionId) {
        this.job = job;
        this.executionId = executionId;
    }

    public Long getJob() {
        return job;
    }

    public void setJob(Long job) {
        this.job = job;
    }

    public Long getExecutionId() {
        return executionId;
    }

    public void setExecutionId(Long executionId) {
        this.executionId = executionId;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ExecutionId)) return false;
        ExecutionId that = (ExecutionId) o;
        return Objects.equals(getJob(), that.getJob()) &&
                Objects.equals(getExecutionId(), that.getExecutionId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getJob(), getExecutionId());
    }
}

