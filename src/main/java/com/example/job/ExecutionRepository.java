package com.example.job;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;


@Repository
public interface ExecutionRepository extends JpaRepository<Execution, Long> {
    @Transactional
    @Query("SELECT e FROM Execution e WHERE e.job.job_id = :job_id")
    List<Execution> findByJobJid(@Param("job_id") Long job_id);

}

