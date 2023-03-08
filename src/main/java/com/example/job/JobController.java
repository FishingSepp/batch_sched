package com.example.job;

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

    private final JobRepository jobRepository;
    private final ExecutionController executionController;
    private final ExecutionRepository executionRepository;

    public JobController(JobRepository jobRepository, ExecutionController executionController, ExecutionRepository executionRepository) {
        this.jobRepository = jobRepository;
        this.executionController = executionController;
        this.executionRepository = executionRepository;
    }

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
        existingJob.setStart_date(job.getStart_date());
        existingJob.setEnd_date(job.getEnd_date());
        existingJob.setNext_date(job.getNext_date());
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

    @PostMapping("/{jid}/execute")
    public ResponseEntity<Execution> executeJob(@PathVariable Long jid, @RequestBody Execution executionRequest) {
        Optional<Job> jobOptional = jobRepository.findById(jid);
        if (jobOptional.isPresent()) {
            Job job = jobOptional.get();
            // execute job logic
            // ...
            // edit parameter below to depend on outcome of job execution
            // create new execution
            Execution execution = new Execution();
            execution.setSuccess(executionRequest.getSuccess());
            execution.setExit_code(executionRequest.getExit_code());
            execution.setOutput(executionRequest.getOutput());
            execution.setJob(job);
            execution.setJobId(job.getJob_id());
            executionRepository.save(execution);
            return new ResponseEntity<>(execution, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

}
