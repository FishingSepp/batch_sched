package com.example.job;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {
    @EntityGraph(attributePaths = "history")
    Optional<Job> findById(Long id);
}
