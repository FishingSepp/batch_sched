package com.example.job;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;
import java.util.List;
import io.swagger.annotations.*;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;
import java.time.LocalDateTime;

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
        System.out.println("Getting all jobs...");
        return jobRepository.findAll();
    }


    @GetMapping("/{jid}")
    @ApiOperation(value = "Gets job by ID", notes = "Gets a job with the given ID")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Job received successfully"),
            @ApiResponse(code = 404, message = "Resource not found")
    })
    public ResponseEntity<Job> getJobById(@PathVariable Long jid) {
        Optional<Job> jobOptional = jobRepository.findById(jid);
        if (jobOptional.isPresent()) {
            System.out.println("Getting job with Id "+jid+"...");
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
        System.out.println("Creating job...");
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
        existingJob.setJob_script(job.getJob_script());
        existingJob.setCronExpression(job.getCronExpression());
        existingJob.setStatus(job.isStatus());
        existingJob.setStart_date(job.getStart_date());
        existingJob.setEnd_date(job.getEnd_date());
        Job updatedJob = jobRepository.save(existingJob);
        System.out.println("Editing job with Id "+jid+"...");
        return new ResponseEntity<>(updatedJob, HttpStatus.OK);
    }

    @PutMapping("/{jid}/status")
    @ApiOperation(value = "Edit job status by ID", notes = "Edits the status of a job with the given ID")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Job status edited successfully"),
            @ApiResponse(code = 404, message = "Resource not found")
    })
    public ResponseEntity<Job> updateJobStatus(@PathVariable("jid") Long jid, @Validated @RequestBody Map<String, Boolean> body) {
        Job existingJob = jobRepository.findById(jid)
                .orElseThrow(() -> new JobNotFoundException("Job not found with id: " + jid));
        Boolean newStatus = body.get("status");
        existingJob.setStatus(newStatus);
        Job updatedJob = jobRepository.save(existingJob);
        System.out.println("Editing status of job with Id "+jid+"...");
        return new ResponseEntity<>(updatedJob, HttpStatus.OK);
    }



    @DeleteMapping("/{jid}")
    public ResponseEntity<Void> deleteJob(@PathVariable("jid") Long jid) {
        Job job = jobRepository.findById(jid)
                .orElseThrow(() -> new JobNotFoundException("Job not found with id: " + jid));
        jobRepository.delete(job);
        System.out.println("Deleting job with Id "+jid+"...");
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{jid}/execute")
    public ResponseEntity<Execution> executeJob(@PathVariable Long jid, @RequestBody Execution executionRequest) {
        Optional<Job> jobOptional = jobRepository.findById(jid);
        if (jobOptional.isPresent()) {
            Job job = jobOptional.get();

            executionRequest.setStart_time(LocalDateTime.now());

            // Check if empty or null job script, if so -> success
            String command = job.getJob_script();
            if (command == null || command.trim().isEmpty()) {
                Execution execution = new Execution();
                execution.setSuccess(true);
                execution.setExit_code(0);
                execution.setOutput("No job script provided.");
                execution.setStart_time(executionRequest.getStart_time());
                execution.setEnd_time(LocalDateTime.now());
                execution.setJob(job);
                execution.setJobId(job.getJob_id());
                executionRepository.save(execution);
                return new ResponseEntity<>(execution, HttpStatus.OK);
            }

            // exec job logic
            Process process;
            int exitCode = -1;
            String output = "";
            try {
                process = Runtime.getRuntime().exec(command);
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                output = reader.lines().collect(Collectors.joining("\n"));
                exitCode = process.waitFor();

                System.out.println("Script output (jobId "+jid+"): " + output);
                System.out.println("Exit code (jobId "+jid+"): " + exitCode);

                BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                String errorOutput = errorReader.lines().collect(Collectors.joining("\n"));
                System.out.println("Error output (jobId "+jid+"): " + errorOutput);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }

            // success based on exit code?
            boolean success = exitCode == 0;

            Execution execution = new Execution();
            execution.setSuccess(success);
            execution.setExit_code(exitCode);
            execution.setOutput(output);
            execution.setStart_time(executionRequest.getStart_time());
            execution.setEnd_time(LocalDateTime.now());
            execution.setJob(job);
            execution.setJobId(job.getJob_id());
            executionRepository.save(execution);
            return new ResponseEntity<>(execution, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


}
