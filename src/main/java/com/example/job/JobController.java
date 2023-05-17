package com.example.job;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;
import java.util.List;
import io.swagger.annotations.*;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/job")
public class JobController {

    private static final Logger log = LoggerFactory.getLogger(JobScheduler.class);

    //final because immutable, nullsafe, test-friendly
    //since Spring4.3 dependencies are autowired automatically if it's a single constructor
    //spring team itself recommends constructor injection to be able to use final
    private final JobRepository jobRepository;
    private final JobScheduler jobScheduler;

    public JobController(JobRepository jobRepository, JobScheduler jobScheduler ) {
        this.jobRepository = jobRepository;
        this.jobScheduler = jobScheduler;
    }

    @GetMapping
    @ApiOperation(value = "Get all jobs", notes = "Gets all existing jobs")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Jobs received successfully"),
            @ApiResponse(code = 404, message = "Resource not found")
    })
    public List<Job> getAllJobs() {
        //System.out.println("Getting all jobs...");
        log.trace("getAllJobs(): getting all jobs");
        return jobRepository.findAll();
    }


    @GetMapping("/{jid}")
    @ApiOperation(value = "Gets job by ID", notes = "Gets a job with the given ID")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Job received successfully"),
            @ApiResponse(code = 404, message = "Resource not found")
    })
    //if marked as pathvariable it is expected (if not configured differently)
    //and if path variable is missing spring ~should~ throw missingpathvariableexception
    //but long instead of Long seems to be recommended
    public ResponseEntity<Job> getJobById(@PathVariable long jid) {
        Optional<Job> jobOptional = jobRepository.findById(jid);
        if (jobOptional.isPresent()) {
            //System.out.println("Getting job with Id "+jid+"...");
            log.trace("getJobById(): getting job: id="+jid);
            return new ResponseEntity<>(jobOptional.get(), HttpStatus.OK);
        }
        log.trace("getJobById(): job not found for id="+jid);
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }


    @PostMapping
    @ApiOperation(value = "Creates job", notes = "Creates a new job")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Job created successfully")
    })
    public ResponseEntity<Job> createJob(@Validated @RequestBody Job job) {
        Job createdJob = jobRepository.save(job);
        //System.out.println("Creating job...");
        log.trace("createJob(): creating new job:"+createdJob);
        return new ResponseEntity<>(createdJob, HttpStatus.CREATED);
    }


    @PutMapping("/{jid}")
    @ApiOperation(value = "Update job by ID", notes = "Updates a job with the given ID")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Job updated successfully"),
            @ApiResponse(code = 404, message = "Resource not found")
    })
    public ResponseEntity<Job> updateJob(@PathVariable("jid") long jid, @Validated @RequestBody Job job) {
        Job existingJob = jobRepository.findById(jid)
                .orElseThrow(() -> new JobNotFoundException("updateJob(): finding job: id=" + jid));
        existingJob.setName(job.getName());
        existingJob.setDescription(job.getDescription());
        existingJob.setCommand(job.getCommand());
        existingJob.setCronExpression(job.getCronExpression());
        existingJob.setStatus(job.isStatus());
        existingJob.setStartDate(job.getStartDate());
        existingJob.setEndDate(job.getEndDate());
        Job updatedJob = jobRepository.save(existingJob);
        //System.out.println("Editing job with Id "+jid+"...");
        log.trace("updateJob(): updating job: id="+jid);
        return new ResponseEntity<>(updatedJob, HttpStatus.OK);
    }

    //hibernate would need to be configured for dirty checking to only write the status field then
    //still keeping this for understandability etc.?
    //not certain tbh
    @PutMapping("/{jid}/status")
    @ApiOperation(value = "Update job status by ID", notes = "Updates the status of a job with the given ID")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Job status updated successfully"),
            @ApiResponse(code = 404, message = "Resource not found")
    })
    public ResponseEntity<Job> updateJobStatus(@PathVariable("jid") long jid, @RequestBody Boolean newStatus) {
        //either custom: Job existingJob = jobRepository.findById(jid)
        //                .orElseThrow(() -> new JobNotFoundException("Job not found with id: " + jid));
        //or new RuntimeException (but would give misleading 500 internal server error
        //or response entity with 404 that misses detailed info
        Job existingJob = jobRepository.findById(jid)
                .orElseThrow(() -> new JobNotFoundException("updateStatusJob(): finding job: id=" + jid));
        existingJob.setStatus(newStatus);
        Job updatedJob = jobRepository.save(existingJob);
        //System.out.println("Editing status of job with Id "+jid+"...");
        log.trace("updateJobStatus(): updating status of job: id="+jid);
        return new ResponseEntity<>(updatedJob, HttpStatus.OK);
    }

    @DeleteMapping("/{jid}")
    @ApiOperation(value = "Delete job by ID", notes = "Deletes the job with the given ID")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Job deleted successfully"),
            @ApiResponse(code = 404, message = "Job not found")
    })
    public ResponseEntity<Void> deleteJob(@PathVariable("jid") long jid) {
        Job job = jobRepository.findById(jid)
                .orElseThrow(() -> new JobNotFoundException("deleteJob(): finding job: id=" + jid));
        jobRepository.delete(job);
        //System.out.println("Deleting job with Id "+jid+"...");
        log.trace("deleteJob(): deleting job: id="+jid);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{jid}/execute")
    @ApiOperation(value = "Execute job by ID", notes = "Executes the job with the given ID")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Job executed successfully"),
            @ApiResponse(code = 404, message = "Job not found")
    })
    public ResponseEntity<Execution> executeJob(@PathVariable long jid) {
        try {
            Execution execution = jobScheduler.executeJob(jid);
            return new ResponseEntity<>(execution, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

}
