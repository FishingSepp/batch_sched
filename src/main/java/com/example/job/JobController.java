package com.example.job;

import com.example.job.entities.Job;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;
import java.util.List;
import io.swagger.annotations.*;

@RestController
@RequestMapping("/job")
public class JobController {

    @Autowired
    private JobRepository jobRepository;

    @GetMapping
    @ApiOperation(value = "Get all jobs", notes = "Gets all existing jobs")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Jobs received successfully"),
            @ApiResponse(code = 404, message = "Resource not found")
    })
    public List<Job> getAllJobs() {
        return jobRepository.findAll();
    }


    @GetMapping("/{id}")
    @ApiOperation(value = "Gets job by ID", notes = "Gets a job with the given ID")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Job received successfully"),
            @ApiResponse(code = 404, message = "Resource not found")
    })
    public ResponseEntity<Job> getJobById(@PathVariable Long id) {
        Optional<Job> jobOptional = jobRepository.findById(id);
        if (jobOptional.isPresent()) {
            return new ResponseEntity<>(jobOptional.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


    @PostMapping
    @ApiOperation(value = "Creates job by ID", notes = "Creates a job with the given ID")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Job created successfully")
    })
    public ResponseEntity<Job> createJob(@Validated @RequestBody Job job) {
        Job createdJob = jobRepository.save(job);
        return new ResponseEntity<>(createdJob, HttpStatus.CREATED);
    }


    @PutMapping("/{jid}")
    @ApiOperation(value = "Edit job by ID", notes = "Edits a job with the given ID")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Job edited successfully"),
            @ApiResponse(code = 404, message = "Resource not found")
    })
    public ResponseEntity<Job> updateJob(@PathVariable("jid") Long jid, @Validated @RequestBody Job job) {
        Job existingJob = jobRepository.findById(jid)
                .orElseThrow(() -> new JobNotFoundException("Job not found with id: " + jid));
        existingJob.setName(job.getName());
        existingJob.setDescription(job.getDescription());
        existingJob.setRepeated(job.isRepeated());
        existingJob.setStatus(job.isStatus());
        existingJob.setStartDate(job.getStartDate());
        existingJob.setEndDate(job.getEndDate());
        existingJob.setNextDate(job.getNextDate());
        Job updatedJob = jobRepository.save(existingJob);
        return new ResponseEntity<>(updatedJob, HttpStatus.OK);
    }


    @DeleteMapping("/{jid}")
    public ResponseEntity<Void> deleteJob(@PathVariable("jid") Long jid) {
        Job job = jobRepository.findById(jid)
                .orElseThrow(() -> new JobNotFoundException("Job not found with id: " + jid));
        jobRepository.delete(job);
        return ResponseEntity.noContent().build();
    }

}
