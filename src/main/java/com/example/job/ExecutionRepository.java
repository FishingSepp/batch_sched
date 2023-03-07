package com.example.job;

import com.example.job.entities.Execution;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ExecutionRepository extends JpaRepository<Execution, Long> {
    @EntityGraph(attributePaths = "history")
    Optional<Execution> findById(Long id);
}
