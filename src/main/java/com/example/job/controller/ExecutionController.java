package com.example.job.controller;

import com.example.job.domain.Execution;
import com.example.job.dao.ExecutionRepository;
import com.example.job.dao.JobRepository;
import com.example.job.service.JobService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import io.swagger.annotations.*;
import org.springframework.http.HttpStatus;

import java.util.Collections;



@RestController
@RequestMapping("/execution")
public class ExecutionController {

    private static final Logger log = LoggerFactory.getLogger(JobService.class);

    private ExecutionRepository executionRepository;
    private JobRepository jobRepository;

    public ExecutionController(ExecutionRepository executionRepository, JobRepository jobRepository) {
        this.executionRepository = executionRepository;
        this.jobRepository = jobRepository;
    }

    //took away 404 response as it would flood console for not-yet executed jobs
    @GetMapping("/{jid}")
    @ApiOperation(value = "Gets executions of a job by ID", notes = "Gets the executions of a job with the given ID")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Executions of job received successfully"),
            //@ApiResponse(code = 404, message = "Resource not found")
    })
    public ResponseEntity<List<Execution>> getExecutionsByJobId(@PathVariable Long jid) {
        List<Execution> executions = executionRepository.findByJobJid(jid);
        if (executions.isEmpty()) {
            //System.out.println("Empty response for executions of job with Id "+jid+"...");
            return ResponseEntity.ok(Collections.emptyList());
        }
        //System.out.println("Getting executions of job with Id "+jid+"...");
        log.trace("getExecutionsByJobId(): getting all executions of job with id="+jid);
        //return statement gives json format list and doesn't use the overwritten toString method
        //when execution object is returned in response entity spring auto converts objects to json by using the getters to construct the json
        return ResponseEntity.ok(executions);
    }

    @DeleteMapping("/job/{jid}")
    @ApiOperation(value = "Delete executions of a job by job ID", notes = "Deletes all executions of a job with the given job ID")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Executions deleted successfully"),
            @ApiResponse(code = 404, message = "Resource not found")
    })
    public ResponseEntity<String> deleteExecutionsOfJob(@PathVariable("jid") Long jid) {
        List<Execution> executions = executionRepository.findByJobJid(jid);

        if (executions.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("deleteExecutionsOfJob(): No executions found with jobId: " + jid);
        }

        executionRepository.deleteAll(executions);
        log.trace("deleteExecutionsOfJob(): deleting all executions of job with id="+jid);
        return ResponseEntity.noContent().build();
    }

}

