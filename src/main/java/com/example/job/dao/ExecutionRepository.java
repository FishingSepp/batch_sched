package com.example.job.dao;

import com.example.job.domain.Execution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;


@Repository
public interface ExecutionRepository extends JpaRepository<Execution, Long> {
    @Transactional
    @Query("SELECT e FROM Execution e WHERE e.job.id = :jobId")
    List<Execution> findByJobJid(@Param("jobId") Long jobId);

}

